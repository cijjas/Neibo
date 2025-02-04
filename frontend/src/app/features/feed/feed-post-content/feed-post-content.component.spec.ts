import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { FeedPostContentComponent } from './feed-post-content.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

// Import types (adjust paths as needed)
import { Post, Tag, Comment, Role, LinkKey } from '@shared/index';
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

// Dummy post to assign to the component input.
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
  // Return dummy safe URLs for both post and author images.
  imageServiceSpy.fetchImage.and.returnValue(
    of({ safeUrl: 'dummy_safe_url', isFallback: false }),
  );

  const commentServiceSpy = jasmine.createSpyObj('CommentService', [
    'createComment',
    'getComments',
  ]);
  // For comment creation.
  commentServiceSpy.createComment.and.returnValue(of({ self: 'new_comment' }));
  // For fetching comments.
  commentServiceSpy.getComments.and.returnValue(
    of({ comments: [{ text: 'comment1' }], totalPages: 1, currentPage: 1 }),
  );

  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  // For tag fetching and like status
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
  // We'll assume createLike and deleteLike are not used in these tests.

  const tagServiceSpy = jasmine.createSpyObj('TagService', ['getTags']);
  tagServiceSpy.getTags.and.returnValue(
    of({ tags: [{ name: 'tag1', self: 'tag1_url' }], totalPages: 1 }),
  );

  const userSessionSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
  ]);
  userSessionSpy.getCurrentUser.and.returnValue(of({ self: 'user_self' }));

  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // ActivatedRoute stub with default queryParams.
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
    // Set the required @Input() post.
    component.post = { ...dummyPost };
    fixture.detectChanges(); // triggers ngOnInit
  });

  // Test 1: Initialization
  it('should initialize by fetching images, tags, like status, and build the comment form', () => {
    // Verify commentForm is built.
    expect(component.commentForm).toBeDefined();
    expect(component.commentForm.contains('comment')).toBeTrue();

    // Verify that imageService.fetchImage is called for both post and author.
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('post_image_url');
    expect(imageServiceSpy.fetchImage).toHaveBeenCalledWith('author_image_url');

    // Verify that tagService.getTags is called with the expected arguments.
    expect(tagServiceSpy.getTags).toHaveBeenCalledWith('tags_link', {
      onPost: 'post_self',
    });
    // Verify that likeService.getLikes is called.
    expect(likeServiceSpy.getLikes).toHaveBeenCalled();
  });

  // Test 2: Toggling Like
  it('should toggle like status by calling likePost or unlikePost', () => {
    const compAny = component as any;
    spyOn(compAny, 'likePost');
    spyOn(compAny, 'unlikePost');

    component.isLiked = false;
    component.toggleLike();
    expect(compAny.likePost).toHaveBeenCalled();
    expect(compAny.unlikePost).not.toHaveBeenCalled();

    component.isLiked = true;
    component.toggleLike();
    expect(compAny.unlikePost).toHaveBeenCalled();
  });

  // Test 3: Submitting a Comment
  it('should create a comment and refresh comments when onSubmit is called', () => {
    // Set a valid comment value.
    component.commentForm.setValue({ comment: 'Hello World' });
    // Spy on getComments.
    spyOn(component, 'getComments');
    component.onSubmit();
    expect(commentServiceSpy.createComment).toHaveBeenCalledWith(
      'comments_link',
      'Hello World',
      'user_self',
    );
    expect(component.getComments).toHaveBeenCalled();
    // After reset, the comment control should be null.
    expect(component.commentForm.value.comment).toBeNull();
  });

  // Test 4: Comment Pagination (onPageChange)
  it('should update page, update query params, and re-fetch comments on page change', () => {
    // Spy on private updateQueryParams via casting.
    const compAny = component as any;
    spyOn(compAny, 'updateQueryParams');
    spyOn(component, 'getComments');
    component.onPageChange(2);
    expect(component.currentPage).toEqual(2);
    expect(compAny.updateQueryParams).toHaveBeenCalled();
    expect(component.getComments).toHaveBeenCalledWith(2, component.pageSize);
  });

  // Test 5: Filtering by Tag
  it('should merge new tag parameter and navigate to /posts when filterByTag is called', () => {
    // Simulate a route snapshot with existing query params.
    (component as any).route = {
      snapshot: { queryParams: { page: '1', size: '10' } },
    };
    component.filterByTag('SampleTag');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/posts'], {
      queryParams: { page: '1', size: '10', withTag: 'SampleTag' },
      queryParamsHandling: 'merge',
    });
  });
});
