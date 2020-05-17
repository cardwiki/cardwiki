import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {CategoryCreateComponent} from './components/category/category-create/category-create.component';
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import {DeckViewComponent} from './components/deck/deck-view/deck-view.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'decks/:id/cards/new', canActivate: [AuthGuard], component: CardCreateComponent},
  {path: 'createCategory', canActivate: [AuthGuard], component: CategoryCreateComponent},
  {path: 'decks/:id', component: DeckViewComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
