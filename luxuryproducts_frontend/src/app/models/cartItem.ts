import {Productvariation} from "./Productvariation";

export interface CartItem {
  productVariation: Productvariation;
  quantity: number;
}
