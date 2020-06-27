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
import { DeckPreviewComponent } from './components/deck-preview/deck-preview.component';
import { CategoryListComponent } from './components/category/category-list/category-list.component';
import { ListComponent } from './components/category/list/list.component';
import { CategoryDetailsComponent } from './components/category/category-details/category-details.component';
import { ListSearchComponent } from './components/category/list-search/list-search.component';
import { CategorySubcategoriesComponent } from './components/category/category-subcategories/category-subcategories.component';
import { CategoryDecksComponent } from './components/category/category-decks/category-decks.component';
import { Md2htmlPipe } from './pipes/md2html.pipe';
import { MarkdownSyntaxComponent } from './components/help/markdown-syntax/markdown-syntax.component';
import { ProfileComponent } from './components/profile/profile.component';
import { UserSearchComponent } from './components/user-search/user-search.component';
import { ToastsContainerComponent } from './components/toasts-container/toasts-container.component';
import { LearnDeckComponent } from './components/learn-deck/learn-deck.component';
import { DeckForkModalComponent } from './components/deck/deck-fork-modal/deck-fork-modal.component';
import { CardRemoveModalComponent } from './components/deck/card-remove-modal/card-remove-modal.component';
import { UserRoleDirective } from './directives/user-role.directive';
import { CommentFormComponent } from './components/comment/comment-form/comment-form.component';
import { CommentListComponent } from './components/comment/comment-list/comment-list.component';
import { ConfirmModalComponent } from './components/utils/confirm-modal/confirm-modal.component';
import { ClipboardComponent } from './components/clipboard/clipboard.component';

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
    DeckPreviewComponent,
    CategoryListComponent,
    ListComponent,
    CategoryDetailsComponent,
    CardEditComponent,
    ListSearchComponent,
    CategorySubcategoriesComponent,
    CategoryDecksComponent,
    Md2htmlPipe,
    MarkdownSyntaxComponent,
    ProfileComponent,
    UserSearchComponent,
    ToastsContainerComponent,
    LearnDeckComponent,
    DeckForkModalComponent,
    CardRemoveModalComponent,
    UserRoleDirective,
    CommentFormComponent,
    CommentListComponent,
    ConfirmModalComponent,
    ClipboardComponent,
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
