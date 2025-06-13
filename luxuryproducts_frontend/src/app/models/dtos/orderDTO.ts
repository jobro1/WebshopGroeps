import {OrderItemDTO} from "./orderItemDTO";

export interface OrderDTO {
    orderId?: number;
    totalPrice: number;
    date: string;
    status: string;
    userId: number;
    orderItems?: OrderItemDTO[];
}
