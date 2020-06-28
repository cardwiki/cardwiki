import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {CategoryCreateComponent} from './components/category/category-create/category-create.component';
import { CardEditComponent } from './components/card/card-edit/card-edit.component';
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import {DeckViewComponent} from './components/deck/deck-view/deck-view.component';
import {DeckEditComponent} from './components/deck/deck-edit/deck-edit.component';
import { SearchComponent } from './components/search/search.component';
import { AboutComponent } from './components/about/about.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { DeckPreviewComponent } from './components/deck-preview/deck-preview.component';
import {CategoryUpdateComponent} from './components/category/category-update/category-update.component';
import {CategoryListComponent} from './components/category/category-list/category-list.component';
import {CategoryDetailsComponent} from './components/category/category-details/category-details.component';
import {CategorySubcategoriesComponent} from './components/category/category-subcategories/category-subcategories.component';
import {CategoryDecksComponent} from './components/category/category-decks/category-decks.component';

import { MarkdownSyntaxComponent } from './components/help/markdown-syntax/markdown-syntax.component';
import {ProfileComponent} from "./components/profile/profile.component";
import {UserSearchComponent} from "./components/user-search/user-search.component";
import {LearnDeckComponent} from './components/learn-deck/learn-deck.component';
import { ClipboardViewComponent } from './components/clipboard/clipboard-view/clipboard-view.component';


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'categories', component: CategoryListComponent},
  {path: 'categories/new', canActivate: [AuthGuard], component: CategoryCreateComponent},
  {path: 'categories/:id', component: CategoryDetailsComponent},
  {path: 'categories/:id/decks', component: CategoryDecksComponent},
  {path: 'categories/:id/edit', canActivate: [AuthGuard], component: CategoryUpdateComponent},
  {path: 'categories/:id/subcategories', component: CategorySubcategoriesComponent},
  {path: 'decks/:id', component: DeckViewComponent},
  {path: 'decks/:id/cards/new', canActivate: [AuthGuard], component: CardCreateComponent},
  {path: 'decks/:id/edit', canActivate: [AuthGuard], component: DeckEditComponent},
  {path: 'decks/:id/preview', component: DeckPreviewComponent},
  {path: 'decks/:deckId/cards/:cardId/edit', canActivate: [AuthGuard], component: CardEditComponent},
  {path: 'learn/:id', canActivate: [AuthGuard], component: LearnDeckComponent},
  {path: 'login', component: LoginComponent},
  {path: 'search', component: SearchComponent},
  {path: 'users/:username/profile', component: ProfileComponent},
  {path: 'users', component: UserSearchComponent},
  {path: 'clipboard', component: ClipboardViewComponent},

  // static pages
  {path: 'about', component: AboutComponent},
  {path: 'help/markdown-syntax', component: MarkdownSyntaxComponent},

  // 404
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
