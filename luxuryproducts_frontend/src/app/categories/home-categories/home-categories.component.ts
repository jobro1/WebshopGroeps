import {Component, inject, OnInit} from '@angular/core';
import {CategoryService} from '../../services/category.service.';
import {RouterLink} from '@angular/router';
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-home-categories',
  imports: [
    RouterLink,
    TranslatePipe
  ],
  templateUrl: './home-categories.component.html',
  styleUrl: './home-categories.component.scss'
})
export class HomeCategoriesComponent implements OnInit {
  private categoryService = inject(CategoryService);
  protected categories = this.categoryService.getAllCategories();

  ngOnInit() {
    this.categoryService.loadCategories()
  }


}
