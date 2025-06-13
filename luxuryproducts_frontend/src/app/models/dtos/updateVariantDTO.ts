export interface UpdateVariantDTO {
    sku: string;
    price: number;
    stock: number;
    variations:{
        variationValueId: number
        value: string;
        variationName: string;
    }[];
}