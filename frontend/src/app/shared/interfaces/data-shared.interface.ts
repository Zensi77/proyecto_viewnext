import { Product } from '../../home/interfaces/Data.interface';

export interface CartResponse {
  cart_id: string;
  products: ProductCart[];
  totalPrice: number;
}

export interface ProductCart {
  product: Product;
  quantity: number;
}
