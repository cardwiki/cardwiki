import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CardsImportModalComponent } from './cards-import-modal.component';

describe('CardsImportModalComponent', () => {
  let component: CardsImportModalComponent;
  let fixture: ComponentFixture<CardsImportModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CardsImportModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CardsImportModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
