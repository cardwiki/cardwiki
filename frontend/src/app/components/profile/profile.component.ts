import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../services/user.service";
import {UserProfile} from "../../dtos/userProfile";
import {DeckSimple} from "../../dtos/deckSimple";
import {RevisionDetailed} from "../../dtos/revisionDetailed";
import {Globals} from "../../global/globals";
import {AuthService} from "../../services/auth.service";
import {RevisionType} from 'src/app/dtos/revisionSimple';
import { Page } from 'src/app/dtos/page';
import { Pageable } from 'src/app/dtos/pageable';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  readonly DECK_PAGINATION_LIMIT: number = 10;
  readonly REVISION_PAGINATION_LIMIT: number = 10;
  readonly REVISIONTEXT_TRUNCATE: number = 30;

  profile: UserProfile;
  deckPage: Page<DeckSimple>;
  decks: DeckSimple[] = [];
  revisionPage: Page<RevisionDetailed>;
  revisions: RevisionDetailed[] = [];

  me: boolean = false;
  editingDescription: boolean = false;
  editingSuccess: boolean = false;

  readonly revisionTypeToString: { [key in RevisionType]: string } = {
    [RevisionType.CREATE] : 'Created',
    [RevisionType.EDIT] : 'Edited',
    [RevisionType.DELETE] : 'Deleted',
  }

  constructor(public globals: Globals, private authService: AuthService, private userService: UserService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.revisions = [];
      this.decks = [];
      this.me = this.authService.getUserName() === params.get('username')
      this.loadProfile(params.get('username'));
    });
  }

  loadProfile(username: string): void {
    this.userService.getProfile(username).subscribe(profile => {
      this.profile = profile;
      this.loadDecks();
      this.loadRevisions();
    })
  }

  loadDecks(): void {
    const nextPageNumber = this.deckPage ? this.deckPage.pageable.pageNumber + 1 : 0;
    this.userService.getDecks(this.profile.id, new Pageable(nextPageNumber, this.DECK_PAGINATION_LIMIT))
      .subscribe(deckPage => {
        this.deckPage = deckPage;
        this.decks.push(...deckPage.content);
      })
  }

  loadRevisions(): void {
    const nextPageNumber = this.revisionPage ? this.revisionPage.pageable.pageNumber + 1 : 0;
    this.userService.getRevisions(this.profile.id, new Pageable(nextPageNumber, this.REVISION_PAGINATION_LIMIT))
      .subscribe(revisionPage => {
        this.revisionPage = revisionPage
        this.revisions.push(...revisionPage.content)
      })
  }

  saveDescription(): void {
    this.userService.editDescription(this.profile.id, this.profile.description).subscribe(
      profile => {
        this.profile = profile;
        this.editingSuccess = true;
        setTimeout(() => {
          this.editingDescription = this.editingSuccess = false;
        }, 1000)
      }
    );
  }
}
