import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment.development';
import {
  Category,
  Product,
  Provider,
  SearchProduct,
  SearchProductResponse,
} from '../interfaces/Data.interface';
import { ProductName } from '../interfaces/ProductName.interface';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private readonly _router = inject(Router);
  private readonly _http = inject(HttpClient);

  categories = signal<Category[]>([]);
  providers = signal<Provider[]>([]);

  constructor() {
    this.getCategories();
    this.getProviders();
  }

  getProductsNamesObs() {
    const url = environment.get_names_products;
    return this._http.get<ProductName[]>(url);
  }

  getCategories() {
    const url = environment.get_all_categories;
    return this._http.get<Category[]>(url).subscribe((res: Category[]) => {
      this.categories.set(res.sort(() => Math.random() - 0.5));
    });
  }

  getProviders() {
    const url = environment.get_all_providers;
    return this._http.get<Provider[]>(url).subscribe((res: Provider[]) => {
      this.providers.set(res.sort(() => Math.random() - 0.5));
    });
  }

  getRandomProducts() {
    const url = environment.get_random_products;
    return this._http.get<Product[]>(url, {
      params: { quantity: '8' },
    });
  }

  searchProducts(search: SearchProduct) {
    const url = environment.get_all_products;
    let params = new HttpParams();

    params = params.append('page', search.page.toString());
    params = params.append('size', search.size.toString());
    params = params.append('sortBy', search.sortBy);
    params = params.append('orderBy', search.orderBy);

    if (search.filterProvider.length > 0) {
      search.filterProvider.forEach((provider) => {
        params = params.append('filterProvider', provider);
      });
    }

    if (search.filterCategory.length > 0) {
      search.filterCategory.forEach((category) => {
        params = params.append('filterCategory', category);
      });
    }

    params = params.append('filterPriceMin', search.filterPriceMin.toString());

    params = params.append('filterPriceMax', search.filterPriceMax.toString());

    if (search.filterName) {
      params = params.append('filterName', search.filterName);
    }

    return this._http.get<SearchProductResponse>(url, {
      params: params,
    });
  }

  getProduct(id: number) {
    const url = environment.get_product;

    return this._http.get<Product>(url + id);
  }
}
