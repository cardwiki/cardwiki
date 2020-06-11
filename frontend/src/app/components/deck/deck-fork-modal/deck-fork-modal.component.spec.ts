import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeckForkModalComponent } from './deck-fork-modal.component';

describe('DeckForkModalComponent', () => {
  let component: DeckForkModalComponent;
  let fixture: ComponentFixture<DeckForkModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeckForkModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeckForkModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
