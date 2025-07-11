import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FeedPostDetailPageComponent } from './feed-post-detail-page.component';
import { ActivatedRoute } from '@angular/router';
import { Observable, of } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { Post, Role } from '@shared/index';
import { TranslateService } from '@ngx-translate/core';

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
}

// Create a dummy post with all required properties. Use a cast to bypass type issues.
const dummyPost: Post = {
  title: 'Test Post',
  body: 'This is a test post body',
  createdAt: new Date(),
  image: 'post_image_url',
  channel: 'post_channel',
  likeCount: 0,
  comments: 'comments_link',
  author: {
    email: 'author@example.com',
    name: 'Author',
    surname: 'User',
    darkMode: false,
    phoneNumber: '0000000000',
    identification: 1,
    creationDate: new Date(),
    language: 'en',
    userRole: Role.NEIGHBOR,
    userRoleEnum: Role.NEIGHBOR,
    userRoleDisplay: 'Neighbor',
    image: 'author_image_url',
    self: 'author_self',
  },
  self: 'post_self',
} as any as Post;

describe('FeedPostDetailPageComponent', () => {
  let component: FeedPostDetailPageComponent;
  let fixture: ComponentFixture<FeedPostDetailPageComponent>;

  // Provide a fake ActivatedRoute that emits route data containing the dummy post.
  const fakeActivatedRoute = {
    data: of({ post: dummyPost }),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FeedPostDetailPageComponent],
      providers: [
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: TranslateService, useClass: FakeTranslateService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the template to avoid processing any pipes or unknown elements.
      .overrideTemplate(
        FeedPostDetailPageComponent,
        `<div>Dummy Template</div>`,
      )
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedPostDetailPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should assign the resolved post from route data on init', () => {
    expect(component.post).toEqual(dummyPost);
  });
});
