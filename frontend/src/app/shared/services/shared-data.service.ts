import { effect, inject, Injectable, signal } from '@angular/core';
import { ProductName } from '../../home/interfaces/ProductName.interface';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { AuthService } from '../../auth/services/auth.service';
import { CartResponse } from '../interfaces/data-shared.interface';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  private readonly _http = inject(HttpClient);
  private readonly _authService = inject(AuthService);

  productNames = signal<ProductName[]>([]);
  cart = signal<CartResponse | null>(null);

  constructor() {
    this.getProductsNames();
    this.getCart();
  }

  getCart() {
    const url = environment.get_cart;
    return this._http.get<CartResponse>(url).subscribe(
      (res) => {
        console.log(res);

        this.cart.set(res);
      },
      (err) => {
        console.error(err);
        if (err.status === 500) {
          Swal.fire({
            icon: 'error',
            text: 'Error, al obtener el carrito, por favor, inténtelo más tarde',
            showCloseButton: true,
            confirmButtonText: 'Aceptar',
          });
        }
      }
    );
  }

  setProductNames(names: ProductName[]) {
    this.productNames.set(names);
  }

  getProductsNames() {
    const url = environment.get_names_products;
    return this._http.get<ProductName[]>(url).subscribe((res) => {
      this.productNames.set(res);
    });
  }

  addProductToCart(productId: number, quantity: number = 1) {
    const url = environment.add_to_cart;
    const body = {
      cart_id: this.cart()!.cart_id,
      product_id: productId,
      quantity,
    };
    return this._http.post<CartResponse>(url, body).subscribe(
      (res) => {
        this.cart.set(res);
      },
      (err) => {
        console.error(err);

        if (err.status === 500) {
          Swal.fire({
            icon: 'error',
            text: 'Error, al añadir el producto al carrito, por favor, inténtelo más tarde',
            showCloseButton: true,
            confirmButtonText: 'Aceptar',
          });
        }
      }
    );
  }

  deleteProductFromCart(productId: number) {}
}
