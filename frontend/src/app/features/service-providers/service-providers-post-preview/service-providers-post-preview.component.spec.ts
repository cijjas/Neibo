import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Pipe, PipeTransform } from '@angular/core';
import { ServiceProvidersPostPreviewComponent } from './service-providers-post-preview.component';
import { Post } from '@shared/index';

@Pipe({ name: 'translate' })
class FakeTranslatePipe implements PipeTransform {
  transform(value: string): string {
    return value;
  }
}

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
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceProvidersPostPreviewComponent);
    component = fixture.componentInstance;
    component.post = dummyPost; 
    fixture.detectChanges(); 
  });

  it('should update humanReadableDate on init', () => {
    expect(component.humanReadableDate).toContain('minute');
  });
});
