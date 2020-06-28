import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryPickerModalComponent } from './category-picker-modal.component';

describe('CategoryPickerModalComponent', () => {
  let component: CategoryPickerModalComponent;
  let fixture: ComponentFixture<CategoryPickerModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CategoryPickerModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CategoryPickerModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
