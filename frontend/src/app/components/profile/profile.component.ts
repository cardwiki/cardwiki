import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {UserService} from "../../services/user.service";
import {UserProfile} from "../../dtos/userProfile";
import {DeckSimple} from "../../dtos/deckSimple";
import {RevisionDetailed} from "../../dtos/revisionDetailed";
import {Globals} from "../../global/globals";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  DECK_PAGINATION_LIMIT: number = 10;
  REVISION_PAGINATION_LIMIT: number = 10;
  REVISIONTEXT_TRUNCATE: number = 30;

  @Input() profile: UserProfile; //TODO using this correctly? (only editing one field)
  decks: DeckSimple[] = [];
  revisions: RevisionDetailed[] = [];
  maxDecksLoaded: boolean = false;
  maxRevisionsLoaded: boolean = false;

  me: boolean = false;
  editingDescription: boolean = false;
  editingSuccess: boolean = false;

  constructor(private globals: Globals, private userService: UserService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      if (params.get('userid') == '@me') this.me = true;
      this.loadProfile(params.get('userid'));
    });
  }

  loadProfile(userid: string): void {
    this.userService.getProfile(userid).subscribe(profile => {
      this.profile = profile;
      this.loadDecks(0);
      this.loadRevisions(0);
    })
  }

  loadDecks(offset: number = this.decks.length/this.DECK_PAGINATION_LIMIT): void {
    this.userService.getDecks(this.profile.id, this.DECK_PAGINATION_LIMIT, offset).subscribe(decks => {
        Array.prototype.push.apply(this.decks, decks);
        if (decks.length < this.DECK_PAGINATION_LIMIT) this.maxDecksLoaded = true;
      })
  }

  loadRevisions(offset: number = this.revisions.length/this.REVISION_PAGINATION_LIMIT): void {
    this.userService.getRevisions(this.profile.id, this.REVISION_PAGINATION_LIMIT, offset).subscribe(revisions => {
        Array.prototype.push.apply(this.revisions, revisions);
        if (revisions.length < this.REVISION_PAGINATION_LIMIT) this.maxRevisionsLoaded = true;
      })
  }

  saveDescription(): void {
    this.userService.editDescription(this.profile.description).subscribe(
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
