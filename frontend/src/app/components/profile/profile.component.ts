import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../services/user.service";
import {UserProfile} from "../../dtos/userProfile";
import {DeckSimple} from "../../dtos/deckSimple";
import {RevisionDetailed} from "../../dtos/revisionDetailed";
import {Globals} from "../../global/globals";
import {AuthService} from "../../services/auth.service";
import {RevisionType} from 'src/app/dtos/revisionSimple';

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
  decks: DeckSimple[] = [];
  revisions: RevisionDetailed[] = [];
  maxDecksLoaded: boolean = false;
  maxRevisionsLoaded: boolean = false;

  me: boolean = false;
  admin: boolean = false;
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
    if (localStorage.getItem("whoami")) this.admin = JSON.parse(localStorage.getItem("whoami")).admin;
  }

  loadProfile(username: string): void {
    this.userService.getProfile(username).subscribe(profile => {
      this.profile = profile;
      this.loadDecks(0);
      this.loadRevisions(0);
    })
  }

  loadDecks(offset: number = this.decks.length/this.DECK_PAGINATION_LIMIT): void {
    this.userService.getDecks(this.profile.id, this.DECK_PAGINATION_LIMIT, offset).subscribe(decks => {
      this.decks.push(...decks);
      if (decks.length < this.DECK_PAGINATION_LIMIT) this.maxDecksLoaded = true;
    })
  }

  loadRevisions(offset: number = this.revisions.length/this.REVISION_PAGINATION_LIMIT): void {
    this.userService.getRevisions(this.profile.id, this.REVISION_PAGINATION_LIMIT, offset).subscribe(revisions => {
      this.revisions.push(...revisions);
      if (revisions.length < this.REVISION_PAGINATION_LIMIT) this.maxRevisionsLoaded = true;
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
