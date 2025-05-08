import {Component, DestroyRef, inject, Input, OnInit} from '@angular/core';
import {ProductCardComponent} from '../product-card/product-card.component';
import {Product} from '../../models/product';
import {ProductService} from '../../services/product.service';
import {RouterLink} from '@angular/router';
import {CartService} from '../../services/cart.service';
import {ImageSliderComponent} from '../image-slider/image-slider.component';

@Component({
  selector: 'app-product',
  imports: [
    ProductCardComponent,
    RouterLink,
    ImageSliderComponent,
  ],
  templateUrl: './product.component.html',
  styleUrl: './product.component.scss'
})
export class ProductComponent implements OnInit {
  @Input({required: true}) product!: Product;
  private productService= inject(ProductService);
  protected products = this.productService.getProducts();

  private cartService = inject(CartService);
  private destroyRef = inject(DestroyRef);

  ngOnInit() {
    this.productService.loadProducts();
  }

  onSelectBuyNow(product: Product){
    this.cartService.addToCart(product);
  }




}
