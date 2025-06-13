import {Productvariation} from "./Productvariation";

export interface OrderItem {
  orderItemId: number;
  quantity: number;
  subtotal: number;
  productVariation: Productvariation
}
