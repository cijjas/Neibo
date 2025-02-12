import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FeedCreatePostPageComponent } from './feed-create-post-page.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { TagService, LinkKey, Role, Tag } from '@shared/index';
import {
  HateoasLinksService,
  UserSessionService,
  ImageService,
  ToastService,
  AuthService,
} from '@core/index';
import { PostService } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

/** Fake translate pipe to satisfy usage of "| translate" in the template. */
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

export class FakeTranslateService {
  currentLang = 'en';

  setDefaultLang(lang: string): void {}
  use(lang: string): Observable<string> {
    this.currentLang = lang;
    return of(lang);
  }
  instant(key: string): string {
    return key;
  }
  getTranslation(lang: string): Observable<any> {
    return of({});
  }
}

describe('FeedCreatePostPageComponent', () => {
  let component: FeedCreatePostPageComponent;
  let fixture: ComponentFixture<FeedCreatePostPageComponent>;

  // Service spies
  const postServiceSpy = jasmine.createSpyObj('PostService', ['createPost']);
  const tagServiceSpy = jasmine.createSpyObj('TagService', [
    'getTags',
    'createTag',
    'getTag',
  ]);
  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['createImage']);
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const userSessionSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentRole',
    'getCurrentUserValue',
    'getCurrentUser',
  ]);
  const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
  const authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // We’ll store queryParams in an object we can manipulate per test.
  let mockQueryParams: any = {};
  // The ActivatedRoute that returns an observable of the query params object.
  const fakeActivatedRoute = {
    queryParams: of(mockQueryParams),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [FeedCreatePostPageComponent, FakeTranslatePipe],
      providers: [
        FormBuilder,
        { provide: TranslateService, useClass: FakeTranslateService },

        { provide: PostService, useValue: postServiceSpy },
        { provide: TagService, useValue: tagServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: UserSessionService, useValue: userSessionSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        { provide: TranslateService, useValue: { instant: (k: string) => k } },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(
        FeedCreatePostPageComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    // By default, the user is NEIGHBOR (not worker).
    userSessionSpy.getCurrentRole.and.returnValue(Role.NEIGHBOR);
    userSessionSpy.getCurrentUser.and.returnValue(of({ self: 'user_self' }));

    // Stub link lookups
    linkServiceSpy.getLink.and.callFake((key: string) => {
      switch (key) {
        case LinkKey.NEIGHBORHOOD_FEED_CHANNEL:
          return 'feed_channel_url';
        case LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL:
          return 'announcements_channel_url';
        case LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL:
          return 'complaints_channel_url';
        case LinkKey.NEIGHBORHOOD_TAGS:
          return 'tags_url';
        default:
          return '';
      }
    });

    // By default, tagService.getTags returns one tag with 2 total pages
    tagServiceSpy.getTags.and.returnValue(
      of({ tags: [{ name: 'Tag1', self: 'tag1_url' }], totalPages: 2 }),
    );

    // Reset or define the default query params.
    mockQueryParams = {}; // empty
  });

  // 1) Non-worker initialization => fetches tags
  it('should initialize, fetch tags if not worker', () => {
    fixture = TestBed.createComponent(FeedCreatePostPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit

    expect(tagServiceSpy.getTags).toHaveBeenCalledWith('tags_url', {
      page: component.currentPage,
      size: component.pageSize,
    });
    expect(component.notWorker).toBeTrue();
  });

  // 2) Worker initialization => does NOT fetch tags
  it('should skip tag fetching if user is worker', () => {
    // Now we set the role to WORKER before we create the component
    userSessionSpy.getCurrentRole.and.returnValue(Role.WORKER);

    // Reset any calls from previous tests
    tagServiceSpy.getTags.calls.reset();

    // Now create the component
    fixture = TestBed.createComponent(FeedCreatePostPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit

    // Because user is worker, fetchTags should not be called
    expect(tagServiceSpy.getTags).not.toHaveBeenCalled();
    expect(component.notWorker).toBeFalse();
  });

  // 3) Set channel in query param => update title
  it('should set channel title if channel param is set', () => {
    mockQueryParams = { inChannel: 'complaints_channel_url' };
    fakeActivatedRoute.queryParams = of(mockQueryParams);
    fixture = TestBed.createComponent(FeedCreatePostPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.channel).toEqual('complaints_channel_url');
  });

  // 4) Creating a custom tag => invalid vs. valid
  it('should show an error toast if custom tag is invalid, otherwise add it', () => {
    fixture = TestBed.createComponent(FeedCreatePostPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.appliedTags.length).toBe(0);

    // Invalid tag: "12345"
    component.createCustomTag('12345');
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'FEED-CREATE-POST-PAGE.INVALID_TAG_FORMAT_USE_LETTERS_ONLY',
      'warning',
    );
    expect(component.appliedTags.length).toBe(0);

    toastServiceSpy.showToast.calls.reset();

    // Valid tag: "Hello Tag" => CamelCase => "HelloTag"
    component.createCustomTag('Hello Tag');
    expect(component.appliedTags.length).toBe(1);
    expect(component.appliedTags[0].name).toBe('HelloTag');
    expect(toastServiceSpy.showToast).not.toHaveBeenCalled();
  });

  // 5) Removing a tag from appliedTags
  it('should remove a tag from applied tags when removeTag is called', () => {
    fixture = TestBed.createComponent(FeedCreatePostPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    component.appliedTags = [
      { name: 'Tag1', self: 'tag1_url' },
      { name: 'Tag2', self: 'tag2_url' },
    ];

    component.removeTag({ name: 'Tag2', self: 'tag2_url' });
    expect(component.appliedTags.length).toBe(1);
    expect(component.appliedTags[0].name).toBe('Tag1');
  });
});
