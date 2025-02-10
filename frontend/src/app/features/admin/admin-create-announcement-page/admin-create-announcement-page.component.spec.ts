import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminCreateAnnouncementPageComponent } from './admin-create-announcement-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { of } from 'rxjs';
import { PostService, TagService, LinkKey, Tag, Role } from '@shared/index';
import {
  ImageService,
  HateoasLinksService,
  UserSessionService,
  ToastService,
} from '@core/index';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { fakeAsync, flush } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AdminCreateAnnouncementPageComponent', () => {
  let component: AdminCreateAnnouncementPageComponent;
  let fixture: ComponentFixture<AdminCreateAnnouncementPageComponent>;

  // Create spy objects for the services
  let mockPostService: jasmine.SpyObj<PostService>;
  let mockTagService: jasmine.SpyObj<TagService>;
  let mockImageService: jasmine.SpyObj<ImageService>;
  let mockLinkService: jasmine.SpyObj<HateoasLinksService>;
  let mockUserSessionService: jasmine.SpyObj<UserSessionService>;
  let mockToastService: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    // Create spies
    mockPostService = jasmine.createSpyObj('PostService', ['createPost']);
    mockTagService = jasmine.createSpyObj('TagService', [
      'createTag',
      'getTag',
    ]);
    mockImageService = jasmine.createSpyObj('ImageService', ['createImage']);
    mockLinkService = jasmine.createSpyObj('HateoasLinksService', ['getLink']);
    mockUserSessionService = jasmine.createSpyObj('UserSessionService', [
      'getCurrentUser',
      'getCurrentUserValue',
    ]);
    mockToastService = jasmine.createSpyObj('ToastService', ['showToast']);

    // Setup return values for dependencies
    // Return a dummy announcements channel link
    mockLinkService.getLink.and.callFake((key: string) => {
      if (key === LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL) {
        return '/api/channels/announcements';
      }
      return '';
    });
    // Return a dummy user
    mockUserSessionService.getCurrentUser.and.returnValue(
      of({
        email: 'test@example.com',
        name: 'Test',
        surname: 'User',
        darkMode: false,
        phoneNumber: '1234567890',
        identification: 123456,
        creationDate: new Date(),
        language: 'en',
        userRole: 'ADMIN',
        userRoleEnum: Role.ADMINISTRATOR,
        userRoleDisplay: 'Admin',
        image: 'http://example.com/avatar.png',
        self: '/api/users/1',
      }),
    );
    // Simulate a successful post creation
    mockPostService.createPost.and.returnValue(of(''));
    // Simulate that no image is uploaded (i.e. image service returns null)
    mockImageService.createImage.and.returnValue(of(null));
    // For tagService.createTag, simulate creation by returning a location string
    mockTagService.createTag.and.returnValue(of('/api/tags/newTag'));
    // For tagService.getTag, return a dummy Tag with a self link
    mockTagService.getTag.and.returnValue(
      of({ name: 'NewTag', self: '/api/tags/newTag' }),
    );

    await TestBed.configureTestingModule({
      declarations: [AdminCreateAnnouncementPageComponent],
      imports: [ReactiveFormsModule, TranslateModule.forRoot()],
      providers: [
        { provide: PostService, useValue: mockPostService },
        { provide: TagService, useValue: mockTagService },
        { provide: ImageService, useValue: mockImageService },
        { provide: HateoasLinksService, useValue: mockLinkService },
        { provide: UserSessionService, useValue: mockUserSessionService },
        { provide: ToastService, useValue: mockToastService },
        TranslateService,
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminCreateAnnouncementPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and initialize form', () => {
    expect(component).toBeTruthy();
    expect(component.announcementForm).toBeDefined();

    // Check initial form values
    const formValue = component.announcementForm.value;
    expect(formValue.subject).toBe('');
    expect(formValue.message).toBe('');
    expect(formValue.imageFile).toBeNull();
    expect(formValue.tags).toEqual([]);
  });

  it('should add a tag and update the tags control', () => {
    const tag: Tag = { name: 'Event', self: null };
    component.addTagToApplied(tag);
    // The appliedTags array should now have one tag.
    expect(component.appliedTags.length).toBe(1);
    // The form control "tags" is updated with the self links (here, still null)
    expect(component.announcementForm.get('tags')?.value).toEqual([null]);

    // Add the same tag again: should trigger a toast warning.
    component.addTagToApplied(tag);
    expect(mockToastService.showToast).toHaveBeenCalled();
  });

  it('should remove a tag and update the tags control', () => {
    const tag: Tag = { name: 'Event', self: null };
    component.addTagToApplied(tag);
    expect(component.appliedTags.length).toBe(1);
    component.removeTag(tag);
    expect(component.appliedTags.length).toBe(0);
    expect(component.announcementForm.get('tags')?.value).toEqual([]);
  });

  it('should update image control on file change', () => {
    // Create a fake File object
    const file = new File(['dummy content'], 'example.png', {
      type: 'image/png',
    });
    const event = { target: { files: [file] } };

    component.onFileChange(event);
    expect(component.announcementForm.get('imageFile')?.value).toBe(file);
  });

  it('should submit the announcement and create post (valid scenario)', fakeAsync(() => {
    // 1. Set up the form with valid values.
    component.announcementForm.setValue({
      subject: 'Announcement Title',
      message: 'This is the announcement message.',
      imageFile: null,
      tags: ['dummy'], // Dummy value so that the form is valid.
    });

    // 2. Set applied tags: one new and one existing.
    component.appliedTags = [
      { name: 'NewTag', self: null },
      { name: 'ExistingTag', self: '/api/tags/existing' },
    ];

    // 3. Prevent the form from being reset (so we can check the patched value).
    spyOn<any>(component, 'resetForm').and.stub();
    // 4. Set up mocks for tag and post services.
    mockTagService.createTag.and.returnValue(of('/api/tags/newTag'));
    mockTagService.getTag.and.returnValue(
      of({ name: 'NewTag', self: '/api/tags/newTag' }),
    );
    mockPostService.createPost.and.returnValue(of('/api/posts/123'));

    // 5. Call onSubmit (which now should pass the validity check).
    component.onSubmit();

    // 6. Flush asynchronous operations.
    flush();

    // 7. Check that the form's "tags" control was patched with the correct tag URLs.
    const updatedTags = component.announcementForm.get('tags')?.value;
    expect(updatedTags).toEqual(['/api/tags/existing', '/api/tags/newTag']);

    // 8. Verify that tag service methods were called.
    expect(mockTagService.createTag).toHaveBeenCalledWith('NewTag');
    expect(mockTagService.getTag).toHaveBeenCalledWith('/api/tags/newTag');

    // 9. Verify that postService.createPost was called.
    expect(mockPostService.createPost).toHaveBeenCalled();
  }));

  it('should not submit when form is invalid', () => {
    // Invalidate the form by leaving required fields empty.
    component.announcementForm.setValue({
      subject: '',
      message: '',
      imageFile: null,
      tags: [],
    });
    spyOn(component.announcementForm, 'markAllAsTouched');
    component.onSubmit();
    expect(component.announcementForm.markAllAsTouched).toHaveBeenCalled();
    expect(mockPostService.createPost).not.toHaveBeenCalled();
  });
});
