import {Component, inject, Input, OnInit} from '@angular/core';
import {Product} from '../../models/product';
import {CartService} from '../../services/cart.service';
import {CartItem} from '../../models/cartItem';

@Component({
  selector: 'app-cart-item',
  imports: [],
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

  removeItem(productID: number) {
    this.cartService.removeFromCart(productID)
  }

  increaseQuantity(productId: number) {
    this.cartService.increaseQuantity(productId)
  }
  decreaseQuantity(productId: number) {
    this.cartService.decreaseQuantity(productId)
  }


}
