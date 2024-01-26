import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackgroundCloudsComponent } from './background-clouds.component';

describe('BackgroundCloudsComponent', () => {
  let component: BackgroundCloudsComponent;
  let fixture: ComponentFixture<BackgroundCloudsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BackgroundCloudsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BackgroundCloudsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
