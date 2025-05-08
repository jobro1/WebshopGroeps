import {Product} from './product';

export interface ProductCategory {
  categoryId?: number;
  name: string;
  products?: Product[];
}
