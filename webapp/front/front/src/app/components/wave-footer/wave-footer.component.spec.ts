import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaveFooterComponent } from './wave-footer.component';

describe('WaveFooterComponent', () => {
  let component: WaveFooterComponent;
  let fixture: ComponentFixture<WaveFooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WaveFooterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(WaveFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
