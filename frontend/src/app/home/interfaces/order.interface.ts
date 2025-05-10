import { Product } from './Data.interface';

export interface ProductOrder {
  product: Product;
  quantity: number;
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  PAYPAL = 'PAYPAL',
  GOOGLE_PAY = 'GOOGLE_PAY',
  APPLE_PAY = 'APPLE_PAY',
}

export interface CreateOrder {
  productOrder: ProductOrder[];
  paymentMethod: PaymentMethod;
}

export interface OrderResponse {
  id: string;
  createdAt: string;
  totalPrice: number;
  paymentMethod: PaymentMethod;
  status: string;
  items: ProductOrder[];
}
