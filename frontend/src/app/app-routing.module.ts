import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {CategoryCreateComponent} from './components/category/category-create/category-create.component';
<<<<<<< Updated upstream
=======
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import { CardViewComponent } from "./components/card/card-view/card-view.component";
import { CardEditComponent } from "./components/card/card-edit/card-edit.component";
>>>>>>> Stashed changes

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
<<<<<<< Updated upstream
=======
  {path: 'decks/:id/cards/new', component: CardCreateComponent},
  { path: 'decks/:deckId/cards/:cardId/edit', component: CardEditComponent },
>>>>>>> Stashed changes
  { path: 'createCategory', component: CategoryCreateComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
