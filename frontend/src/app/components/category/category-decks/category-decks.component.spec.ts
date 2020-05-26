import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryDecksComponent } from './category-decks.component';

describe('CategoryDecksComponent', () => {
  let component: CategoryDecksComponent;
  let fixture: ComponentFixture<CategoryDecksComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategoryDecksComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoryDecksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
