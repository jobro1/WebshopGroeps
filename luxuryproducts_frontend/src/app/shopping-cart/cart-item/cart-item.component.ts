import {Component, inject, Input, OnInit} from '@angular/core';
import {Product} from '../../models/product';
import {CartService} from '../../services/cart.service';
import {CartItem} from '../../models/cartItem';
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-cart-item',
    imports: [
        TranslatePipe
    ],
  templateUrl: './cart-item.component.html',
  styleUrl: './cart-item.component.scss'
})
export class CartItemComponent implements OnInit {
  @Input({required:true}) item!: CartItem;
  private cartService = inject(CartService);
  protected cartItems = this.cartService.getCartItems();


  ngOnInit() {
    this.cartService.loadCartFromLocalStorage();
  }

  removeItem(productSku: string) {
    this.cartService.removeFromCart(productSku)
  }

  increaseQuantity(productSku: string) {
    this.cartService.increaseQuantity(productSku)
  }
  decreaseQuantity(productSku: string) {
    this.cartService.decreaseQuantity(productSku)
  }


}
