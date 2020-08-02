import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { CategoryCreateComponent } from './components/category/category-create/category-create.component';
import { CardEditComponent } from './components/card/card-edit/card-edit.component';
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import { DeckViewComponent } from './components/deck/deck-view/deck-view.component';
import { DeckEditComponent } from './components/deck/deck-edit/deck-edit.component';
import { SearchComponent } from './components/search/search.component';
import { AboutComponent } from './components/about/about.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { DeckPreviewComponent } from './components/deck/deck-preview/deck-preview.component';
import { CategoryUpdateComponent } from './components/category/category-update/category-update.component';
import { CategoryDetailsComponent } from './components/category/category-details/category-details.component';
import { MarkdownSyntaxComponent } from './components/help/markdown-syntax/markdown-syntax.component';
import { ProfileComponent } from './components/profile/profile.component';
import { UserSearchComponent } from './components/user-search/user-search.component';
import { LearnDeckComponent } from './components/learn-deck/learn-deck.component';
import { ClipboardViewComponent } from './components/clipboard/clipboard-view/clipboard-view.component';
import { CardDiffComponent } from './components/card/card-diff/card-diff.component';
import { CardHistoryComponent } from './components/card/card-history/card-history.component';
import { DeckHistoryComponent } from './components/deck/deck-history/deck-history.component';
import { CategorySearchComponent } from './components/category/category-search/category-search.component';
import { SettingsComponent } from './components/settings/settings.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'categories', component: CategorySearchComponent },
  {
    path: 'categories/new',
    canActivate: [AuthGuard],
    component: CategoryCreateComponent,
  },
  { path: 'categories/:id', component: CategoryDetailsComponent },
  {
    path: 'categories/:id/edit',
    canActivate: [AuthGuard],
    component: CategoryUpdateComponent,
  },
  { path: 'decks/:id', component: DeckViewComponent },
  {
    path: 'decks/:id/cards/new',
    canActivate: [AuthGuard],
    component: CardCreateComponent,
  },
  {
    path: 'decks/:id/edit',
    canActivate: [AuthGuard],
    component: DeckEditComponent,
  },
  { path: 'decks/:id/preview', component: DeckPreviewComponent },
  { path: 'decks/:id/history', component: DeckHistoryComponent },
  { path: 'decks/:deckId/cards/:cardId', component: CardDiffComponent },
  {
    path: 'decks/:deckId/cards/:cardId/edit',
    canActivate: [AuthGuard],
    component: CardEditComponent,
  },
  {
    path: 'decks/:deckId/cards/:cardId/history',
    component: CardHistoryComponent,
  },
  {
    path: 'learn/:id',
    canActivate: [AuthGuard],
    component: LearnDeckComponent,
  },
  { path: 'login', component: LoginComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'search', component: SearchComponent },
  { path: 'users/:username/profile', component: ProfileComponent },
  { path: 'users', component: UserSearchComponent },
  { path: 'clipboard', component: ClipboardViewComponent },

  // static pages
  { path: 'about', component: AboutComponent },
  { path: 'help/markdown-syntax', component: MarkdownSyntaxComponent },

  // 404
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
