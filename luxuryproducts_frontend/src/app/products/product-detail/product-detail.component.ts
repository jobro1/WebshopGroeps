import { Component, inject, OnInit, signal } from '@angular/core';
import { ProductService } from '../../services/product.service';
import { ActivatedRoute } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {TranslatePipe} from "@ngx-translate/core";

// Interfaces for better typing
interface ProductVariationValue {
  variation: {
    variationName: string;
  };
  value: string;
}

interface Productvariation {
  values: ProductVariationValue[];
  stock: number;
  imageUrl: string;
  price: number;
  sku: string;
}

@Component({
  selector: 'app-product-detail',
    imports: [
        NgForOf,
        NgIf,
        FormsModule,
        TranslatePipe,
    ],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent implements OnInit {
  private productService = inject(ProductService);
  protected cartService = inject(CartService);
  private route = inject(ActivatedRoute);

  productId = signal<number | null>(null);
  protected product = this.productService.getProduct();
  selectedValues: Record<string, string> = {};
  selectedVariation: Productvariation | null = null;

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

  protected getVariationOptions(): Record<string, string[]> {
    const map: Record<string, Set<string>> = {};

    this.product()?.variations.forEach((variation) => {
      variation.values.forEach((value) => {
        const name = value.variation.variationName;
        if (!map[name]) map[name] = new Set();
        map[name].add(value.value);
      });
    });

    const result: Record<string, string[]> = {};
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
      (name) => !!this.selectedValues[name]
    );

    if (allSelected) {
      this.selectedVariation =
        this.product()?.variations.find((variation) => {
          return variation.values.every(
            (v) => this.selectedValues[v.variation.variationName] === v.value
          );
        }) || null;
    }
  }

  getFilteredOptions(name: string): string[] {
    if (!this.product()) return [];

    const variations = this.product()!.variations;
    const allOptions = this.getVariationOptions()[name];

    return allOptions.filter((option) => {
      return variations.some((variation) => {
        if (variation.stock === 0) return false;

        return variation.values.every((v) => {
          if (v.variation.variationName === name) {
            return v.value === option;
          }
          const selected = this.selectedValues[v.variation.variationName];
          return selected ? v.value === selected : true;
        });
      });
    });
  }

  public toSingleValueTuple(variation: Productvariation): {
    sku: string;
    price: number;
    imageUrl: string;
    stock: number;
    values: [{
      variationValueId: number;
      value: string;
      variation: {
        variationId: number;
        variationName: string;
      };
    }];
  } {
    const firstValue = variation.values[0];

    // You must cast this because we're transforming to the expected structure
    return {
      sku: variation.sku,
      price: variation.price,
      imageUrl: variation.imageUrl,
      stock: variation.stock,
      values: [firstValue] as [{
        variationValueId: number;
        value: string;
        variation: {
          variationId: number;
          variationName: string;
        };
      }],
    };
  }

}
