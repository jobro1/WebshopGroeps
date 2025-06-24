export interface Productvariation {
  productVariationId: number;
  sku: string;
  price: number;
  stock: number;
  imageUrl: string;
  values: {
    variationValueId: number;
    value: string;
    variation: {
      variationId: number;
      variationName: string;
    };
  }[];
}