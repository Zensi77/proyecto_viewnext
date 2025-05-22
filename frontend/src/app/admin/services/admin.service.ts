import { inject, Injectable, signal } from '@angular/core';
import {
  Category,
  Product,
  Provider,
} from '../../home/interfaces/Data.interface';
import { User } from '../../auth/interfaces/user.interface';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { CreateProduct } from '../interfaces/admin-data.interface';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private readonly _http = inject(HttpClient);

  providers = signal<Provider[]>([]);
  loading = signal<boolean>(false);
  categories = signal<Category[]>([]);
  Users = signal<User[]>([]);

  constructor() {}

  getAllUsers() {
    const url = environment.base_url + environment.get_all_users;

    return this._http.get<User[]>(url);
  }

  updateUser(userId: string, user: User) {
    const url = environment.base_url + environment.update_user + userId;

    return this._http.put<User>(url, user);
  }

  createProduct(product: CreateProduct) {
    const url = environment.base_url + environment.create_product;

    return this._http.post<Product>(url, product);
  }

  deleteProduct(id: string) {
    const url = environment.base_url + environment.delete_product + id;

    return this._http.delete<Product>(url);
  }

  updateProduct(id: string, product: CreateProduct) {
    const url = environment.base_url + environment.update_product + id;

    return this._http.put<Product>(url, product);
  }

  createCategory(name: string) {
    const url = environment.base_url + environment.create_category;

    return this._http.post<Category>(url, { name });
  }

  updateCategory(id: string, category: Category) {
    const url = environment.base_url + environment.update_category + id;

    return this._http.put<Category>(url, category);
  }

  deleteCategory(id: string) {
    const url = environment.base_url + environment.delete_category + id;

    return this._http.delete<Category>(url);
  }

  createProvider(provider: Provider) {
    const url = environment.base_url + environment.create_provider;

    return this._http.post<Provider>(url, provider);
  }

  updateProvider(id: string, provider: Provider) {
    const url = environment.base_url + environment.update_provider + id;

    return this._http.put<Provider>(url, provider);
  }

  deleteProvider(id: string) {
    const url = environment.base_url + environment.delete_provider + id;

    return this._http.delete<Provider>(url);
  }
}
