import { Component } from '@angular/core';
import {HeaderComponent} from "../header/header.component";
import {SpecialProductComponent} from "../products/special-product/special-product.component";
import {HomeCategoriesComponent} from '../categories/home-categories/home-categories.component';
import {HomeNewProductsComponent} from '../products/home-new-products/home-new-products.component';
import {SwitchLanguageComponent} from "../header/switch-language/switch-language.component";

@Component({
  selector: 'app-home',
    imports: [
        SpecialProductComponent,
        HomeCategoriesComponent,
        HomeNewProductsComponent,

    ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
