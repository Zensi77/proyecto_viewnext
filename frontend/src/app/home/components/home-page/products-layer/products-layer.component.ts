import { Component, inject } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { CommonModule } from '@angular/common';
import { Product } from '../../../interfaces/Data.interface';
import { HomeService } from '../../../services/home.service';
import { ProductCardComponent } from '../../../ui/product-card/product-card.component';

@Component({
  selector: 'app-products-layer',
  imports: [
    ButtonModule,
    CommonModule,
    ProductCardComponent,
    ProductCardComponent,
  ],
  templateUrl: './products-layer.component.html',
  styles: ``,
})
export class ProductsLayerComponent {
  private readonly _homeService = inject(HomeService);

  products: Product[] = [];

  constructor() {
    this._homeService.getRandomProducts(8).subscribe((data: Product[]) => {
      this.products = data;
    });
  }
}
