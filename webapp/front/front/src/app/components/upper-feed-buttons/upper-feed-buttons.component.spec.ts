import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpperFeedButtonsComponent } from './upper-feed-buttons.component';

describe('UpperFeedButtonsComponent', () => {
  let component: UpperFeedButtonsComponent;
  let fixture: ComponentFixture<UpperFeedButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpperFeedButtonsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UpperFeedButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
