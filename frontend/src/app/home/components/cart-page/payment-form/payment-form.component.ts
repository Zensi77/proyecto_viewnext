import { CommonModule } from '@angular/common';
import { Component, inject, Input, signal } from '@angular/core';
import { HomeService } from '../../../services/home.service';
import { SharedDataService } from '../../../../shared/services/shared-data.service';
import { CartResponse } from '../../../../shared/interfaces/data-shared.interface';
import { CreateOrder, PaymentMethod } from '../../../interfaces/Data.interface';

@Component({
  selector: 'payment-form',
  imports: [CommonModule],
  templateUrl: './payment-form.component.html',
  styles: `
  .selected{
    background-color: #f0f0f0;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }
  `,
})
export class PaymentFormComponent {
  private readonly _homeService = inject(HomeService);

  @Input({ required: true }) cart: CartResponse | null = null;

  paymentMethod = signal<string>('credit-card');

  paymentSelect(method: 'paypal' | 'credit-card' | 'google-pay' | 'apple-pay') {
    if (this.paymentMethod() === method) {
      this.paymentMethod.set('credit-card');
      return;
    }

    this.paymentMethod.set(method);
  }

  private switchToPaymentMethod() {
    switch (this.paymentMethod()) {
      case 'credit-card':
        return PaymentMethod.CREDIT_CARD;
      case 'paypal':
        return PaymentMethod.PAYPAL;
      case 'google-pay':
        return PaymentMethod.GOOGLE_PAY;
      case 'apple-pay':
        return PaymentMethod.APPLE_PAY;
      default:
        return PaymentMethod.CREDIT_CARD;
    }
  }

  createOrder() {
    console.log(this.cart);

    const body: CreateOrder = {
      products: this.cart!.products.map((product) => {
        return {
          id: product.product.id,
          quantity: product.quantity,
        };
      }),
      paymentMethod: this.switchToPaymentMethod(),
    };

    this._homeService.createOrder(body);
  }
}
