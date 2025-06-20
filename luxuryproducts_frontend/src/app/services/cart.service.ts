import {effect, inject, Injectable, signal} from '@angular/core';
import {CartItem} from '../models/cartItem';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/customUser';
import {OrderService} from './order.service';
import {Productvariation} from "../models/Productvariation";
import {OrderDTO} from "../models/dtos/orderDTO";
import {GiftCardService} from "./gift-card.service";
import {firstValueFrom} from "rxjs";
import {GiftcardStatus} from "../models/giftcard";


@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItems= signal<CartItem[]>(this.loadCartFromLocalStorage());
  private orderService = inject(OrderService);
  private httpClient = inject(HttpClient);
  private giftCardService = inject(GiftCardService);


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
      if (variation.sku.startsWith('GC')) {
        existingItem.quantity += 1;
      } else if (existingItem.quantity < variation.stock) {
        existingItem.quantity += 1;
      }
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
    const cart = this.cartItems();
    const item = cart.find(item => item.productVariation.sku === productSku);
    
    if (item) {
      if (item.productVariation.sku.startsWith('GC')) {
        this.cartItems.set(
          cart.map(cartItem =>
            cartItem.productVariation.sku === productSku 
              ? { ...cartItem, quantity: cartItem.quantity + 1 } 
              : cartItem
          )
        );
      } else if (item.quantity < item.productVariation.stock) {
        this.cartItems.set(
          cart.map(cartItem =>
            cartItem.productVariation.sku === productSku 
              ? { ...cartItem, quantity: cartItem.quantity + 1 } 
              : cartItem
          )
        );
      }
    }
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

  public createOrder(user: User, finalTotal: number) {
    const originalTotal = this.getCartCount();
    const currentDate = new Date().toISOString();
    const orderItems = [];

    for (const item of this.cartItems()) {
      orderItems.push({
        quantity: item.quantity,
        subtotal: item.productVariation.price * item.quantity,
        sku: item.productVariation.sku
      });
    }

    // Create order with the final total (after gift card application)
    const order: OrderDTO = {
      userId: user.userId!,
      totalPrice: Number(finalTotal.toFixed(2)),  // Ensure 2 decimal places
      date: currentDate,
      status: 'Pending',
      orderItems: orderItems,
    };

    return this.httpClient.post<OrderDTO>('http://localhost:8080/api/orders', order).subscribe({
      next: async (response) => {
        // console.log('Order created successfully with final total:', finalTotal);
        // console.log('Original total before gift cards:', originalTotal);
        this.clearCart();
      },
      error: (error) => {
        // console.error('Failed to create the order:', error);
        throw new Error(`Failed to create the order. Server responded with: ${error.message}`);
      }
    });
  }


  public async calculateTotalWithGiftCards(userId: number): Promise<number> {
    let total = this.getCartCount();

    try {
      const giftCards = await firstValueFrom(this.giftCardService.getMyGiftCards());
      const activeCards = giftCards.filter(card => 
        card.status === GiftcardStatus.ACTIVE && 
        card.currentBalance > 0 &&
        new Date(card.expirationDate) > new Date()
      );

      for (let giftCard of activeCards) {
        try {
          if (total <= 0) break;

          const amountToDeduct = Math.min(giftCard.currentBalance, total);
          
          // Update gift card balance in the database
          const newBalance = Number((giftCard.currentBalance - amountToDeduct).toFixed(2));
          await firstValueFrom(
            this.giftCardService.updateGiftCardBalance(giftCard.code, newBalance)
          );
          
          // Reduce the total by the amount deducted
          total = Number((total - amountToDeduct).toFixed(2));
          
          // console.log(`Applied gift card ${giftCard.code}: -€${amountToDeduct.toFixed(2)}`);
          // console.log(`New gift card balance: €${newBalance.toFixed(2)}`);

        } catch (error) {
          // console.error(`Failed to process gift card ${giftCard.code}:`, error);
          continue;
        }
      }

      return Number(Math.max(0, total).toFixed(2));
    } catch (error) {
      // console.error('Failed to process gift cards:', error);
      return Number(this.getCartCount().toFixed(2));
    }
  }

  private saveCartToLocalStorage(cart: CartItem[]) {
    localStorage.setItem('cart', JSON.stringify(cart));
  }
}
