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
  id: string;
  name: string;
  price: number;
  image: string;
  description: string;
  stock: number;
  provider: Provider;
  category: Category;
  liked: boolean;
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
