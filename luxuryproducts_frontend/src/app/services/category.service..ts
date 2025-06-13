import {inject, Injectable,signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {ProductCategory} from '../models/productCategory';
import {Product} from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private categories = signal<ProductCategory[]>([]);
  private httpClient=  inject(HttpClient);
  private category = signal<ProductCategory | null>(null);

  getAllCategories(){
    return this.categories.asReadonly()
  }
  getCategory(){
    return this.category.asReadonly();
  }

  public loadCategoryById(id:number){
    this.httpClient.get<ProductCategory>(environment.apiUrl +`/productCategories/${id}`).subscribe({
      next: (resdata) => {
        console.log(resdata);
        this.category.set(resdata)
      },
      error: (err) => console.error('Error loading product:', err)
    });


  }

  public loadCategories() {
    this.httpClient.get<ProductCategory[]>(environment.apiUrl + '/productCategories').subscribe({
      next: (data) => {
        console.log(data);
        this.categories.set(data);
      },
      error: (err) => console.error('Error loading product:', err)
    });
  }


}
