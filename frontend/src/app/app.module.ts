import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { DeckViewComponent } from './components/deck/deck-view/deck-view.component';
import { CategoryCreateComponent } from './components/category/category-create/category-create.component';
import { CategoryFormComponent } from './components/category/category-form/category-form.component';
import { CategoryUpdateComponent } from './components/category/category-update/category-update.component';
import { DeckCreateModalComponent } from './components/deck/deck-create-modal/deck-create-modal.component';
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import { CardFormComponent } from './components/card/card-form/card-form.component';
import { CardViewComponent } from './components/card/card-view/card-view.component';
import { DeckEditComponent } from './components/deck/deck-edit/deck-edit.component';
import { CardEditComponent } from './components/card/card-edit/card-edit.component';
import { SearchComponent } from './components/search/search.component';
import { AboutComponent } from './components/about/about.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { LearnComponent } from './components/learn/learn.component';
import { DeckPreviewComponent } from './components/deck-preview/deck-preview.component';
import { CategoryListComponent } from './components/category/category-list/category-list.component';
import { ListComponent } from './components/category/list/list.component';
import { CategoryDetailsComponent } from './components/category/category-details/category-details.component';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    CategoryCreateComponent,
    CategoryFormComponent,
    CategoryUpdateComponent,
    DeckCreateModalComponent,
    CardCreateComponent,
    CardFormComponent,
    CardViewComponent,
    DeckViewComponent,
    DeckEditComponent,
    SearchComponent,
    AboutComponent,
    DashboardComponent,
    PageNotFoundComponent,
    LearnComponent,
    DeckPreviewComponent,
    CategoryListComponent,
    ListComponent,
    CategoryDetailsComponent,
    CardEditComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
