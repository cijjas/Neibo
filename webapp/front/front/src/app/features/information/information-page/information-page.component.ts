import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Contact, Resource, ContactService, ResourceService, LinkKey } from '@shared/index';
import { HateoasLinksService } from '@core/index';

@Component({
  selector: 'app-information-page',
  templateUrl: './information-page.component.html',
})
export class InformationPageComponent implements OnInit {
  contacts: Contact[] = [];
  resources: Resource[] = [];
  darkMode = false;

  // Contacts Pagination
  contactCurrentPage = 1;
  contactPageSize = 10;
  contactTotalPages = 1;

  // Resources Pagination
  resourceCurrentPage = 1;
  resourcePageSize = 10;
  resourceTotalPages = 1;

  contactsUrl: string;
  resourcesUrl: string;

  constructor(
    private linkService: HateoasLinksService,
    private resourceService: ResourceService,
    private contactService: ContactService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.linkService.logLinks();

    this.resourcesUrl = this.linkService.getLink(LinkKey.NEIGHBORHOOD_RESOURCES);
    this.contactsUrl = this.linkService.getLink(LinkKey.NEIGHBORHOOD_CONTACTS);

    // Read initial page states from query parameters
    this.route.queryParamMap.subscribe(params => {
      const contactPageParam = params.get('contactsPage');
      const resourcePageParam = params.get('resourcesPage');

      this.contactCurrentPage = contactPageParam ? +contactPageParam : 1;
      this.resourceCurrentPage = resourcePageParam ? +resourcePageParam : 1;

      // Fetch data after we have initial pagination from query params
      this.fetchContacts();
      this.fetchResources();
    });
  }

  fetchContacts(): void {
    this.contactService.getContacts({ page: this.contactCurrentPage, size: this.contactPageSize })
      .subscribe((result) => {
        this.contacts = result.contacts;
        this.contactTotalPages = result.totalPages;
        this.contactCurrentPage = result.currentPage;
      });
  }

  fetchResources(): void {
    this.resourceService.getResources({ page: this.resourceCurrentPage, size: this.resourcePageSize })
      .subscribe((result) => {
        this.resources = result.resources;
        this.resourceTotalPages = result.totalPages;
        this.resourceCurrentPage = result.currentPage;
      });
  }

  onContactPageChange(newPage: number): void {
    this.contactCurrentPage = newPage;
    // Update query params for contacts
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { contactsPage: this.contactCurrentPage },
      queryParamsHandling: 'merge'
    });
    this.fetchContacts();
  }

  onResourcePageChange(newPage: number): void {
    this.resourceCurrentPage = newPage;
    // Update query params for resources
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { resourcesPage: this.resourceCurrentPage },
      queryParamsHandling: 'merge'
    });
    this.fetchResources();
  }
}
