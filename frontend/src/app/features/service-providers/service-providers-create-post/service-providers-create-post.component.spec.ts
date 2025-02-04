import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
  waitForAsync,
} from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

import { ServiceProvidersCreatePostComponent } from './service-providers-create-post.component';
import { PostService } from '@shared/index';
import {
  ImageService,
  ToastService,
  UserSessionService,
  HateoasLinksService,
} from '@core/index';
import { TranslateService } from '@ngx-translate/core';

// ----- Fake Translate Pipe -----
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// ----- Create a complete dummy ActivatedRoute -----
const fakeActivatedRoute = {
  queryParams: of({ inChannel: 'channelTest', forWorker: 'worker123' }),
} as unknown as ActivatedRoute;

// ----- Dummy user (minimal) -----
const dummyUser = { self: 'user_self' };

// ----- Service Spies / Stubs -----
const postServiceSpy = jasmine.createSpyObj('PostService', ['createPost']);
const imageServiceSpy = jasmine.createSpyObj('ImageService', ['createImage']);
const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showToast']);
const userSessionSpy = jasmine.createSpyObj('UserSessionService', [
  'getCurrentUser',
]);
const hateoasLinksServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
  'getLink',
]);
const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

// Stub for user session: returns a dummy user.
userSessionSpy.getCurrentUser.and.returnValue(of(dummyUser));

// Stub for link lookup: when asked for 'USER_SELF', return dummy value.
hateoasLinksServiceSpy.getLink.and.callFake((key: string) => {
  if (key === 'USER_SELF') {
    return 'user_self';
  }
  return '';
});

describe('ServiceProvidersCreatePostComponent', () => {
  let component: ServiceProvidersCreatePostComponent;
  let fixture: ComponentFixture<ServiceProvidersCreatePostComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ServiceProvidersCreatePostComponent, FakeTranslatePipe],
      imports: [ReactiveFormsModule],
      providers: [
        FormBuilder,
        { provide: PostService, useValue: postServiceSpy },
        { provide: ImageService, useValue: imageServiceSpy },
        { provide: ToastService, useValue: toastServiceSpy },
        { provide: UserSessionService, useValue: userSessionSpy },
        { provide: HateoasLinksService, useValue: hateoasLinksServiceSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        {
          provide: TranslateService,
          useValue: { instant: (key: string) => key },
        },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersCreatePostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: Initialization – verify that the reactive form is built and query parameters are read.
  it('should initialize the form and set channel and workerId from query params', () => {
    expect(component.createWrokerPostForm).toBeDefined();
    // Our fakeActivatedRoute provides queryParams { inChannel: 'channelTest', forWorker: 'worker123' }.
    expect(component.channel).toEqual('channelTest');
    expect(component.workerId).toEqual('worker123');
  });

  // Test 2: onFileChange – simulate file selection and verify form patching and preview.
  it('should add new image on file change', fakeAsync(() => {
    // Create a valid File instance.
    const dummyFile = new File(['dummy content'], 'test.png', {
      type: 'image/png',
    });
    // Use DataTransfer to create a valid FileList.
    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(dummyFile);
    const inputElement = document.createElement('input');
    inputElement.type = 'file';
    inputElement.files = dataTransfer.files;

    const dummyResult = 'data:image/png;base64,dummy';
    // Create a fake FileReader that returns a dummy data URL.
    class FakeFileReader {
      public result: any;
      public onload: any;
      readAsDataURL(file: File) {
        this.result = dummyResult;
        this.onload({ target: { result: dummyResult } });
      }
    }
    spyOn(window as any, 'FileReader').and.returnValue(new FakeFileReader());

    const event = { target: inputElement } as unknown as Event;
    component.onFileChange(event);
    tick();

    expect(component.createWrokerPostForm.value.imageFile).toEqual(dummyFile);
    expect(component.imagePreviewUrl).toEqual(dummyResult);
  }));

  // Test 3: onSubmit – simulate a valid form submission (with image) and verify navigation and toast.
  it('should submit the form and navigate on success', fakeAsync(() => {
    // Patch the form with valid values.
    component.createWrokerPostForm.patchValue({
      title: 'Test Title',
      body: 'Test Body',
      channel: '',
      user: '',
    });
    // Also, set an image file.
    const dummyFile = new File(['dummy content'], 'test.png', {
      type: 'image/png',
    });
    component.createWrokerPostForm.patchValue({ imageFile: dummyFile });

    // Stub imageService.createImage to return an observable of a dummy URL.
    imageServiceSpy.createImage.and.returnValue(
      of('http://dummyimage.com/img.png'),
    );
    // Stub postService.createPost to return an observable (simulate a successful creation).
    postServiceSpy.createPost.and.returnValue(of('post_created_url'));

    component.onSubmit();
    tick();

    // Verify that the toastService is called with success message.
    expect(toastServiceSpy.showToast).toHaveBeenCalledWith(
      'SERVICE-PROVIDERS-CREATE-POST.YOUR_POST_WAS_CREATED_SUCCESSFULLY',
      'success',
    );
    // Verify that the router navigates to the expected URL.
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/services', 'profile', 'worker123'],
      { queryParams: { tab: 'posts' } },
    );
  }));
});
