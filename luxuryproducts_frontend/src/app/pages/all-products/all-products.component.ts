import {Component, inject, OnInit} from '@angular/core';
import {ProductCardComponent} from '../../products/product-card/product-card.component';
import {ProductService} from '../../services/product.service';
import {ProductComponent} from '../../products/product/product.component';

@Component({
  selector: 'app-all-products',
  imports: [
    ProductComponent

  ],
  templateUrl: './all-products.component.html',
  styleUrl: './all-products.component.scss'
})
export class AllProductsComponent implements OnInit {

  private productService= inject(ProductService);
  protected products = this.productService.getProducts();

  ngOnInit() {
    this.productService.loadProducts();
  }

}
