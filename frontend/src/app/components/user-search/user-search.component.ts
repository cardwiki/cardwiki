import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserProfile} from '../../dtos/userProfile';
import {UserService} from '../../services/user.service';
import { Page } from 'src/app/dtos/page';
import { Pageable } from 'src/app/dtos/pageable';
import { TitleService } from 'src/app/services/title.service';

@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.css']
})
export class UserSearchComponent implements OnInit {

  readonly USER_PAGINATION_LIMIT = 10;

  searchTerm = '';
  page: Page<UserProfile>;
  users: UserProfile[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService,
              private titleService: TitleService) {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['username'] || '';
    });
  }

  ngOnInit(): void {
    this.titleService.setTitle('Users', 'User search');
    this.loadUsers();
  }

  resetResults(): void {
    this.page = null;
    this.users = [];
  }

  onSubmit(): void {
    console.log('search', this.searchTerm);
    this.users = [];
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: { username: this.searchTerm },
        queryParamsHandling: 'merge'
      });
    this.resetResults();
    this.loadUsers();
  }

  loadUsers(): void {
    const nextPage = this.page ? this.page.pageable.pageNumber + 1 : 0;
    this.userService.searchUsers(this.searchTerm, new Pageable(nextPage, this.USER_PAGINATION_LIMIT))
      .subscribe(userPage => {
        this.page = userPage;
        this.users.push(...userPage.content);
      });
  }

  grantAdminRights(event: Event, user: UserProfile): void {
    event.stopPropagation();
    event.preventDefault();
    if (confirm(`Are you sure you want to grant admin rights to user '${user.username}'`)) {
      this.userService.editAdminStatus(user.id, true).subscribe(updatedUser => {
        user.admin = updatedUser.admin;
      });
    }
  }

  editEnabledStatus(event: Event, user: UserProfile, enabled: boolean): void {
    event.stopPropagation();
    event.preventDefault();
    let reason = null;
    if (enabled) {
      if (!confirm(`Are you sure you want to enable user '${user.username}'?`)) {
        return;
      }
    } else {
      reason = prompt(`Why do you want to disable user '${user.username}'?`);
      if (reason === null) {
        return;
      }
    }
    this.userService.editEnabledStatus(user.id, enabled, reason).subscribe(updatedUser => {
      user.enabled = updatedUser.enabled;
    });
  }

  delete(event: Event, user: UserProfile): void {
    event.stopPropagation();
    event.preventDefault();
    const reason = prompt(`Why do you want to permanently delete user '${user.username}'?`);
    if (reason !== null) {
      this.userService.delete(user.id, reason).subscribe(() => {
        this.users = this.users.filter(u => u !== user);
      });
    }
  }
}
