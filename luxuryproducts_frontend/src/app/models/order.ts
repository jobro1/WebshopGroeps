import {OrderItem} from './orderItem';
import {User} from './customUser';

export interface Order {
  orderId?: number;
  totalPrice: number;
  date: string;
  status: string;
  userId: number;
  orderItems?: OrderItem[];
}
