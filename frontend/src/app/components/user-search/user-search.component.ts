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
      this.searchTerm = params['username'];
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
      Array.prototype.push.apply(this.users, users);
      if (users.length + this.users.length === 0) this.noUsersFound = true;
      if (users.length < this.USER_PAGINATION_LIMIT) this.maxUsersLoaded = true;
    })
  }

}
