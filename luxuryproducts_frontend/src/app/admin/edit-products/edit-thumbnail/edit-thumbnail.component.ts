import { Component, inject } from '@angular/core';
import { Input } from '@angular/core';
import { Product } from '../../../models/product';
import { RouterModule } from '@angular/router';
import { ProductService } from '../../../services/product.service';

@Component({
  selector: 'app-edit-thumbnail',
  imports: [RouterModule],
  templateUrl: './edit-thumbnail.component.html',
  styleUrl: './edit-thumbnail.component.scss'
})
export class EditThumbnailComponent {
  @Input({required: true}) product!: Product;
  private productService= inject(ProductService);
  protected products = this.productService.getProducts();
}
