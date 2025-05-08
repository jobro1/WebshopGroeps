import {Component, inject, OnInit} from '@angular/core';
import {NgForOf, SlicePipe} from '@angular/common';
import {ProductService} from '../../services/product.service';
import {ProductComponent} from '../product/product.component';

@Component({
  selector: 'app-home-new-products',
  imports: [
    ProductComponent,
    SlicePipe

  ],
  templateUrl: './home-new-products.component.html',
  styleUrl: './home-new-products.component.scss'
})

export class HomeNewProductsComponent implements OnInit {
  private productService= inject(ProductService);
  protected products = this.productService.getProducts();

  ngOnInit() {
    this.productService.loadProducts();
  }
}
