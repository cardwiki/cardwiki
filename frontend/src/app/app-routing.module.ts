import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {CategoryCreateComponent} from './components/category/category-create/category-create.component';
import { CardEditComponent } from "./components/card/card-edit/card-edit.component";
import {CategoryUpdateComponent} from './components/category/category-update/category-update.component';
import {CardCreateComponent} from './components/card/card-create/card-create.component';
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import { SearchComponent } from './components/search/search.component';
import { AboutComponent } from './components/about/about.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { LearnComponent } from './components/learn/learn.component';
import { DeckPreviewComponent } from './components/deck-preview/deck-preview.component';
import {CategoryUpdateComponent} from './components/category/category-update/category-update.component';
import {CategoryListComponent} from './components/category/category-list/category-list.component';
import {CategoryDetailsComponent} from './components/category/category-details/category-details.component';


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'about', component: AboutComponent},
  {path: 'search', component: SearchComponent},
  {path: 'login', component: LoginComponent},
  {path: 'decks/:id/cards/new', component: CardCreateComponent},
  { path: 'decks/:deckId/cards/:cardId/edit', component: CardEditComponent },
  { path: 'createCategory', component: CategoryCreateComponent },
  {path: 'categories/new', canActivate: [AuthGuard], component: CategoryCreateComponent},
  {path: 'categories/:id/edit', canActivate: [AuthGuard], component: CategoryUpdateComponent},
  {path: 'learn', canActivate: [AuthGuard], component: LearnComponent},
  {path: 'learn/:id', component: DeckPreviewComponent},
  {path: 'decks/:id/cards/new', canActivate: [AuthGuard], component: CardCreateComponent},
  {path: 'categories', component: CategoryListComponent},
  {path: 'categories/new', canActivate: [AuthGuard], component: CategoryCreateComponent},
  {path: 'categories/:id', component: CategoryDetailsComponent},
  {path: 'categories/:id/edit', canActivate: [AuthGuard], component: CategoryUpdateComponent},
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
