export interface Provider {
  id: string;
  name: string;
  address: string;
}

export interface Category {
  id: string;
  name: string;
}

export interface Product {
  id: number;
  name: string;
  price: number;
  image: string;
  description: string;
  stock: number;
  provider: Provider;
  category: Category;
}

export interface SearchProduct {
  page: number;
  size: number;
  sortBy: string;
  orderBy: string;
  filterProvider: string[];
  filterCategory: string[];
  filterPriceMin: number;
  filterPriceMax: number;
  filterName: string;
}

export interface SearchProductResponse {
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  sortBy: string;
  orderBy: string;
  filterBy: string;
  filterValue: string;
  hasNext: boolean;
  hasPrevious: boolean;
  products: Product[];
}

interface ProductOrder {
  id: number;
  quantity: number;
}

export enum PaymentMethod {
  CREDIT_CARD = 'CREDIT_CARD',
  PAYPAL = 'PAYPAL',
  GOOGLE_PAY = 'GOOGLE_PAY',
  APPLE_PAY = 'APPLE_PAY',
}

export interface CreateOrder {
  products: ProductOrder[];
  paymentMethod: PaymentMethod;
}
