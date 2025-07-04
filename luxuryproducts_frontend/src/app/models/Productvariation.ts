import {Value} from "./Value";

export interface Productvariation {
  productVariationId: number;
  sku: string;
  price: number;
  stock: number;
  imageUrl: string;
  values: Value[]
}