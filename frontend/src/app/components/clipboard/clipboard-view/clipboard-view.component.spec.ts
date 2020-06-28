import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClipboardViewComponent } from './clipboard-view.component';

describe('ClipboardComponent', () => {
  let component: ClipboardViewComponent;
  let fixture: ComponentFixture<ClipboardViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClipboardViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClipboardViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
