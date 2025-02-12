import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Contact,
  Resource,
  ContactService,
  ResourceService,
  LinkKey,
} from '@shared/index';
import { HateoasLinksService } from '@core/index';
import { TranslateService } from '@ngx-translate/core';
import { AppTitleKeys } from '@shared/constants/app-titles';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-information-page',
  templateUrl: './information-page.component.html',
})
export class InformationPageComponent implements OnInit {
  contacts: Contact[] = [];
  resources: Resource[] = [];

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

  loading: boolean = true;

  constructor(
    private linkService: HateoasLinksService,
    private resourceService: ResourceService,
    private contactService: ContactService,
    private route: ActivatedRoute,
    private router: Router,
    private translate: TranslateService,
    private titleService: Title,
  ) {}

  ngOnInit(): void {
    const title = this.translate.instant(AppTitleKeys.INFORMATION_PAGE);
    this.titleService.setTitle(title);

    this.resourcesUrl = this.linkService.getLink(
      LinkKey.NEIGHBORHOOD_RESOURCES,
    );
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
    this.contactService
      .getContacts({
        page: this.contactCurrentPage,
        size: this.contactPageSize,
      })
      .subscribe({
        next: result => {
          this.contacts = result.contacts;
          this.contactTotalPages = result.totalPages;
          this.contactCurrentPage = result.currentPage;
        },
        error: err => {
          console.error(err);
        },
      });
  }

  fetchResources(): void {
    this.loading = true;
    this.resourceService
      .getResources({
        page: this.resourceCurrentPage,
        size: this.resourcePageSize,
      })
      .subscribe({
        next: result => {
          this.resources = result.resources;
          this.resourceTotalPages = result.totalPages;
          this.resourceCurrentPage = result.currentPage;
          this.loading = false;
        },
        error: err => {
          console.error(err);
          this.loading = false;
        },
      });
  }

  onContactPageChange(newPage: number): void {
    this.contactCurrentPage = newPage;
    // Update query params for contacts
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { contactsPage: this.contactCurrentPage },
      queryParamsHandling: 'merge',
    });
    this.fetchContacts();
  }

  onResourcePageChange(newPage: number): void {
    this.resourceCurrentPage = newPage;
    // Update query params for resources
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { resourcesPage: this.resourceCurrentPage },
      queryParamsHandling: 'merge',
    });
    this.fetchResources();
  }
}
