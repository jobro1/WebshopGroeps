import {Component, computed, inject, OnInit} from '@angular/core';
import { RouterLink} from '@angular/router';
import {CartService} from '../../services/cart.service';
import {CartItemComponent} from '../cart-item/cart-item.component';
import {TranslatePipe} from "@ngx-translate/core";


@Component({
  selector: 'app-cart',
    imports: [RouterLink, CartItemComponent, TranslatePipe],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit {

  private cartService = inject(CartService);
  totalPrice = computed(() => this.cartService.getCartCount());
  protected cartItems= this.cartService.getCartItems();

  ngOnInit() {
    this.cartService.loadCartFromLocalStorage();
  }

  removeAllItemsFromCart() {
    this.cartService.clearCart();
  }


}
