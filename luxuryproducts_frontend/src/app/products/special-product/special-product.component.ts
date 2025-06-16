import { Component } from '@angular/core';
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-special-product',
    imports: [
        TranslatePipe
    ],
  templateUrl: './special-product.component.html',
  styleUrl: './special-product.component.scss'
})
export class SpecialProductComponent {

}
