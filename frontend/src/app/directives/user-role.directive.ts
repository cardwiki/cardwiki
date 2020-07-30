import {
  Directive,
  Input,
  ViewContainerRef,
  TemplateRef,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService, UserRole } from '../services/auth.service';

@Directive({
  selector: '[appUserRole]',
})
export class UserRoleDirective implements OnInit, OnDestroy {
  @Input() appUserRole: UserRole;

  private stop$ = new Subject();
  private isVisible = false;

  constructor(
    private viewContainerRef: ViewContainerRef,
    private templateRef: TemplateRef<any>,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.userRoles$
      .pipe(takeUntil(this.stop$))
      .subscribe((roles) => {
        if (roles.includes(this.appUserRole)) {
          // Update view if not already visible
          if (!this.isVisible) {
            this.isVisible = true;
            this.viewContainerRef.createEmbeddedView(this.templateRef);
          }
        } else {
          this.isVisible = false;
          this.viewContainerRef.clear();
        }
      });
  }

  // Clear the subscription on destroy
  ngOnDestroy() {
    this.stop$.next();
  }
}
