import {inject, Injectable,signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Order} from '../models/order';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private httpClient = inject(HttpClient);
  private orders = signal<Order[]>([]);

  getOrders() {
    return this.orders.asReadonly()
  }

  public loadOrdersByUserId(userId: number) {
    this.httpClient.get<Order[]>(`${environment.apiUrl}/orders?userId=${userId}`).subscribe({
    next: (orders) => {
      this.orders.set(orders);
    },
    error: (error) => console.error('error loading orders',error)
      }
    )
  }


}
