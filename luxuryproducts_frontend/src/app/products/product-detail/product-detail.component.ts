import {Component, inject, OnInit, signal} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {ActivatedRoute} from '@angular/router';
import {CartService} from '../../services/cart.service';
import {ImageSliderComponent} from '../image-slider/image-slider.component';

@Component({
  selector: 'app-product-detail',
  imports: [
    ImageSliderComponent
  ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {

  private productService = inject(ProductService);
  private cartService = inject(CartService);

  productId = signal<number | null>(null);
  private route = inject(ActivatedRoute);
  protected product = this.productService.getProduct();

  ngOnInit()  {

    this.route.paramMap.subscribe((params) => {
      const productId = params.get('productId');
      if (productId) {
        this.productId.set(+productId);
        this.productService.loadProductById(this.productId()!)
      }
    });
  }

  onSelectBuyNow(){
    this.cartService.addToCart(this.product()!);
  }


}
