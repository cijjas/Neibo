import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FeedPostPreviewComponent } from './feed-post-preview.component';
import { ActivatedRoute, Router } from '@angular/router';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { of, Subscription } from 'rxjs';

// Import service types (adjust paths as needed)
import {
  ImageService,
  HateoasLinksService,
  UserSessionService,
} from '@core/index';
import { LikeService, TagService, Post, Roles, LinkKey } from '@shared/index';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// Create a dummy post object with all required fields.
const dummyPost: Post = {
  title: 'Sample Post',
  body: 'This is the post body.',
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
    userRole: Roles.NEIGHBOR,
    userRoleEnum: Roles.NEIGHBOR,
    userRoleDisplay: 'Neighbor',
    image: 'author_image_url',
    self: 'author_self',
  },
  self: 'post_self',
};

describe('FeedPostPreviewComponent', () => {
  let component: FeedPostPreviewComponent;
  let fixture: ComponentFixture<FeedPostPreviewComponent>;

  // Spy stubs for each service.
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['fetchImage']);
  const tagServiceSpy = jasmine.createSpyObj('TagService', ['getTags']);
  const likeServiceSpy = jasmine.createSpyObj('LikeService', [
    'getLikes',
    'createLike',
    'deleteLike',
  ]);
  const userSessionSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
  ]);
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Provide a route with some default query params.
  const fakeActivatedRoute = {
    snapshot: { queryParams: { inChannel: 'channel_url' } },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FeedPostPreviewComponent, FakeTranslatePipe],
      providers: [
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: TagService, useValue: tagServiceSpy },
        { provide: LikeService, useValue: likeServiceSpy },
        { provide: UserSessionService, useValue: userSessionSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(FeedPostPreviewComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedPostPreviewComponent);
    component = fixture.componentInstance;
    // Provide the required @Input() post.
    component.post = { ...dummyPost };

    // Stub out service return values needed for ngOnInit.
    linkServiceSpy.getLink.and.callFake((key: string) => {
      if (key === LinkKey.NEIGHBORHOOD_TAGS) return 'tag_link';
      if (key === LinkKey.NEIGHBORHOOD_LIKES) return 'likes_link';
      return '';
    });
    imageServiceSpy.fetchImage.and.returnValue(
      of({ safeUrl: 'safe_url', isFallback: false }),
    );
    tagServiceSpy.getTags.and.returnValue(
      of({ tags: [{ name: 'Tag1', self: 'tag1_url' }] }),
    );
    likeServiceSpy.getLikes.and.returnValue(of({ likes: [] }));
    userSessionSpy.getCurrentUser.and.returnValue(of({ self: 'user_self' }));

    fixture.detectChanges(); // triggers ngOnInit
  });

  // 1) Initialization
  it('should fetch tags, images, and like status on init', () => {
    // Verify that the route’s query param inChannel is stored
    expect(component.inChannel).toEqual('channel_url');

    // Tag fetch
    expect(tagServiceSpy.getTags).toHaveBeenCalledWith('tag_link', {
      onPost: 'post_self',
    });
    // Image fetch for post and author
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('post_image_url');
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('author_image_url');
    // Like fetch
    expect(likeServiceSpy.getLikes).toHaveBeenCalledWith('likes_link', {
      onPost: 'post_self',
      likedBy: 'user_self',
    });
  });

  // 2) Toggling like from not liked => calls likePost
  it('should call likePost if hasLiked is false when toggleLike is invoked', () => {
    const compAny = component as any;
    spyOn(compAny, 'likePost');
    spyOn(compAny, 'unlikePost');

    component.hasLiked = false;
    component.toggleLike();
    expect(compAny.likePost).toHaveBeenCalled();
    expect(compAny.unlikePost).not.toHaveBeenCalled();
  });

  // 3) Toggling like from liked => calls unlikePost
  it('should call unlikePost if hasLiked is true when toggleLike is invoked', () => {
    const compAny = component as any;
    spyOn(compAny, 'likePost');
    spyOn(compAny, 'unlikePost');

    component.hasLiked = true;
    component.toggleLike();
    expect(compAny.unlikePost).toHaveBeenCalled();
    expect(compAny.likePost).not.toHaveBeenCalled();
  });

  // 4) Filter by Tag
  it('should merge the new withTag param and navigate', () => {
    component.filterByTag('SampleTag');
    expect(routerSpy.navigate).toHaveBeenCalledWith([], {
      relativeTo: fakeActivatedRoute,
      queryParams: { inChannel: 'channel_url', withTag: 'SampleTag' }, // Keep existing params
      queryParamsHandling: 'merge',
    });
  });

  // 5) Destroy
  it('should clear interval and unsubscribe on destroy', () => {
    // Spy on clearInterval and the subscriptions object
    spyOn(window, 'clearInterval');
    spyOn(component['subscriptions'], 'unsubscribe');

    component.ngOnDestroy();
    expect(window.clearInterval).toHaveBeenCalled();
    expect(component['subscriptions'].unsubscribe).toHaveBeenCalled();
  });
});
