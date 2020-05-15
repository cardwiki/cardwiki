import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {CategoryCreateComponent} from './components/category/category-create/category-create.component';
import {CategoryUpdateComponent} from './components/category/category-update/category-update.component';
import {CardCreateComponent} from './components/card/card-create/card-create.component';
import {CategoryListComponent} from './components/category/category-list/category-list.component';
import {CategoryDetailsComponent} from './components/category/category-details/category-details.component';


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'categories/new', component: CategoryCreateComponent},
  {path: 'categories/:id/edit', component: CategoryUpdateComponent},
  {path: 'decks/:id/cards/new', component: CardCreateComponent},
  {path: 'categories', component: CategoryListComponent},
  {path: 'categories/:id', component: CategoryDetailsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
