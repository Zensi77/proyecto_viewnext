import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Product } from '../../interfaces/Data.interface';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'product-card',
  imports: [CommonModule, RouterLink],
  template: `
    <div
      class="w-full max-w-sm min-h-full bg-white border border-gray-200 rounded-xl shadow hover:shadow-lg transition-all duration-300 flex flex-col"
    >
      <a [routerLink]="['product', product!.id]" class="relative">
        <img
          class="rounded-t-xl h-64 w-full object-fill"
          [src]="product.image"
          [alt]="product.name"
        />
        <span
          *ngIf="product?.stock === 0"
          class="absolute top-2 right-2 bg-red-500 text-white text-xs px-2 py-1 rounded"
        >
          Sin stock
        </span>
      </a>

      <div class="flex flex-col flex-grow p-5">
        <h5 class="text-lg font-bold text-gray-900 mb-1">
          {{ product.name }}
        </h5>

        <p class="text-sm text-gray-600 mb-2 line-clamp-2">
          {{ product.description }}
        </p>

        <p class="text-xs text-gray-500 mb-4">
          Categoría:
          <span class="font-medium">{{ product.category.name }}</span
          ><br />
          Proveedor:
          <span class="font-medium">{{ product.provider.name }}</span>
        </p>

        <div class="flex items-center justify-between mt-auto">
          <span class="text-xl font-bold text-gray-800">
            {{ product.price | currency : 'EUR' }}
          </span>

          <button
            [disabled]="product.stock === 0"
            class="bg-blue-600 hover:bg-blue-700 text-white font-medium cursor-pointer rounded-lg text-sm px-4 py-2 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Añadir al carrito
          </button>
        </div>
      </div>
    </div>
  `,
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductCardComponent {
  @Input({ required: true }) product!: Product;
}
