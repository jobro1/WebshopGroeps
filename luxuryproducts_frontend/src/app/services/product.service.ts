import {inject, Injectable,signal} from '@angular/core';
import {Product} from '../models/product';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import { Productvariation } from '../models/Productvariation';
import { Observable } from 'rxjs';

interface CreateProductVariationDTO {
  sku: string;
  price: number;
  stock: number;
  imageUrl: string;
  productId: number;
  values: {
    variationName: string;
    value: string;
  }[];
}


@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private products = signal<Product[]>([]);
  private httpClient=  inject(HttpClient);
  private product = signal<Product | null> (null)

  public getProducts(){
    return this.products.asReadonly();
  }
  public getProduct() {
    return this.product.asReadonly();
  }

  public loadProductById(id:number){
      this.httpClient.get<Product>(environment.apiUrl + `/products/${id}`).subscribe({
        next: (data) => {
          console.log(data);
          this.product.set(data)
        },
        error: (err) => console.error('Error loading product:', err)
      });
  }

  public loadProducts() {
    this.httpClient.get<Product[]>(
      environment.apiUrl +'/products',
    ).subscribe({
        next: (responseData) => {
          console.log(responseData);
          this.products.set(responseData);
        }
      }
    )
  }

  updateProductVariation(variation: Productvariation) {
    const payload = {
      productVariationId: variation.productVariationId,
      sku: variation.sku,
      price: variation.price,
      imageUrl: variation.imageUrl,
      stock: variation.stock,
      values: variation.values.map(v => ({
        variationValueId: v.variationValueId,
        value: v.value,
      })),
    };

    return this.httpClient.put(`${environment.apiUrl}/products/admin/variation`, payload);
  }

  deleteProductVariation(variationId: number) {
    return this.httpClient.delete(`${environment.apiUrl}/products/admin/variation/${variationId}`);
  }

  createProductVariation(variation: CreateProductVariationDTO): Observable<unknown> {
    return this.httpClient.post(`${environment.apiUrl}/products/admin/variation/create`, variation);
  }

  public isMatchingVariation(
      variation: Productvariation,
      name: string,
      option: string,
      selectedValues: Record<string, string>
  ): boolean {
    if (variation.stock === 0) return false;

    return variation.values.every((v) => {
      if (v.variation.variationName === name) {
        return v.value === option;
      }
      const selected = selectedValues[v.variation.variationName];
      return selected ? v.value === selected : true;
    });
  }


}
