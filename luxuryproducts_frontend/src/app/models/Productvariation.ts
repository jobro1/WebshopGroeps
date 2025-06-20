export interface Productvariation {
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
        }
    }]
}