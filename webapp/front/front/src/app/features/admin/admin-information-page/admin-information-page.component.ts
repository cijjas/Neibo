import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastService } from '@core/index';
import { ContactService, ResourceService } from '@shared/index';

@Component({
  selector: 'app-admin-information-page',
  templateUrl: './admin-information-page.component.html',
})
export class AdminInformationPageComponent implements OnInit {
  // Contacts
  phoneNumbersList: any[] = [];
  contactCurrentPage = 1;
  contactPageSize = 10;
  contactTotalPages = 1;

  // Resources
  resourceList: any[] = [];
  resourceCurrentPage = 1;
  resourcePageSize = 10;
  resourceTotalPages = 1;

  // Dialog toggle
  showCreateContactDialog = false;

  // Reactive form for Contact
  contactForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private contactService: ContactService,
    private resourceService: ResourceService,
    private toastService: ToastService
  ) { }

  ngOnInit(): void {
    // Build the contact form
    this.contactForm = this.fb.group({
      contactName: [
        '',
        [
          Validators.required,
          Validators.maxLength(50), // Limit to 50 characters
        ],
      ],
      contactAddress: [
        '',
        [
          Validators.required,
          Validators.maxLength(50), // Limit to 50 characters
        ],
      ],
      contactPhone: [
        '',
        [
          Validators.required,
        ],
      ],
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

  // Getters for form controls (for simpler usage in template)
  get contactNameControl() {
    return this.contactForm.get('contactName');
  }
  get addressControl() {
    return this.contactForm.get('contactAddress');
  }
  get phoneControl() {
    return this.contactForm.get('contactPhone');
  }

  // DIALOG CONTROL
  openCreateContactDialog() {
    this.showCreateContactDialog = true;
  }
  closeCreateContactDialog() {
    this.showCreateContactDialog = false;
  }

  // SUBMIT CONTACT FORM
  onSubmit() {
    if (this.contactForm.invalid) {
      // Mark all as touched so errors appear
      this.contactForm.markAllAsTouched();
      return;
    }

    this.contactService.createContact(
      this.contactForm.value.contactName,
      this.contactForm.value.contactPhone,
      this.contactForm.value.contactAddress
    ).subscribe({
      next: (res) => {
        this.toastService.showToast('Contact created!', 'success');
        this.closeCreateContactDialog();
        this.fetchContacts(); // Refresh the contact list
      },
      error: (err) => {
        console.error('Error creating contact:', err);
        this.toastService.showToast('Failed to create contact.', 'error');
      },
    });
  }

  // CONTACTS
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
    // Update the query params in the URL (merging with existing)
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

  // RESOURCES
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

  // NAVIGATION
  goToCreateResource() {
    this.router.navigate(['admin/information/resource-info/new']);
  }
}
