import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckPreviewComponent } from './deck-preview.component';

describe('DeckPreviewComponent', () => {
  let component: DeckPreviewComponent;
  let fixture: ComponentFixture<DeckPreviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeckPreviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeckPreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
