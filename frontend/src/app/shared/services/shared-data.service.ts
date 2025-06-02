import { effect, inject, Injectable, signal } from '@angular/core';
import { ProductName } from '../../home/interfaces/ProductName.interface';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { AuthService } from '../../auth/services/auth.service';
import { CartResponse, ProductCart } from '../interfaces/data-shared.interface';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  private readonly _http = inject(HttpClient);
  private readonly _authService = inject(AuthService);

  productNames = signal<ProductName[]>([]);
  cart = signal<CartResponse | null>(null);

  cartChangeCount = signal(0);

  constructor() {
    this.getProductsNames();

    effect(() => {
      if (this._authService.user()) {
        this.getCart();
      }
    });
  }

  getCart() {
    const url = environment.base_url + environment.get_cart;
    return this._http.get<CartResponse>(url).subscribe((res) => {
      this.cart.set(res);
    });
  }

  setProductNames(names: ProductName[]) {
    this.productNames.set(names);
  }

  getProductsNames() {
    const url = environment.base_url + environment.get_names_products;
    return this._http.get<ProductName[]>(url).subscribe((res) => {
      this.productNames.set(res);
    });
  }

  addProductToCart(productId: string, quantity: number = 1) {
    const url = environment.base_url + environment.add_to_cart;
    const body = {
      cart_id: this.cart()!.cart_id,
      product_id: productId,
      quantity,
    };
    return this._http.post<CartResponse>(url, body).subscribe(
      (res) => {
        this.cartChangeCount.update((count) => count + 1);
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

  deleteProductFromCart(productId: string) {
    const url = environment.base_url + environment.delete_product_from_cart;

    this._http
      .delete(url, {
        params: { product_id: productId },
      })
      .subscribe(() => this.getCart());
  }

  updateProductQuantity(productItem: ProductCart, quantity: number) {
    const url = environment.base_url + environment.modify_product_quantity;

    const body = {
      cart_id: this.cart()!.cart_id,
      product_id: productItem.product.id,
      quantity: productItem.quantity + quantity,
    };

    this._http.put<CartResponse>(url, body).subscribe(
      (res) => {
        this.cartChangeCount.update((count) => count + 1);
        this.cart.set(res);
      },
      (err) => {
        console.error(err);

        if (err.status === 500) {
          Swal.fire({
            icon: 'error',
            text: 'Error, al modificar la cantidad del producto, por favor, inténtelo más tarde',
            showCloseButton: true,
            confirmButtonText: 'Aceptar',
          });
        }
      }
    );
  }
}
