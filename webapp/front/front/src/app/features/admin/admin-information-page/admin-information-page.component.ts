import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-information-page',
  templateUrl: './admin-information-page.component.html',
})
export class AdminInformationPageComponent implements OnInit {

  phoneNumbersList: any[] = [];
  resourceList: any[] = [];

  constructor() { }

  ngOnInit(): void {
    // Example: fetch contacts from a service
    // this.contactService.getContacts().subscribe(contacts => this.phoneNumbersList = contacts);
    // Example: fetch resources
    // this.resourceService.getResources().subscribe(res => this.resourceList = res);
  }

  // Methods
  deleteContact(contactId: number) {
    console.log('Delete contact with ID:', contactId);
    // call service to delete contact
  }

  goToCreateContact() {
    console.log('Navigating to create-contact page...');
    // e.g. this.router.navigate(['/admin/create-contact']);
  }

  goToCreateResource() {
    console.log('Navigating to create-resource page...');
    // e.g. this.router.navigate(['/admin/create-resource']);
  }

  deleteResource(resourceId: number) {
    console.log('Delete resource with ID:', resourceId);
    // call service to delete resource
  }

  getResourceImageUrl(imageId: number): string {
    // Return your image path or use a service
    return `assets/images/${imageId}.png`;
  }
}
