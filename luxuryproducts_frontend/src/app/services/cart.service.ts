import {effect, inject, Injectable, signal} from '@angular/core';
import {CartItem} from '../models/cartItem';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/customUser';
import {OrderService} from './order.service';
import {Productvariation} from "../models/Productvariation";
import {OrderDTO} from "../models/dtos/orderDTO";


@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItems= signal<CartItem[]>(this.loadCartFromLocalStorage());
  private orderService = inject(OrderService);
  private httpClient = inject(HttpClient);


  constructor() {
    effect(() => {
      this.saveCartToLocalStorage(this.cartItems());
    });
  }

  public getCartItems(){
    return this.cartItems.asReadonly();
  }

  public loadCartFromLocalStorage(): CartItem[] {
    const savedCart = localStorage.getItem('cart');
    return savedCart ? JSON.parse(savedCart) : [];
  }

  public addToCart(variation: Productvariation) {
    const updateCart = [...this.cartItems()];
    const existingItem = updateCart.find(item => item.productVariation.sku === variation.sku);

    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      updateCart.push({
        productVariation: variation,
        quantity: 1
      });
    }

    this.cartItems.set(updateCart);
  }


  public removeFromCart(productSku: string) {
    this.cartItems.set(this.cartItems().filter(item => item.productVariation.sku !== productSku));
  }

  public increaseQuantity(productSku: string) {
    this.cartItems.set(
      this.cartItems().map(item =>
        item.productVariation.sku === productSku ? { ...item, quantity: item.quantity + 1 } : item
      )
    );
  }

  public decreaseQuantity(productSku: string) {
    this.cartItems.set(
      this.cartItems()
        .map(item =>
          item.productVariation.sku === productSku
            ? { ...item, quantity: item.quantity > 1 ? item.quantity - 1 : 1 }
            : item
        )
    );
  }

  public getCartCount() {
    return this.cartItems().reduce((total, item) => total + item.productVariation.price * item.quantity,0)

  }

  public clearCart() {
    this.cartItems.set([]);
    localStorage.removeItem('cart');
  }

  public createOrder(user: User) {
    const totalPrice = this.getCartCount();
    const currentDate = new Date().toISOString();
    const orderItems = [];

    for (const item of this.cartItems()) {
      orderItems.push({
        quantity: item.quantity,
        subtotal: item.productVariation.price * item.quantity,
        sku: item.productVariation.sku
      });
    }

    const order: OrderDTO = {
      userId: user.userId!,
      totalPrice:totalPrice,
      date: currentDate,
      status: 'Pending',
      orderItems: orderItems,
    };

    this.httpClient.post<OrderDTO>('http://localhost:8080/api/orders', order).subscribe({
      next: (response) => {
      console.log('Order created successfully!', response);
      this.clearCart();
    },
      error: (error) => {
      console.error('Failed to create the order:', error);
      alert(`Failed to create the order. Server responded with: ${error.message}`);
    }
    });
  }

  private saveCartToLocalStorage(cart: CartItem[]) {
    localStorage.setItem('cart', JSON.stringify(cart));
  }
}
