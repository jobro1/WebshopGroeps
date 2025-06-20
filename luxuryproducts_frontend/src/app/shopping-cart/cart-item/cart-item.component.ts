import {Component, inject, Input, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {CartItem} from '../../models/cartItem';
import {TranslatePipe} from "@ngx-translate/core";
import {Productvariation} from '../../models/Productvariation';
import {ProductService} from '../../services/product.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cart-item',
  standalone: true,
  imports: [
    TranslatePipe,
    CommonModule
  ],
  templateUrl: './cart-item.component.html',
  styleUrl: './cart-item.component.scss'
})
export class CartItemComponent implements OnInit {
  @Input({required:true}) item!: CartItem;
  private cartService = inject(CartService);
  private productService = inject(ProductService);
  protected cartItems = this.cartService.getCartItems();

  ngOnInit() {
    this.cartService.loadCartFromLocalStorage();
  }

  removeItem(productSku: string) {
    if (productSku) {
      this.cartService.removeFromCart(productSku);
    }
  }

  increaseQuantity(productSku: string) {
    if (productSku) {
      this.cartService.increaseQuantity(productSku);
    }
  }

  decreaseQuantity(productSku: string) {
    if (productSku) {
      this.cartService.decreaseQuantity(productSku);
    }
  }

  getProductName(variation: Productvariation | undefined): string {
    if (!variation) return 'Product Not Found';
    
    if (variation.sku.startsWith('GC')) {
      const value = variation.sku.match(/GC(\d+)/)?.[1];
      return value ? `Cadeaubon €${value}` : variation.sku;
    }

    if (variation.values?.length) {
      const nameValues = variation.values
        .filter(v => v && v.value)
        .map(v => v.value)
        .join(' - ');
      return nameValues || variation.sku;
    }

    return variation.sku;
  }

  getImageUrl(variation: Productvariation | undefined): string {
    if (!variation) return '/assets/placeholder.png';
    
    if (variation.imageUrl?.startsWith('/assets')) {
      return variation.imageUrl;
    }
    
    if (variation.imageUrl?.startsWith('http')) {
      return variation.imageUrl;
    }
    
    return '/assets/placeholder.png';
  }
}
