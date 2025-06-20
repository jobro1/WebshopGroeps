import {OrderItem} from './orderItem';

export interface Order {
  orderId?: number;
  totalPrice: number;
  date: string;
  status: string;
  userId: number;
  orderItems?: OrderItem[];
}
