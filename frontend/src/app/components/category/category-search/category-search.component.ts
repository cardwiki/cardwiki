import { Component, OnInit } from '@angular/core';
import { Page } from 'src/app/dtos/page';
import { CategorySimple } from 'src/app/dtos/categorySimple';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryService } from 'src/app/services/category.service';
import { TitleService } from 'src/app/services/title.service';
import { Pageable } from 'src/app/dtos/pageable';

@Component({
  selector: 'app-category-search',
  templateUrl: './category-search.component.html',
  styleUrls: ['./category-search.component.css'],
})
export class CategorySearchComponent implements OnInit {
  readonly limit = 10;

  searchTerm = '';
  page: Page<CategorySimple>;
  categories: CategorySimple[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private categoryService: CategoryService,
    private titleService: TitleService
  ) {
    this.route.queryParams.subscribe(
      (params) => (this.searchTerm = params.name || '')
    );
  }

  ngOnInit(): void {
    this.titleService.setTitle('Categories', 'Category search');
    this.loadCategories();
  }

  resetResults() {
    this.page = null;
    this.categories = [];
  }

  onSubmit() {
    console.log('search', this.searchTerm);
    this.categories = [];
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { name: this.searchTerm },
      queryParamsHandling: 'merge',
    });
    this.resetResults();
    this.loadCategories();
  }

  loadCategories(): void {
    const nextPage = this.page ? this.page.pageable.pageNumber + 1 : 0;
    this.categoryService
      .searchByName(this.searchTerm, new Pageable(nextPage, this.limit))
      .subscribe((categoryPage) => {
        this.page = categoryPage;
        this.categories.push(...categoryPage.content);
      });
  }

  deleteCategory(event: any, category: CategorySimple) {
    event.stopPropagation();
    event.preventDefault();

    if (
      confirm(
        `Are you sure you want to permanently delete category '${category.name}'?`
      )
    ) {
      this.categoryService.deleteCategory(category.id).subscribe(() => {
        this.categories = this.categories.filter((c) => c !== category);
      });
    }
  }
}
