import {
  Component,
  OnInit,
  inject,
  signal,
  effect,
  runInInjectionContext,
  Injector,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgForOf, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../services/product.service';
import { CartService } from '../../../services/cart.service';

interface ProductVariationValue {
  variation: {
    variationName: string;
    variationId: number;
  };
  value: string;
  variationValueId: number;
}

interface ProductVariation {
  productVariationId: number;
  values: ProductVariationValue[];
  stock: number;
  imageUrl: string;
  price: number;
  sku: string;
}

interface Product {
  product_id: number;
  name: string;
  variations: ProductVariation[];
}

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

@Component({
  selector: 'app-edit-page',
  imports: [NgForOf, NgIf, FormsModule],
  templateUrl: './edit-page.component.html',
  styleUrl: './edit-page.component.scss',
})
export class EditPageComponent implements OnInit {
  private readonly productService = inject(ProductService);
  public readonly cartService = inject(CartService);
  private readonly route = inject(ActivatedRoute);
  private readonly injector = inject(Injector);

  public readonly product = signal<Product | null>(null);
  public readonly productId = signal<number | null>(null);

  public selectedValues: Record<string, string> = {};
  public selectedVariation: ProductVariation | null = null;
  private cachedOptions?: Record<string, string[]>;

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = +(params.get('productId') || 0);
      if (id) {
        this.productId.set(id);
        this.productService.loadProductById(id);
        this.cachedOptions = undefined;

       runInInjectionContext(this.injector, () => {
          effect(() => {
            const prod = this.productService.getProduct()();
            this.product.set(prod);
            this.cachedOptions = undefined;

            if (prod && prod.variations.length > 0) {
              const first = prod.variations.find((v) => v.stock > 0);
              if (first) {
                this.selectedValues = {};
                first.values.forEach((v) => {
                  this.selectedValues[v.variation.variationName] = v.value;
                });
                this.selectedVariation = first;
              }
            }
          });
        });
      }
    });
  }

  public getVariationOptions(): Record<string, string[]> {
    if (this.cachedOptions) return this.cachedOptions;

    const map: Record<string, Set<string>> = {};
    const product = this.product();
    if (!product) return {};

    product.variations.forEach((variation) => {
      variation.values.forEach((v) => {
        const name = v.variation.variationName;
        if (!map[name]) map[name] = new Set();
        map[name].add(v.value);
      });
    });

    const result: Record<string, string[]> = {};
    for (const key in map) {
      result[key] = Array.from(map[key]);
    }

    this.cachedOptions = result;
    return result;
  }

  public variationNames(): string[] {
    return Object.keys(this.getVariationOptions());
  }

  public getFilteredOptions(name: string): string[] {
    const product = this.product();
    if (!product) return [];

    const variations = product.variations;
    const allOptions = this.getVariationOptions()[name] || [];

    return allOptions.filter((option) =>
      variations.some((variation) => {
        if (variation.stock === 0) return false;

        return variation.values.every((v) => {
          if (v.variation.variationName === name) {
            return v.value === option;
          }
          const selected = this.selectedValues[v.variation.variationName];
          return selected ? v.value === selected : true;
        });
      })
    );
  }

  public onSelectValue(variationName: string, value: string): void {
    this.selectedValues[variationName] = value;

    const allSelected = this.variationNames().every(
      (name) => !!this.selectedValues[name]
    );

    if (allSelected) {
      const product = this.product();
      if (!product) return;

      this.selectedVariation =
        product.variations.find((variation) =>
          variation.values.every(
            (v) => this.selectedValues[v.variation.variationName] === v.value
          )
        ) || null;
    }
  }

  public saveVariationChanges(): void {
    if (!this.selectedVariation) return;

    this.productService.updateProductVariation(this.selectedVariation).subscribe({
      next: (response) => {
        console.log('Update successful:', response);
        alert('Variation updated successfully!');
      },
      error: (err) => {
        console.error('Update failed:', err);
        alert('Failed to update variation.');
      }
    });
  }

  deleteVariation(variationId: number) {
    this.productService.deleteProductVariation(variationId).subscribe({
      next: () => {
        window.location.reload(); // Reload product to update UI
      },
      error: (err) => console.error('Delete failed', err)
    });
  }
  
  public showCreateForm = false;

  public newVariation: Partial<ProductVariation> = {
    sku: '',
    price: 0,
    stock: 0,
    imageUrl: '',
    values: []
  };

  public toggleCreateForm() {
    this.showCreateForm = !this.showCreateForm;

    if (this.showCreateForm) {
      this.newVariation = {
        sku: '',
        price: 0,
        stock: 0,
        imageUrl: '',
        values: []
      };
    }
  }


  public createVariation(): void {
    const product = this.product();
    if (!product || !this.newVariation.values) return;

    const dto: CreateProductVariationDTO = {
      sku: this.newVariation.sku!,
      price: this.newVariation.price!,
      stock: this.newVariation.stock!,
      imageUrl: this.newVariation.imageUrl!,
      productId: product.product_id,
      values: this.newVariation.values!.map(v => ({
        variationName: v.variation.variationName,
        value: v.value,
      }))
    };

    this.productService.createProductVariation(dto).subscribe({
      next: () => window.location.reload(),
      error: (err) => {
        console.error('Create failed', err);
        alert('Failed to create variation.');
      }
    });
  }

  getVariationValueId(variationName: string, value: string): number | null {
    const product = this.product();
    if (!product) return null;

    const match = product.variations
      .flatMap(v => v.values)
      .find(val => val.variation.variationName === variationName && val.value === value);

    return match ? match.variationValueId : null;
  }

  addVariationField() {
    this.newVariation.values?.push({
      variation: { variationName: '', variationId: 0 },
      value: '',
      variationValueId: 0
    });
  }

  removeVariationField(index: number) {
    this.newVariation.values?.splice(index, 1);
  }
}
