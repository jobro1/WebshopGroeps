import {Component, inject, Input} from '@angular/core';
import {Product} from '../../models/product';
import {ProductService} from '../../services/product.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-product',
  imports: [
    RouterLink,
  ],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent {
  @Input({required: true}) product!: Product;
  private productService= inject(ProductService);
  protected products = this.productService.getProducts();

}
