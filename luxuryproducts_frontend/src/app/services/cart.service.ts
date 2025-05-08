import {effect, inject, Injectable, signal} from '@angular/core';
import {Product} from '../models/product';
import {CartItem} from '../models/cartItem';
import {Order} from '../models/order';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/customUser';
import {OrderService} from './order.service';


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

  public addToCart(product: Product) {
    const updateCart = [...this.cartItems()]
    const existingItem = updateCart.find(item => item.product.product_id === product.product_id);

    if (existingItem) {
      existingItem.quantity = existingItem.quantity + 1;

    }else{
      updateCart.push({id:Date.now(),product, quantity: 1})
    }
    this.cartItems.set(updateCart);
  }

  public removeFromCart(productId: number) {
    this.cartItems.set(this.cartItems().filter(item => item.product.product_id !== productId));
  }

  public increaseQuantity(productId: number) {
    this.cartItems.set(
      this.cartItems().map(item =>
        item.product.product_id === productId ? { ...item, quantity: item.quantity + 1 } : item
      )
    );
  }

  public decreaseQuantity(productId: number) {
    this.cartItems.set(
      this.cartItems()
        .map(item =>
          item.product.product_id === productId
            ? { ...item, quantity: item.quantity > 1 ? item.quantity - 1 : 1 }
            : item
        )
    );
  }

  public getCartCount() {
    return this.cartItems().reduce((total, item) => total + item.product.price * item.quantity,0)

  }

  public clearCart() {
    this.cartItems.set([]);
    localStorage.removeItem('cart');
  }

  public createOrder(user: User) {
    // if(user == null) {
    //   console.log("User is null")
    //   return;
    // }
    const totalPrice = this.getCartCount();
    const currentDate = new Date().toISOString();
    const orderItems = [];

    for (let item of this.cartItems()) {
      orderItems.push({
        orderItemId: item.id,
        productId: item.product.product_id!,
        quantity: item.quantity,
        subtotal: item.product.price * item.quantity,
      });
    }

    const order: Order = {
      userId: user.userId!,
      totalPrice:totalPrice,
      date: currentDate,
      status: 'Pending',
      orderItems: orderItems,
    };

    this.httpClient.post<Order>('http://localhost:8080/api/orders', order).subscribe({
      next: (response) => {
      console.log('Order created successfully!', response);
      this.clearCart();
      this.orderService.updateOrder(order);
        // alert(response);
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
