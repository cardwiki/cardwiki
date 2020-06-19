import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserProfile} from "../../dtos/userProfile";
import {UserService} from "../../services/user.service";
import { Page } from 'src/app/dtos/page';
import { Pageable } from 'src/app/dtos/pageable';

@Component({
  selector: 'app-user-search',
  templateUrl: './user-search.component.html',
  styleUrls: ['./user-search.component.css']
})
export class UserSearchComponent implements OnInit {

  readonly USER_PAGINATION_LIMIT = 10;

  searchTerm = '';
  page: Page<UserProfile>
  users: UserProfile[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private userService: UserService) {
    this.route.queryParams.subscribe(params => {
      this.searchTerm = params['username'] || '';
    });
  }

  ngOnInit() {
    if (this.searchTerm)
      this.loadUsers()
  }

  onSubmit() {
    console.log('search', this.searchTerm);
    this.users = [];
    this.router.navigate(
      [],
      {
        relativeTo: this.route,
        queryParams: { username: this.searchTerm },
        queryParamsHandling: 'merge'
      });
    this.loadUsers()
  }

  loadUsers(): void {
    const nextPage = this.page ? this.page.pageable.pageNumber + 1 : 0
    this.userService.searchUsers(this.searchTerm, new Pageable(nextPage, this.USER_PAGINATION_LIMIT))
      .subscribe(userPage => {
        this.page.numberOfElements
        this.page = userPage
        this.users.push(...userPage.content);
      })
  }

}
