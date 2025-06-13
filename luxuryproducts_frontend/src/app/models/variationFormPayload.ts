export interface VariationFormPayload {

    productId: number;
    baseSku: string;
    price: number;
    stock: number;
    variations: [{
        variationName: string,
        value: string,
    }]

}
