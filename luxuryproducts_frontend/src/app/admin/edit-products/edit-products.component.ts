import { Component, inject, OnInit } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { EditThumbnailComponent } from './edit-thumbnail/edit-thumbnail.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-edit-products',
  imports: [
    EditThumbnailComponent,
    TranslatePipe
  ],
  templateUrl: './edit-products.component.html',
  styleUrl: './edit-products.component.scss'
})
export class EditProductsComponent implements OnInit {
  private productService= inject(ProductService);
  protected products = this.productService.getProducts();

  ngOnInit() {
    this.productService.loadProducts()
  }
}
