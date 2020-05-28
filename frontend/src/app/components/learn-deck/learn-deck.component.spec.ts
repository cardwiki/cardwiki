import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LearnDeckComponent } from './learn-deck.component';

describe('LearnDeckComponent', () => {
  let component: LearnDeckComponent;
  let fixture: ComponentFixture<LearnDeckComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LearnDeckComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LearnDeckComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
