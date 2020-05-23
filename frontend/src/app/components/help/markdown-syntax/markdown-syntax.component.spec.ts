import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MarkdownSyntaxComponent } from './markdown-syntax.component';

describe('MarkdownSyntaxComponent', () => {
  let component: MarkdownSyntaxComponent;
  let fixture: ComponentFixture<MarkdownSyntaxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MarkdownSyntaxComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MarkdownSyntaxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
