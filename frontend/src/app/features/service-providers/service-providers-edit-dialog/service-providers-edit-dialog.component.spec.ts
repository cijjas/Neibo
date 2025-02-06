import {
  ComponentFixture,
  TestBed,
  waitForAsync,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

import { ServiceProvidersEditDialogComponent } from './service-providers-edit-dialog.component';
import { ImageService } from '@core/index';

describe('ServiceProvidersEditDialogComponent', () => {
  let component: ServiceProvidersEditDialogComponent;
  let fixture: ComponentFixture<ServiceProvidersEditDialogComponent>;
  let imageServiceSpy: jasmine.SpyObj<ImageService>;

  beforeEach(waitForAsync(() => {
    imageServiceSpy = jasmine.createSpyObj('ImageService', ['createImage']);

    TestBed.configureTestingModule({
      imports: [
        FormsModule, 
        TranslateModule.forRoot(), 
      ],
      declarations: [ServiceProvidersEditDialogComponent],
      providers: [{ provide: ImageService, useValue: imageServiceSpy }],
      schemas: [NO_ERRORS_SCHEMA], 
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Test 1: ngOnChanges should update internal fields when worker input changes
  it('should update internal fields when worker input changes', () => {
    const dummyWorker = {
      businessName: 'Test Business',
      bio: 'Some bio text',
      phoneNumber: '123456',
      address: 'Test Address',
      backgroundImage: 'bg.jpg',
    };

    const changes = {
      worker: {
        currentValue: dummyWorker,
        previousValue: null,
        firstChange: true,
        isFirstChange: () => true,
      },
    };

    component.ngOnChanges(changes);

    expect(component.businessName).toBe('Test Business');
    expect(component.bio).toBe('Some bio text');
    expect(component.phoneNumber).toBe('123456');
    expect(component.address).toBe('Test Address');
    expect(component.existingBackgroundImage).toBe('bg.jpg');
  });

  // Test 2: closeEditDialog should emit closeDialog event
  it('should emit closeDialog event when closeEditDialog is called', () => {
    spyOn(component.closeDialog, 'emit');
    component.closeEditDialog();
    expect(component.closeDialog.emit).toHaveBeenCalled();
  });

  // Test 3: onFileSelected should set imageFile when a file is selected
  it('should set imageFile when onFileSelected is called', () => {
    // Create a dummy file.
    const dummyFile = new File(['dummy content'], 'test.png', {
      type: 'image/png',
    });

    const inputElement = document.createElement('input');
    inputElement.type = 'file';

    const dataTransfer = new DataTransfer();
    dataTransfer.items.add(dummyFile);
    inputElement.files = dataTransfer.files;

    const event = { target: inputElement } as unknown as Event;
    component.onFileSelected(event);

    expect(component.imageFile).toEqual(dummyFile);
  });

  // Test 4: submitEditProfileForm should emit saveProfile with updated data and close the dialog when no image is selected
  it('should emit saveProfile with updated data and close dialog when no image is selected', () => {
    spyOn(component.saveProfile, 'emit');
    spyOn(component, 'closeEditDialog');

    component.businessName = 'Business';
    component.bio = 'Bio text';
    component.phoneNumber = '999999';
    component.address = 'Test Address';
    component.imageFile = null; 

    component.submitEditProfileForm();

    expect(component.saveProfile.emit).toHaveBeenCalledWith({
      businessName: 'Business',
      bio: 'Bio text',
      phoneNumber: '999999',
      address: 'Test Address',
    });

    expect(component.closeEditDialog).toHaveBeenCalled();
  });

  // Test 5: submitEditProfileForm should upload image, then emit updated DTO with backgroundImage
  it('should upload image, then emit updated DTO with backgroundImage', fakeAsync(() => {
    spyOn(component.saveProfile, 'emit');
    spyOn(component, 'closeEditDialog');

    component.businessName = 'Biz';
    component.bio = 'Bio text';
    component.phoneNumber = '999999';
    component.address = 'Test Address';

    const dummyFile = new File(['dummy content'], 'test.png', {
      type: 'image/png',
    });
    component.imageFile = dummyFile;

    imageServiceSpy.createImage.and.returnValue(
      of('http://image-url.com/test.png'),
    );

    component.submitEditProfileForm();
    tick(); 

    expect(imageServiceSpy.createImage).toHaveBeenCalledWith(dummyFile);
    expect(component.saveProfile.emit).toHaveBeenCalledWith({
      businessName: 'Biz',
      bio: 'Bio text',
      phoneNumber: '999999',
      address: 'Test Address',
      backgroundImage: 'http://image-url.com/test.png',
    });

    expect(component.closeEditDialog).toHaveBeenCalled();
  }));
});
