import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardRevisionComponent } from './card-revision.component';

describe('CardRevisionComponent', () => {
  let component: CardRevisionComponent;
  let fixture: ComponentFixture<CardRevisionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardRevisionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardRevisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
