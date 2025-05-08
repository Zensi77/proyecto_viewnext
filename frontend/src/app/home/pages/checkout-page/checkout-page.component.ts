import { Component, computed, inject } from '@angular/core';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Dialog } from 'primeng/dialog';
import { HomeService } from '../../services/home.service';
import { Product } from '../../interfaces/Data.interface';
import { ProductCardComponent } from '../../ui/product-card/product-card.component';
import { ProductCart } from '../../../shared/interfaces/data-shared.interface';
import { PaymentFormComponent } from '../../components/cart-page/payment-form/payment-form.component';
@Component({
  imports: [
    RouterLink,
    CommonModule,
    ProductCardComponent,
    PaymentFormComponent,
    Dialog,
  ],
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

  modifyQuantity(productItemId: ProductCart, quantity: number) {
    this._sharedService.updateProductQuantity(productItemId, quantity);
  }

  removeFromCart(productId: number) {
    this._sharedService.deleteProductFromCart(productId);
  }

  applyDiscount() {
    this.haveDiscount = this.cart()!.totalPrice * 0.3;
  }

  goToPayment = false;
}
