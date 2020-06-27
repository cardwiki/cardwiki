import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckHistoryComponent } from './deck-history.component';

describe('DeckHistoryComponent', () => {
  let component: DeckHistoryComponent;
  let fixture: ComponentFixture<DeckHistoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeckHistoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeckHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
