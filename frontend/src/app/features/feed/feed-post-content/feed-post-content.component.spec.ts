import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FeedPostContentComponent } from './feed-post-content.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

import { Post, Role, LinkKey } from '@shared/index';
import {
  CommentService,
  ImageService,
  LikeService,
  TagService,
} from '@shared/index';
import { HateoasLinksService, UserSessionService } from '@core/index';

// --- Fake Translate Pipe ---
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// Dummy post object
const dummyPost: Post = {
  title: 'Sample Post',
  body: 'This is a sample post.',
  createdAt: new Date(),
  image: 'post_image_url',
  channel: 'channel_url',
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
    userRole: '',
    userRoleEnum: Role.NEIGHBOR,
    userRoleDisplay: '',
    image: 'author_image_url',
    self: 'author_self',
  },
  self: 'post_self',
};

describe('FeedPostContentComponent', () => {
  let component: FeedPostContentComponent;
  let fixture: ComponentFixture<FeedPostContentComponent>;

  // Service stubs
  const imageServiceSpy = jasmine.createSpyObj('ImageService', ['fetchImage']);
  imageServiceSpy.fetchImage.and.returnValue(
    of({ safeUrl: 'dummy_safe_url', isFallback: false }),
  );

  const commentServiceSpy = jasmine.createSpyObj('CommentService', [
    'createComment',
    'getComments',
  ]);
  commentServiceSpy.createComment.and.returnValue(of({ self: 'new_comment' }));
  commentServiceSpy.getComments.and.returnValue(
    of({ comments: [{ text: 'comment1' }], totalPages: 1, currentPage: 1 }),
  );

  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.NEIGHBORHOOD_TAGS) return 'tags_link';
    if (key === LinkKey.NEIGHBORHOOD_LIKES) return 'likes_link';
    return '';
  });

  const likeServiceSpy = jasmine.createSpyObj('LikeService', [
    'getLikes',
    'createLike',
    'deleteLike',
  ]);
  likeServiceSpy.getLikes.and.returnValue(of({ likes: [] }));

  const tagServiceSpy = jasmine.createSpyObj('TagService', ['getTags']);
  tagServiceSpy.getTags.and.returnValue(
    of({ tags: [{ name: 'tag1', self: 'tag1_url' }], totalPages: 1 }),
  );

  const userSessionSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
    'getCurrentUserValue',
  ]);
  userSessionSpy.getCurrentUser.and.returnValue(of({ self: 'user_self' }));
  userSessionSpy.getCurrentUserValue.and.returnValue({ self: 'user_self' });

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  const fakeActivatedRoute = {
    snapshot: { queryParams: { page: '1', size: '10' } },
    queryParams: of({ page: '1', size: '10' }),
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [FeedPostContentComponent, FakeTranslatePipe],
      providers: [
        FormBuilder,
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: CommentService, useValue: commentServiceSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: LikeService, useValue: likeServiceSpy },
        { provide: TagService, useValue: tagServiceSpy },
        { provide: UserSessionService, useValue: userSessionSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    })
      .overrideTemplate(FeedPostContentComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedPostContentComponent);
    component = fixture.componentInstance;
    component.post = { ...dummyPost };
    fixture.detectChanges();
  });

  it('should create a comment and refresh comments when onSubmit is called', () => {
    spyOn(component, 'getComments'); // Ensure getComments is called

    component.commentForm.setValue({ comment: 'Hello World' });
    component.onSubmit();

    expect(commentServiceSpy.createComment).toHaveBeenCalledWith(
      'comments_link',
      'Hello World',
      'user_self',
    );

    expect(component.getComments).toHaveBeenCalled();
    expect(component.commentForm.value.comment).toBe('');
  });

  it('should initialize by fetching images, tags, like status, and build the comment form', () => {
    expect(component.commentForm).toBeDefined();
    expect(component.commentForm.contains('comment')).toBeTrue();

    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('post_image_url');
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('author_image_url');

    expect(tagServiceSpy.getTags).toHaveBeenCalledWith('tags_link', {
      onPost: 'post_self',
    });
    expect(likeServiceSpy.getLikes).toHaveBeenCalled();
  });

  it('should toggle like status by calling likePost or unlikePost', () => {
    spyOn(component as any, 'likePost');
    spyOn(component as any, 'unlikePost');

    component.isLiked = false;
    component.toggleLike();
    expect((component as any).likePost).toHaveBeenCalled();

    component.isLiked = true;
    component.toggleLike();
    expect((component as any).unlikePost).toHaveBeenCalled();
  });

  it('should update page and fetch comments on page change', () => {
    spyOn(component, 'getComments');
    component.onPageChange(2);

    expect(component.currentPage).toEqual(2);
    expect(component.getComments).toHaveBeenCalledWith(2, component.pageSize);
  });

  it('should merge new tag parameter and navigate to /posts when filterByTag is called', () => {
    component.filterByTag('SampleTag');

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/posts'], {
      queryParams: { page: '1', size: '10', withTag: 'SampleTag' },
      queryParamsHandling: 'merge',
    });
  });

  it('should clear interval and unsubscribe on destroy', () => {
    spyOn(window, 'clearInterval');
    spyOn(component['subscriptions'], 'unsubscribe');

    component.ngOnDestroy();

    expect(window.clearInterval).toHaveBeenCalled();
    expect(component['subscriptions'].unsubscribe).toHaveBeenCalled();
  });
});
