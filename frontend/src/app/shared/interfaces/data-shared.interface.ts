import { Product } from '../../home/interfaces/Data.interface';

export interface CartResponse {
  cart_id: number;
  products: ProductCart[];
  totalPrice: number;
}

export interface ProductCart {
  product: Product;
  quantity: number;
}
