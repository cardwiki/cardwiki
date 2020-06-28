import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ClipboardPasteModalComponent } from './clipboard-paste-modal.component';

describe('ClipboardPasteModalComponent', () => {
  let component: ClipboardPasteModalComponent;
  let fixture: ComponentFixture<ClipboardPasteModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ClipboardPasteModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClipboardPasteModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
