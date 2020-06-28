import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardDiffComponent } from './card-diff.component';

describe('CardDiffComponent', () => {
  let component: CardDiffComponent;
  let fixture: ComponentFixture<CardDiffComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardDiffComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardDiffComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
