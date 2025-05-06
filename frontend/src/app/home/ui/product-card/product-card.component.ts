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
import Swal from 'sweetalert2';

@Component({
  selector: 'product-card',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div
      class="w-full max-w-sm bg-white border border-gray-200 rounded-2xl shadow-sm hover:shadow-lg transition duration-300 flex flex-col overflow-hidden"
    >
      <a [routerLink]="['/product', product!.id]" class="relative group">
        <div class="aspect-[4/3] w-full bg-gray-100 overflow-hidden">
          <img
            [src]="product.image"
            [alt]="product.name"
            loading="lazy"
            class="w-full h-full object-cover blur-[0.3px] group-hover:scale-105 transition-transform duration-300"
          />
        </div>

        <span
          *ngIf="product?.stock === 0"
          class="absolute top-2 right-2 bg-red-600 text-white text-xs font-semibold px-2 py-1 rounded-md shadow"
        >
          Sin stock
        </span>
      </a>

      <div class="flex flex-col flex-grow p-4">
        <h2 class="text-lg font-bold text-gray-900 truncate mb-1">
          {{ product.name }}
        </h2>

        <p class="text-sm text-gray-600 line-clamp-2 mb-3">
          {{ product.description }}
        </p>

        <p class="text-xs text-gray-500 leading-5 mb-4">
          <span class="block">
            <span class="font-semibold text-gray-700">Categoría:</span>
            {{ product.category.name }}
          </span>
          <span class="block">
            <span class="font-semibold text-gray-700">Proveedor:</span>
            {{ product.provider.name }}
          </span>
        </p>

        <div class="flex items-center justify-between mt-auto">
          <span class="text-xl font-semibold text-gray-800">
            {{ product.price | currency : 'EUR' }}
          </span>

          <button
            (click)="addToCart(product.id)"
            [disabled]="product.stock === 0"
            class="bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg text-sm px-4 py-2 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Añadir
          </button>
        </div>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductCardComponent {
  private readonly _sharedService = inject(SharedDataService);
  private readonly _router = inject(Router);

  @Input({ required: true }) product!: Product;

  addToCart(productId: number) {
    if (this._sharedService.cart() === null) {
      this._router.navigate(['/auth/sign-in']);
      return;
    }

    this._sharedService.addProductToCart(productId);
  }
}
