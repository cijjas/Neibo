import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { InformationPageComponent } from './information-page.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { HateoasLinksService } from '@core/index';
import { ContactService, ResourceService, LinkKey } from '@shared/index';

describe('InformationPageComponent - Initialization', () => {
  let component: InformationPageComponent;
  let fixture: ComponentFixture<InformationPageComponent>;

  // Fake ActivatedRoute: no query parameters provided.
  const fakeActivatedRoute = {
    queryParamMap: of({ get: (key: string) => null }),
    queryParams: of({}),
  };

  // Spy on Router.navigate.
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  // Stub HateoasLinksService to return fixed URLs.
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    if (key === LinkKey.NEIGHBORHOOD_CONTACTS) {
      return 'contacts_url';
    }
    if (key === LinkKey.NEIGHBORHOOD_RESOURCES) {
      return 'resources_url';
    }
    return '';
  });

  // Stub ContactService: return a dummy contacts response.
  const contactServiceSpy = jasmine.createSpyObj('ContactService', [
    'getContacts',
  ]);
  contactServiceSpy.getContacts.and.returnValue(
    of({
      contacts: [
        {
          name: 'Contact1',
          address: 'Addr1',
          phoneNumber: '123456',
          self: 'c1',
        },
      ],
      totalPages: 3,
      currentPage: 1,
    }),
  );

  // Stub ResourceService: return a dummy resources response.
  const resourceServiceSpy = jasmine.createSpyObj('ResourceService', [
    'getResources',
  ]);
  resourceServiceSpy.getResources.and.returnValue(
    of({
      resources: [
        { title: 'Resource1', description: 'Desc1', image: 'img1', self: 'r1' },
      ],
      totalPages: 2,
      currentPage: 1,
    }),
  );

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InformationPageComponent],
      providers: [
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: ContactService, useValue: contactServiceSpy },
        { provide: ResourceService, useValue: resourceServiceSpy },
      ],
      // Use NO_ERRORS_SCHEMA to ignore any unknown elements/pipes.
      schemas: [NO_ERRORS_SCHEMA],
    })
      // Override the component template to bypass the translate pipe.
      .overrideTemplate(InformationPageComponent, `<div>Dummy Template</div>`)
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InformationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize with default pagination and fetch contacts and resources', () => {
    // With no query params, the pages should default to 1.
    expect(component.contactCurrentPage).toBe(1);
    expect(component.resourceCurrentPage).toBe(1);

    // Verify that fetchContacts() was called with the default page/size.
    expect(contactServiceSpy.getContacts).toHaveBeenCalledWith({
      page: 1,
      size: component.contactPageSize,
    });
    // Verify that fetchResources() was called with the default page/size.
    expect(resourceServiceSpy.getResources).toHaveBeenCalledWith({
      page: 1,
      size: component.resourcePageSize,
    });

    // Verify that the dummy data was set on the component.
    expect(component.contacts.length).toBe(1);
    expect(component.resources.length).toBe(1);
    expect(component.contactTotalPages).toBe(3);
    expect(component.resourceTotalPages).toBe(2);
  });
});
