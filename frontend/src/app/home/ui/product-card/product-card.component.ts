import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Input,
  signal,
} from '@angular/core';
import { Product } from '../../interfaces/Data.interface';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { HomeService } from '../../services/home.service';

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

      <!-- Botones -->
      <div class="mt-4 md:mt-6 flex flex-col lg:flex-row items-center gap-2.5">
        <button
          (click)="updateWishList(product.id)"
          type="button"
          aria-label="Añadir a favoritos"
          class="w-full text-red-500 lg:w-auto inline-flex items-center justify-center gap-2 rounded-lg border border-gray-200 bg-white p-2.5 text-sm font-medium text-gray-900 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200"
          [ngClass]="{
            'animate__animated animate__heartBeat': product.liked,
            'text-red-500': product.liked,
          }"
          data-tooltip-target="favourites-tooltip-3"
        >
          <svg
            class="h-5 w-5"
            xmlns="http://www.w3.org/2000/svg"
            [attr.fill]="product.liked ? 'currentColor' : 'none'"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M12 6C6.5 1 1 8 5.8 13l6.2 7 6.2-7C23 8 17.5 1 12 6Z"
            />
          </svg>
        </button>
        <div
          id="favourites-tooltip-3"
          role="tooltip"
          class="tooltip hidden absolute z-10 rounded-lg bg-gray-900 px-3 py-2 text-sm text-white shadow-sm"
        >
          Añadir a favoritos
        </div>

        <!-- Añadir al carrito -->
        <button
          (click)="addToCart(product.id)"
          type="button"
          class="w-full grow-1 lg:w-auto inline-flex items-center justify-center gap-2 rounded-lg bg-primary-600 px-4 sm:px-5 py-2.5 cursor-pointer text-sm font-medium text-white hover:bg-primary-700 transition-colors duration-300 "
          [ngClass]="{
            'animate__animated animate__heartBeat ': buttonClicked()
          }"
        >
          <svg
            class="h-5 w-5"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M5 4h1.5L9 16h8m-8 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4Zm8 0a2 2 0 1 0 0 4 2 2 0 0 0 0-4Zm-8.5-3h9.25L19 7h-1M8 7H7.312M13 5v4m-2-2h4"
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
  private readonly _homeService = inject(HomeService);
  private readonly _sharedService = inject(SharedDataService);
  private readonly _router = inject(Router);

  @Input({ required: true }) product!: Product;

  buttonClicked = signal(false);

  addToCart(productId: string) {
    this.buttonClicked.set(true);
    setTimeout(() => {
      this.buttonClicked.set(false);
    }, 1000);

    if (this._sharedService.cart() === null) {
      this._router.navigate(['/auth/sign-in']);
      return;
    }

    this._sharedService.addProductToCart(productId);
  }

  updateWishList(productId: string) {
    this.product.liked = !this.product.liked;
    this._homeService.modifyWishList(productId);
  }
}
