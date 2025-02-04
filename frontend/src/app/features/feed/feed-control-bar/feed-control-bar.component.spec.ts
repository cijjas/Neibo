import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {
  FeedControlBarComponent,
  FeedStatus,
} from './feed-control-bar.component';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';

// Import service types (adjust paths as needed)
import { HateoasLinksService } from '@core/index';
import { UserSessionService } from '@core/index';
import { LinkKey } from '@shared/index';

// Fake translate pipe to satisfy any usage of "| translate" in the template.
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

describe('FeedControlBarComponent - Initialization', () => {
  let component: FeedControlBarComponent;
  let fixture: ComponentFixture<FeedControlBarComponent>;

  // Dummy values for link lookups.
  const dummyLatest = 'latest_url';
  const dummyHot = 'hot_url';
  const dummyTrending = 'trending_url';
  const dummyFeedChannel = 'feed_channel';
  const dummyAnnouncements = 'announcements_channel';
  const dummyComplaints = 'complaints_channel';

  // Stub HateoasLinksService to return fixed URLs.
  const linkServiceSpy = jasmine.createSpyObj('HateoasLinksService', [
    'getLink',
  ]);
  linkServiceSpy.getLink.and.callFake((key: string) => {
    switch (key) {
      case LinkKey.NONE_POST_STATUS:
        return dummyLatest;
      case LinkKey.HOT_POST_STATUS:
        return dummyHot;
      case LinkKey.TRENDING_POST_STATUS:
        return dummyTrending;
      case LinkKey.NEIGHBORHOOD_FEED_CHANNEL:
        return dummyFeedChannel;
      case LinkKey.NEIGHBORHOOD_ANNOUNCEMENTS_CHANNEL:
        return dummyAnnouncements;
      case LinkKey.NEIGHBORHOOD_COMPLAINTS_CHANNEL:
        return dummyComplaints;
      default:
        return '';
    }
  });

  // Stub UserSessionService: for this test we assume a non-admin user.
  const userSessionServiceSpy = jasmine.createSpyObj('UserSessionService', [
    'getCurrentUser',
    'getCurrentRole',
  ]);
  // For this test the current user is not needed, so we can return an empty object.
  userSessionServiceSpy.getCurrentUser.and.returnValue(of({}));
  // Simulate a non-admin role (e.g., 'NEIGHBOR').
  userSessionServiceSpy.getCurrentRole.and.returnValue('NEIGHBOR');

  // Fake ActivatedRoute: no query params.
  const fakeActivatedRoute = {
    queryParams: of({}),
  };

  // Spy on Router.navigate.
  const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FeedControlBarComponent, FakeTranslatePipe],
      providers: [
        { provide: HateoasLinksService, useValue: linkServiceSpy },
        { provide: UserSessionService, useValue: userSessionServiceSpy },
        { provide: ActivatedRoute, useValue: fakeActivatedRoute },
        { provide: Router, useValue: routerSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FeedControlBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // triggers ngOnInit
  });

  it('should initialize with default values when no query params are provided', () => {
    // With no query param "withStatus", fallback should be FeedStatus.LATEST.
    expect(component.status).toEqual(FeedStatus.LATEST);
    // Verify channel URLs are set correctly.
    expect(component.feedChannelUrl).toEqual(dummyFeedChannel);
    expect(component.announcementsChannelUrl).toEqual(dummyAnnouncements);
    expect(component.complaintsChannelUrl).toEqual(dummyComplaints);
  });
});
