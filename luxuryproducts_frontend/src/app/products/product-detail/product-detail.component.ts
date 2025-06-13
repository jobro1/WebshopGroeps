import {Component, inject, OnInit, signal} from '@angular/core';
import {ProductService} from '../../services/product.service';
import {ActivatedRoute} from '@angular/router';
import {CartService} from '../../services/cart.service';
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-product-detail',
  imports: [
      NgForOf,
      NgIf,
      FormsModule,
  ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {

  private productService = inject(ProductService);
  protected cartService = inject(CartService);
  private route = inject(ActivatedRoute);

  productId = signal<number | null>(null);
  protected product = this.productService.getProduct();
  selectedValues: { [variationName: string]: string } = {};
  selectedVariation: any | null = null;

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const productId = params.get('productId');
      if (productId) {
        this.productId.set(+productId);
        this.productService.loadProductById(this.productId()!);

        setTimeout(() => {
          const options = this.getVariationOptions();
          for (const name of Object.keys(options)) {
            this.selectedValues[name] = options[name][0];
          }
          this.onSelectValue('', '');
        }, 300);
      }
    });
  }

  protected getVariationOptions(): { [variationName: string]: string[] } {
    const map: { [variationName: string]: Set<string> } = {};

    this.product()?.variations.forEach(variation => {
      variation.values.forEach(value => {
        const name = value.variation.variationName;
        if (!map[name]) map[name] = new Set();
        map[name].add(value.value);
      });
    });

    const result: { [variationName: string]: string[] } = {};
    for (const key in map) {
      result[key] = Array.from(map[key]);
    }
    return result;
  }

  protected variationNames(): string[] {
    return Object.keys(this.getVariationOptions());
  }

  protected onSelectValue(variationName: string, value: string): void {
    this.selectedValues[variationName] = value;

    const allSelected = this.variationNames().every(
        name => !!this.selectedValues[name]
    );

    if (allSelected) {
      this.selectedVariation = this.product()?.variations.find(variation => {
        return variation.values.every(v =>
            this.selectedValues[v.variation.variationName] === v.value
        );
      }) || null;
    }
  }

  getFilteredOptions(name: string): string[] {
    if (!this.product()) return [];

    const variations = this.product()!.variations;
    const allOptions = this.getVariationOptions()[name];

    return allOptions.filter(option => {
      return variations.some(variation => {
        if (variation.stock === 0) return false;

        return variation.values.every(v => {
          if (v.variation.variationName === name) {
            return v.value === option;
          }
          const selected = this.selectedValues[v.variation.variationName];
          return selected ? v.value === selected : true;
        });
      });
    });
  }


}
