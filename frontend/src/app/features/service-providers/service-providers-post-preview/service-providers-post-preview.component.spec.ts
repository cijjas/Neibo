import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { ServiceProvidersPostPreviewComponent } from './service-providers-post-preview.component';
import { Post } from '@shared/index';

// ----- Fake Translate Pipe -----
@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

// ----- Dummy Post -----
// Make sure the dummy post satisfies the full Post interface.
// Adjust the properties as necessary for your application.
const dummyPost: Post = {
  title: 'Dummy Title',
  body: 'Dummy Body',
  image: 'dummy.jpg',
  channel: 'Dummy Channel',
  createdAt: new Date(Date.now() - 60000),
  likeCount: 0,
  comments: '',
  author: undefined,
  self: '',
};

describe('ServiceProvidersPostPreviewComponent', () => {
  let component: ServiceProvidersPostPreviewComponent;
  let fixture: ComponentFixture<ServiceProvidersPostPreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServiceProvidersPostPreviewComponent, FakeTranslatePipe],
      // Use NO_ERRORS_SCHEMA to ignore any unknown elements (if any).
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersPostPreviewComponent);
    component = fixture.componentInstance;
    component.post = dummyPost; // Set the input
    fixture.detectChanges(); // Triggers ngOnInit()
  });

  it('should update humanReadableDate on init', () => {
    // Because the dummy post was created 1 minute ago,
    // the humanReadableDate should contain the word "minute".
    expect(component.humanReadableDate).toContain('minute');
  });
});
