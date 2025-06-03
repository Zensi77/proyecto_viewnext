import { Component, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Product } from '../../interfaces/Data.interface';
import { HomeService } from '../../services/home.service';
import { CommonModule } from '@angular/common';
import { Ripple } from 'primeng/ripple';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';
import { SharedDataService } from '../../../shared/services/shared-data.service';

@Component({
  selector: 'app-product-page',
  imports: [CommonModule, Ripple, RouterLink, FormsModule, InputNumberModule],
  templateUrl: './product-page.component.html',
  styles: `
  /* HTML: <div class="loader"></div> https://css-loaders.com*/
.loader {
  display: inline-flex;
  gap: 10px;
}
.loader:before,
.loader:after {
  content: "";
  height: 50px;
  aspect-ratio: 1;
  border-radius: 50%;
  background:
    linear-gradient(#222 0 0) top/100% 40% no-repeat,
    radial-gradient(farthest-side,#000 95%,#0000) 50%/8px 8px no-repeat
    #fff;
  animation: l7 1.5s infinite alternate ease-in;
}
@keyframes l7 {
  0%,
  70% {background-size:100% 40%,8px 8px}
  85% {background-size:100% 120%,8px 8px}
  100% {background-size:100% 40%,8px 8px}
}
  `,
})
export default class ProductPageComponent {
  private readonly _route = inject(ActivatedRoute);
  private readonly _homeService = inject(HomeService);
  private readonly _sharedService = inject(SharedDataService);

  isLoading = false;

  product!: Product;
  id = signal<string>('');
  quantity: number = 1;

  constructor() {
    this.isLoading = true;
    this._route.params.subscribe((params) => {
      this.id.set(params['id']);
    });

    effect(() => {
      if (this.id()) {
        this._homeService.getProduct(this.id()).subscribe((res: Product) => {
          this.product = res;
          this.isLoading = false;
        });
      }
    });
  }

  addToCart() {
    this._sharedService.addProductToCart(this.product.id, this.quantity);
  }
}
