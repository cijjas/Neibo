import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FeedPageComponent } from './feed-page.component';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { map } from 'rxjs/operators';

import { PostService, LinkKey, Post } from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';

// Define a simple fake TranslateService.
class FakeTranslateService {
  currentLang = 'en';
  setDefaultLang(lang: string): void {}
  use(lang: string): any {
    return of(lang);
  }
  instant(key: string): string {
    // For testing, simply return the key or a modified version.
    return key;
  }
}

describe('FeedPageComponent - Initialization', () => {
  let component: FeedPageComponent;
  let fixture: ComponentFixture<FeedPageComponent>;

  // Create a BehaviorSubject for query parameters.
  const initialQueryParams = {
    page: '2',
    size: '10',
    inChannel: 'custom_channel',
    withStatus: 'custom_status',
    withTag: ['tag1', 'tag2'],
  };
  const queryParamsSubject = new BehaviorSubject(initialQueryParams);
  const fakeActivatedRoute = {
    queryParams: queryParamsSubject.asObservable(),
    queryParamMap: queryParamsSubject
      .asObservable()
      .pipe(map(params => ({ get: (key: string) => params[key] || null }))),
  };

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Stub HateoasLinksService (fallback values not used here).
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL)
      return 'default_channel';
    if (key === LinkKey.NONE_POST_STATUS) return 'default_status';
    return '';
  });

  // Dummy response from PostService.getPosts.
  const dummyPostsResponse = {
    posts: [
      {
        title: 'Post 1',
        body: 'Body 1',
        createdAt: new Date(),
        image: 'img1',
        channel: 'custom_channel',
        likeCount: 5,
        comments: '...',
        author: null,
        self: 'p1',
      } as Post,
    ],
    totalPages: 5,
    currentPage: 2,
  };

  const postServiceSpy = jasmine.createSpyObj('PostService', ['getPosts']);
  postServiceSpy.getPosts.and.returnValue(of(dummyPostsResponse));

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FeedPageComponent],
      providers: [
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: PostService, useValue: postServiceSpy },
        // Provide our FakeTranslateService so the component can call its methods.
        { provide: TranslateService, useClass: FakeTranslateService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the template to bypass any template-related errors (like missing pipes).
      .overrideTemplate(FeedPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize by reading query params and loading posts', () => {
    // The dummy response sets currentPage to 2, so expect that.
    expect(component.currentPage).toEqual(2);
    expect(component.pageSize).toEqual(10);

    // Verify that PostService.getPosts was called with the expected parameters.
    expect(postServiceSpy.getPosts).toHaveBeenCalledWith({
      page: 2,
      size: 10,
      inChannel: 'custom_channel',
      withStatus: 'custom_status',
      withTag: ['tag1', 'tag2'],
    });

    // Verify that the response data is stored in the component.
    expect(component.postList).toEqual(dummyPostsResponse.posts);
    expect(component.totalPages).toEqual(5);
    expect(component.loading).toBeFalse();
  });
});
