import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CategoryService} from '../../services/category.service.';
import {ProductComponent} from "../../products/product/product.component";

@Component({
  selector: 'app-products-by-category',
    imports: [
        ProductComponent
    ],
  templateUrl: './products-by-category.component.html',
  styleUrl: './products-by-category.component.scss'
})
export class ProductsByCategoryComponent implements OnInit {
  private categoryService = inject(CategoryService);

  categoryId = signal<number | null>(null);
  private route = inject(ActivatedRoute);
  protected category = this.categoryService.getCategory();

  ngOnInit() {

    this.route.paramMap.subscribe((params) => {
      const categoryId = params.get('categoryId');
      if (categoryId) {
        this.categoryId.set(+categoryId);
        this.categoryService.loadCategoryById(this.categoryId()!)
      }
    });
  }
}
