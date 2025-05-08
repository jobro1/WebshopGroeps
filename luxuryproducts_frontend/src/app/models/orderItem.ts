import {Product} from './product';

export interface OrderItem {
  orderItemId: number;
  quantity: number;
  productId: number;
  subtotal: number;
  product?: Product
}
