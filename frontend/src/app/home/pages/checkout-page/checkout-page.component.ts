import { Component, computed, inject } from '@angular/core';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Dialog } from 'primeng/dialog';
import { ProgressSpinner } from 'primeng/progressspinner';
import { HomeService } from '../../services/home.service';
import { Product } from '../../interfaces/Data.interface';
import { ProductCardComponent } from '../../ui/product-card/product-card.component';
import { ProductCart } from '../../../shared/interfaces/data-shared.interface';
import { PaymentFormComponent } from '../../components/cart-page/payment-form/payment-form.component';
import Swal from 'sweetalert2';
@Component({
  imports: [
    RouterLink,
    CommonModule,
    ProductCardComponent,
    PaymentFormComponent,
    Dialog,
    ProgressSpinner,
  ],
  templateUrl: './checkout-page.component.html',
  styleUrl: './checkout-page.component.scss',
})
export default class CheckoutPageComponent {
  private readonly _homeService = inject(HomeService);
  private readonly _sharedService = inject(SharedDataService);

  cart = computed(() => this._sharedService.cart());

  haveDiscount: number = 0;
  randomProducts: Product[] = [];

  constructor() {
    this._homeService.getRandomProducts(3).subscribe((res: Product[]) => {
      this.randomProducts = res;
    });
  }

  animationClass(index: number) {
    index = Math.min(index, 5);
    return `animate__animated animate__fadeIn `;
  }

  modifyQuantity(productItemId: ProductCart, quantity: number) {
    this._sharedService.updateProductQuantity(productItemId, quantity);
  }

  removeFromCart(productId: string) {
    this._sharedService.deleteProductFromCart(productId);
  }

  updateWishList(product: Product) {
    this._homeService.modifyWishList(product.id);
    product.liked = !product.liked;
  }

  applyDiscount() {
    this.haveDiscount = this.cart()!.totalPrice * 0.3;
  }

  goToPayment = false;
  showPayment() {
    this.goToPayment = true;
  }

  loadingPayment = false;
  paymentSuccess() {
    this.loadingPayment = true;
    setTimeout(() => {
      this._sharedService.getCart();
      this.goToPayment = false;
      Swal.fire({
        title: 'Payment Successful',
        text: 'Your order has been placed successfully.',
        icon: 'success',

        showClass: {
          popup: `
          animate__animated
          animate__fadeInUp
          animate__faster
        `,
        },
        hideClass: {
          popup: `
          animate__animated
          animate__fadeOutDown
          animate__faster
        `,
        },
      });
    }, 2000);
  }
}
