import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ImageService, ToastService } from '@core/index';
import { ContactService, ResourceService } from '@shared/index';
import { catchError, Observable, of, switchMap } from 'rxjs';

@Component({
  selector: 'app-admin-information-page',
  templateUrl: './admin-information-page.component.html',
})
export class AdminInformationPageComponent implements OnInit {
  // ------------------ Contacts ------------------
  phoneNumbersList: any[] = [];
  contactCurrentPage = 1;
  contactPageSize = 10;
  contactTotalPages = 1;
  showCreateContactDialog = false;
  contactForm: FormGroup;

  // ------------------ Resources ------------------
  resourceList: any[] = [];
  resourceCurrentPage = 1;
  resourcePageSize = 10;
  resourceTotalPages = 1;
  // New Resource dialog
  showCreateResourceDialog = false;
  resourceForm: FormGroup;
  // For image preview
  previewSrc: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private contactService: ContactService,
    private resourceService: ResourceService,
    private toastService: ToastService,
    private imageService: ImageService
  ) { }

  ngOnInit(): void {
    // ------------------ Contact Form ------------------
    this.contactForm = this.fb.group({
      contactName: ['', [Validators.required, Validators.maxLength(50)]],
      contactAddress: ['', [Validators.required, Validators.maxLength(50)]],
      contactPhone: ['', [Validators.required]],
    });

    // ------------------ Resource Form ------------------
    this.resourceForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      imageFile: [null],
    });

    // Subscribe to query params for pagination
    this.route.queryParamMap.subscribe((params) => {
      const contactPageParam = params.get('contactsPage');
      const resourcePageParam = params.get('resourcesPage');

      // Use the param or default to page 1
      this.contactCurrentPage = contactPageParam ? +contactPageParam : 1;
      this.resourceCurrentPage = resourcePageParam ? +resourcePageParam : 1;

      this.fetchContacts();
      this.fetchResources();
    });
  }

  // ------------------ CONTACT GETTERS ------------------
  get contactNameControl() {
    return this.contactForm.get('contactName');
  }
  get addressControl() {
    return this.contactForm.get('contactAddress');
  }
  get phoneControl() {
    return this.contactForm.get('contactPhone');
  }

  // ------------------ RESOURCE GETTERS ------------------
  get titleControl() {
    return this.resourceForm.get('title');
  }
  get descControl() {
    return this.resourceForm.get('description');
  }

  // ------------------ CONTACT DIALOG CONTROL ------------------
  openCreateContactDialog() {
    this.showCreateContactDialog = true;
  }
  closeCreateContactDialog() {
    this.showCreateContactDialog = false;
  }

  // ------------------ RESOURCE DIALOG CONTROL ------------------
  openCreateResourceDialog() {
    this.showCreateResourceDialog = true;
    // Reset preview each time we open the dialog
    this.previewSrc = null;
    this.resourceForm.reset();
  }
  closeCreateResourceDialog() {
    this.showCreateResourceDialog = false;
  }

  // ------------------ CONTACT CREATE ------------------
  onSubmit() {
    if (this.contactForm.invalid) {
      this.contactForm.markAllAsTouched();
      return;
    }

    this.contactService
      .createContact(
        this.contactForm.value.contactName,
        this.contactForm.value.contactPhone,
        this.contactForm.value.contactAddress
      )
      .subscribe({
        next: () => {
          this.toastService.showToast('Contact created!', 'success');
          this.closeCreateContactDialog();
          this.fetchContacts(); // Refresh
        },
        error: (err) => {
          console.error('Error creating contact:', err);
          this.toastService.showToast('Failed to create contact.', 'error');
        },
      });
  }

  // ------------------ RESOURCE CREATE ------------------
  onSubmitResource() {
    if (this.resourceForm.invalid) {
      this.resourceForm.markAllAsTouched();
      return;
    }

    const formValue = this.resourceForm.value;
    const file: File | null = formValue.imageFile;

    // Create the image first, then create the resource
    this.createImageObservable(file)
      .pipe(
        switchMap((imageUrl) => {
          if (!imageUrl) {
            throw new Error('Image upload failed. Resource creation aborted.');
          }
          // Proceed to create the resource using the uploaded image URL
          return this.resourceService.createResource(
            formValue.title,
            formValue.description,
            imageUrl,
          );
        })
      )
      .subscribe({
        next: () => {
          this.toastService.showToast('Resource created successfully!', 'success');
          this.closeCreateResourceDialog();
          this.fetchResources(); // Refresh the list of resources
        },
        error: (err) => {
          console.error('Error creating resource:', err);
          this.toastService.showToast('Failed to create resource.', 'error');
        },
      });
  }

  private createImageObservable(imageFile: File | null): Observable<string | null> {
    return imageFile
      ? this.imageService.createImage(imageFile).pipe(
        catchError((error) => {
          console.error('Error uploading image:', error);
          return of(null); // Return null if image upload fails
        })
      )
      : of(null);
  }



  // ------------------ IMAGE PREVIEW ------------------
  onFileChange(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.resourceForm.patchValue({ imageFile: file });

      // file preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewSrc = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    } else {
      this.previewSrc = null;
    }
  }

  // ------------------ CONTACTS ------------------
  fetchContacts(): void {
    this.contactService
      .getContacts({ page: this.contactCurrentPage, size: this.contactPageSize })
      .subscribe({
        next: (data) => {
          this.phoneNumbersList = data.contacts;
          this.contactTotalPages = data.totalPages;
          this.contactCurrentPage = data.currentPage;
        },
        error: (err) => {
          this.toastService.showToast(
            'There was an issue looking up contact information.',
            'error'
          );
          console.error('Error fetching contacts:', err);
        },
      });
  }

  onContactPageChange(newPage: number): void {
    this.contactCurrentPage = newPage;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { contactsPage: this.contactCurrentPage },
      queryParamsHandling: 'merge',
    });
    this.fetchContacts();
  }

  deleteContact(contactUrl: string) {
    this.toastService.showToast('Contact deleted successfully.', 'success');
    this.contactService.deleteContact(contactUrl).subscribe(() => {
      this.fetchContacts();
    });
  }

  // ------------------ RESOURCES ------------------
  fetchResources(): void {
    this.resourceService
      .getResources({ page: this.resourceCurrentPage, size: this.resourcePageSize })
      .subscribe({
        next: (data) => {
          this.resourceList = data.resources;
          this.resourceTotalPages = data.totalPages;
          this.resourceCurrentPage = data.currentPage;
        },
        error: (err) => {
          this.toastService.showToast(
            'There was an issue looking up resource information.',
            'error'
          );
          console.error('Error fetching resources:', err);
        },
      });
  }

  onResourcePageChange(newPage: number): void {
    this.resourceCurrentPage = newPage;
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { resourcesPage: this.resourceCurrentPage },
      queryParamsHandling: 'merge',
    });
    this.fetchResources();
  }

  deleteResource(resourceUrl: string) {
    this.toastService.showToast('Resource deleted successfully.', 'success');
    this.resourceService.deleteResource(resourceUrl).subscribe(() => {
      this.fetchResources();
    });
  }

}
