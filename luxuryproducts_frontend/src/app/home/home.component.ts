import { Component } from '@angular/core';
import {SpecialProductComponent} from "../products/special-product/special-product.component";
import {HomeCategoriesComponent} from '../categories/home-categories/home-categories.component';
import {AllProductsComponent} from "../pages/all-products/all-products.component";

@Component({
  selector: 'app-home',
    imports: [
        SpecialProductComponent,
        HomeCategoriesComponent,
        AllProductsComponent,
    ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
