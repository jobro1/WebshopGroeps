import {Productvariation} from "./Productvariation";

export interface Product{
  product_id: number;
  name: string;
  price: number;
  description: string;
  brand: string;
  variations: Productvariation[]
}
