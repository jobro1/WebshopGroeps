import {Productvariation} from "./Productvariation";

export interface OrderItem {
  orderItemId: number;
  quantity: number;
  subtotal: number;

  sku: string;
  priceAtOrder: number;
  variationSummary: string;
  imageUrlAtOrder: string;
}
