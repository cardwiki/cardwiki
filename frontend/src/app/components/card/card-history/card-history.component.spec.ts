import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardHistoryComponent } from './card-history.component';

describe('CardHistoryComponent', () => {
  let component: CardHistoryComponent;
  let fixture: ComponentFixture<CardHistoryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardHistoryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
