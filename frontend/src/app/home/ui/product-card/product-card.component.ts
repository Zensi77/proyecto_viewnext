import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Input,
} from '@angular/core';
import { Product } from '../../interfaces/Data.interface';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SharedDataService } from '../../../shared/services/shared-data.service';

@Component({
  selector: 'product-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div
      class="h-full overflow-hidden rounded-lg border border-gray-200 bg-white p-4 md:p-6 shadow-sm hover:shadow-lg transition-shadow duration-300"
    >
      <a
        [routerLink]="['/product', product.id]"
        class="block overflow-hidden rounded text-center"
      >
        <img
          class="mx-auto h-32 w-32 sm:h-40 sm:w-40 md:h-44 md:w-44 hover:scale-105 transition-transform duration-300"
          [src]="product.image"
          [alt]="product.name"
        />
      </a>
      <div class="h-auto md:h-24">
        <a
          [routerLink]="['/product', product.id]"
          class="text-base sm:text-lg font-semibold leading-tight text-gray-900 hover:underline line-clamp-2"
          >{{ product.name }}</a
        >
        <p
          class="mt-2 text-sm sm:text-base font-normal text-gray-500 line-clamp-2"
        >
          {{ product.description }}
        </p>
      </div>
      <div>
        <p
          class="text-lg sm:text-xl mt-4 md:mt-6 font-bold leading-tight text-gray-900"
        >
          {{ product.price | currency : 'EUR' }}
        </p>
      </div>
      <div class="mt-4 md:mt-6 flex flex-col sm:flex-row items-center gap-2.5">
        <button
          data-tooltip-target="favourites-tooltip-3"
          type="button"
          class="w-full sm:w-auto inline-flex items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white p-2.5 text-sm font-medium text-gray-900 hover:bg-gray-100 hover:text-primary-700 focus:z-10 focus:outline-none focus:ring-4 focus:ring-gray-100"
        >
          <svg
            class="h-5 w-5"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 6C6.5 1 1 8 5.8 13l6.2 7 6.2-7C23 8 17.5 1 12 6Z"
            ></path>
          </svg>
        </button>
        <div
          id="favourites-tooltip-3"
          role="tooltip"
          class="tooltip invisible absolute z-10 h-full inline-block rounded-lg bg-gray-900 px-3 py-2 text-sm font-medium text-white opacity-0 shadow-sm transition-opacity duration-300 dark:bg-gray-700"
        >
          Añadir a favoritos
          <div class="tooltip-arrow" data-popper-arrow></div>
        </div>

        <button
          (click)="addToCart(product.id)"
          type="button"
          class="w-full inline-flex items-center justify-center rounded-lg bg-primary-700 px-3 sm:px-5 py-2.5 text-sm font-medium text-white hover:bg-primary-800 focus:outline-none focus:ring-4 focus:ring-primary-300 dark:bg-primary-600 dark:hover:bg-primary-700 dark:focus:ring-primary-800"
        >
          <svg
            class="-ms-1 me-2 h-4 w-4 sm:h-5 sm:w-5"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            fill="none"
            viewBox="0 0 24 24"
          >
            <path
              stroke="currentColor"
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M5 4h1.5L9 16m0 0h8m-8 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4Zm8 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4Zm-8.5-3h9.25L19 7h-1M8 7h-.688M13 5v4m-2-2h4"
            />
          </svg>
          Añadir al carrito
        </button>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductCardComponent {
  private readonly _sharedService = inject(SharedDataService);
  private readonly _router = inject(Router);

  @Input({ required: true }) product!: Product;

  addToCart(productId: string) {
    if (this._sharedService.cart() === null) {
      this._router.navigate(['/auth/sign-in']);
      return;
    }

    this._sharedService.addProductToCart(productId);
  }
}
