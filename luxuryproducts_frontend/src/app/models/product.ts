import {ProductCategory} from './productCategory';
import {ProductSpecifications} from './productSpecifications';
import {OrderItem} from './orderItem';
import {ImageUrl} from './imageUrl';

export interface Product{
  product_id?: number;
  name: string;
  manufacturer_code: string;
  price: number;
  description: string;
  brand: string;
  warranty: string;
  amount: number;
  productCategory?: ProductCategory;
  productSpecifications?: ProductSpecifications[];
  orderItems?: OrderItem[];
  imageUrls?: ImageUrl[];

}
