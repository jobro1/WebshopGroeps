import {Variation} from "./Variation";

export interface Value {
    variationValueId: number;
    value: string;
    variation: Variation;
}