import { Component, computed, inject } from '@angular/core';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HomeService } from '../../services/home.service';
import { Product } from '../../interfaces/Data.interface';
import { ProductCardComponent } from '../../ui/product-card/product-card.component';

@Component({
  imports: [RouterLink, CommonModule, ProductCardComponent],
  templateUrl: './checkout-page.component.html',
  styles: ``,
})
export class CheckoutPageComponent {
  private readonly _homeService = inject(HomeService);
  private readonly _sharedService = inject(SharedDataService);

  cart = computed(() => this._sharedService.cart());

  haveDiscount: number = 0;
  randomProducts: Product[] = [];

  constructor() {
    this._homeService.getRandomProducts(3).subscribe((res) => {
      this.randomProducts = res as Product[];
    });
  }

  modifyQuantity(productId: number, quantity: number) {}

  removeFromCart(productId: number) {
    this._sharedService.deleteProductFromCart(productId);
  }

  applyDiscount() {
    this.haveDiscount = this.cart()!.totalPrice * 0.3;
  }
}
