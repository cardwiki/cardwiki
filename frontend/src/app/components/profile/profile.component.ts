import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../services/user.service";
import {UserProfile} from "../../dtos/userProfile";
import {DeckSimple} from "../../dtos/deckSimple";
import {RevisionDetailed} from "../../dtos/revisionDetailed";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  DECK_PAGINATION_LIMIT: number = 10;
  REVISION_PAGINATION_LIMIT: number = 10;
  REVISIONTEXT_TRUNCATE: number = 30;

  profile: UserProfile;
  decks: DeckSimple[] = [];
  revisions: RevisionDetailed[] = [];
  maxDecksLoaded: boolean = false;
  maxRevisionsLoaded: boolean = false;

  constructor(private userService: UserService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.loadProfile(params.get('username'));
    });
  }

  loadProfile(username: string): void {
    this.userService.getProfile(username).subscribe(profile => {
      this.profile = profile;
      this.loadDecks(0);
      this.loadRevisions(0);
    })
  }

  loadDecks(offset: number = this.decks.length/this.DECK_PAGINATION_LIMIT): void {
    this.userService.getDecks(this.profile.username, this.DECK_PAGINATION_LIMIT, offset).subscribe(decks => {
        Array.prototype.push.apply(this.decks, decks);
        if (decks.length < this.DECK_PAGINATION_LIMIT) this.maxDecksLoaded = true;
      })
  }

  loadRevisions(offset: number = this.revisions.length/this.REVISION_PAGINATION_LIMIT): void {
    console.log(this.revisions.length);
    this.userService.getRevisions(this.profile.username, this.REVISION_PAGINATION_LIMIT, offset).subscribe(revisions => {
        Array.prototype.push.apply(this.revisions, revisions);
        if (revisions.length < this.REVISION_PAGINATION_LIMIT) this.maxRevisionsLoaded = true;
      })
  }

}
