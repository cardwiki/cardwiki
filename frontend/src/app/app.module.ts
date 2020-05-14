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
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { CategoryCreateComponent } from './components/category/category-create/category-create.component';
import { CategoryFormComponent } from './components/category/category-form/category-form.component';
import { CategoryUpdateComponent } from './components/category/category-update/category-update.component';
import { CardCreateComponent } from './components/card/card-create/card-create.component';
import { CardFormComponent } from './components/card/card-form/card-form.component';
import { CardViewComponent } from './components/card/card-view/card-view.component';
import { ListCategoriesComponent } from './components/category/list-categories/list-categories.component';
import { ListComponent } from './components/category/list/list.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    CategoryCreateComponent,
    CategoryFormComponent,
    CategoryUpdateComponent,
    CardCreateComponent,
    CardFormComponent,
    CardViewComponent,
    ListCategoriesComponent,
    ListComponent,
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
