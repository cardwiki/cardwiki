import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardRemoveModalComponent } from './card-remove-modal.component';

describe('CardRemoveModalComponent', () => {
  let component: CardRemoveModalComponent;
  let fixture: ComponentFixture<CardRemoveModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardRemoveModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardRemoveModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
