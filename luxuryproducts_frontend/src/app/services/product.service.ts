  import {inject, Injectable,signal} from '@angular/core';
import {Product} from '../models/product';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {tap} from 'rxjs';

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
        next: (data) => this.product.set(data),
        error: (err) => console.error('Error loading product:', err)
      });


  }

  public loadProducts() {
    this.httpClient.get<Product[]>(
      environment.apiUrl +'/products',
    ).subscribe({
        next: (responseData) => {
          this.products.set(responseData);
        }
      }

    )
  }

  public searchProducts(query:string) {
    this.httpClient.get<Product[]>(environment.apiUrl + `/products/search?query=${query}`).pipe(tap({
      next: (data) => this.products.set(data),
      error: (err) => console.error('Error loading products:', err)

    }))
  }

}
