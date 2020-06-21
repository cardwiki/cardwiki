import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserProfile} from "../../dtos/userProfile";
import {UserService} from "../../services/user.service";

@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.css']
})
export class UserSearchComponent implements OnInit {

  readonly USER_PAGINATION_LIMIT = 10;

  searchTerm = '';
  users: UserProfile[] = [];
  maxUsersLoaded: boolean = false;
  noUsersFound: boolean = false;

  constructor(private route: ActivatedRoute, private router: Router, private modalService: NgbModal, private userService: UserService) {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['username'] || '';
    });
  }

  ngOnInit() {
    if (this.searchTerm) this.loadUsers(0)
  }

  onSubmit() {
    console.log('search', this.searchTerm);
    this.maxUsersLoaded = false;
    this.noUsersFound = false;
    this.users = [];
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: { username: this.searchTerm },
        queryParamsHandling: 'merge'
      });
    this.loadUsers(0)
  }

  loadUsers(offset: number = this.users.length/this.USER_PAGINATION_LIMIT): void {
    this.userService.searchUsers(this.searchTerm, this.USER_PAGINATION_LIMIT, offset).subscribe(users => {
      this.users.push(...users);
      if (users.length + this.users.length === 0) this.noUsersFound = true;
      if (users.length < this.USER_PAGINATION_LIMIT) this.maxUsersLoaded = true;
      console.log(users);
    })
  }

  grantAdminRights(user: UserProfile): void {
    if (confirm(`Are you sure you want to grant admin rights to user '${user.username}'`)) {
      this.userService.editAdminStatus(user.id, true).subscribe(updatedUser => {
        user.admin = updatedUser.admin;
      });
    }
  }

  editEnabledStatus(user: UserProfile, enabled: boolean): void {
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

  delete(user: UserProfile): void {
    const reason = prompt(`Why do you want to permanently delete user '${user.username}'?`);
    if (reason !== null) {
      this.userService.delete(user.id, reason).subscribe(_ => {
        this.users = this.users.filter(u => u !== user);
      });
    }
  }
}
