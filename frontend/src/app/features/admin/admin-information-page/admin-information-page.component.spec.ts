import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminInformationPageComponent } from './admin-information-page.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

// Mocks for Core services
import { ToastService, ConfirmationService } from '@core/index';
// Mocks for Shared services
import { ContactService, ResourceService, ImageService } from '@shared/index';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AdminInformationPageComponent', () => {
  let component: AdminInformationPageComponent;
  let fixture: ComponentFixture<AdminInformationPageComponent>;

  // Create simple spy objects for the services used in the component.
  let mockContactService: jasmine.SpyObj<ContactService>;
  let mockResourceService: jasmine.SpyObj<ResourceService>;
  let mockToastService: jasmine.SpyObj<ToastService>;
  let mockImageService: jasmine.SpyObj<ImageService>;
  let mockConfirmationService: jasmine.SpyObj<ConfirmationService>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    // Create spy objects with the needed methods.
    mockContactService = jasmine.createSpyObj('ContactService', [
      'createContact',
      'getContacts',
      'deleteContact',
    ]);
    mockResourceService = jasmine.createSpyObj('ResourceService', [
      'createResource',
      'getResources',
      'deleteResource',
    ]);
    mockToastService = jasmine.createSpyObj('ToastService', ['showToast']);
    mockImageService = jasmine.createSpyObj('ImageService', ['createImage']);
    mockConfirmationService = jasmine.createSpyObj('ConfirmationService', [
      'askForConfirmation',
    ]);
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    // Set up basic responses for the getContacts and getResources methods.
    mockContactService.getContacts.and.returnValue(
      of({
        contacts: [
          {
            name: 'John Doe',
            address: '123 Main St',
            phoneNumber: '555-1234',
            self: '/api/contacts/1',
          },
        ],
        totalPages: 1,
        currentPage: 1,
      }),
    );
    mockResourceService.getResources.and.returnValue(
      of({
        resources: [
          {
            title: 'Resource Title',
            description: 'Resource Description',
            image: 'img.jpg',
            self: '/api/resources/1',
          },
        ],
        totalPages: 1,
        currentPage: 1,
      }),
    );

    await TestBed.configureTestingModule({
      declarations: [AdminInformationPageComponent],
      imports: [
        ReactiveFormsModule,
        TranslateModule.forRoot(), // Satisfies TranslateService
      ],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            // Simulate queryParamMap with contactsPage and resourcesPage parameters.
            queryParamMap: of(
              new Map([
                ['contactsPage', '1'],
                ['resourcesPage', '1'],
              ]),
            ),
          },
        },
        { provide: Router, useValue: mockRouter },
        { provide: ContactService, useValue: mockContactService },
        { provide: ResourceService, useValue: mockResourceService },
        { provide: ToastService, useValue: mockToastService },
        { provide: ImageService, useValue: mockImageService },
        { provide: ConfirmationService, useValue: mockConfirmationService },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminInformationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // --- Test 1: Component creation and form initialization ---
  it('should create and initialize forms', () => {
    expect(component).toBeTruthy();
    // Check that both contactForm and resourceForm are defined.
    expect(component.contactForm).toBeDefined();
    expect(component.resourceForm).toBeDefined();
    // Initial dialog flags should be false.
    expect(component.showCreateContactDialog).toBeFalse();
    expect(component.showCreateResourceDialog).toBeFalse();
  });

  // --- Test 2: Open and close the create resource dialog ---
  it('should open and close the create resource dialog correctly', () => {
    component.openCreateResourceDialog();
    expect(component.showCreateResourceDialog).toBeTrue();
    // When opening, the preview should be reset.
    expect(component.previewSrc).toBeNull();
    // Also, the resourceForm should be reset (its controls become null).
    expect(component.resourceForm.value).toEqual({
      title: null,
      description: null,
      imageFile: null,
    });
    component.closeCreateResourceDialog();
    expect(component.showCreateResourceDialog).toBeFalse();
  });

  // --- Test 3: onSubmit for contact creation when form is invalid ---
  it('should mark the contact form as touched and not call createContact when invalid', () => {
    spyOn(component.contactForm, 'markAllAsTouched');
    // Set invalid values (all required fields empty).
    component.contactForm.setValue({
      contactName: '',
      contactAddress: '',
      contactPhone: '',
    });
    component.onSubmit();
    expect(component.contactForm.markAllAsTouched).toHaveBeenCalled();
    expect(mockContactService.createContact).not.toHaveBeenCalled();
  });

  // --- Test 4: Delete a contact (simulate confirmation and deletion) ---
  it('should delete a contact when confirmed', () => {
    // Simulate that the confirmation returns true.
    mockConfirmationService.askForConfirmation.and.returnValue(of(true));
    // Simulate deletion success.
    mockContactService.deleteContact.and.returnValue(of());

    component.deleteContact('/api/contacts/1');

    expect(mockConfirmationService.askForConfirmation).toHaveBeenCalled();
    // After deletion, fetchContacts() should be called; thus, getContacts should be called.
    expect(mockContactService.getContacts).toHaveBeenCalled();
  });
});
