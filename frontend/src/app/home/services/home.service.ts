import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
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

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private readonly _router = inject(Router);
  private readonly _http = inject(HttpClient);

  getProductsNamesObs() {
    const url = environment['get-names-products'];
    return this._http.get<ProductName[]>(url);
  }

  getCategoryes() {
    const url = environment['get-all-categories'];
    return this._http.get<Category[]>(url);
  }

  getProviders() {
    const url = environment['get-all-providers'];
    return this._http.get<Provider[]>(url);
  }

  getRandomProducts() {
    const url = environment['get-random-products'];
    return this._http.get<Product[]>(url, {
      params: { quantity: '8' },
    });
  }

  searchProducts(search: SearchProduct) {
    console.log('search', search);

    const url = environment['get-all-products'];
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
}
