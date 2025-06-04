import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  Output,
  signal,
} from '@angular/core';
import { HomeService } from '../../../services/home.service';
import { CartResponse } from '../../../../shared/interfaces/data-shared.interface';
import { PaymentMethod } from '../../../interfaces/order.interface';

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
  @Output() paymentSuccess = new EventEmitter<void>();

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
    this._homeService
      .createOrder({
        productOrder: this.cart!.products,
        paymentMethod: this.switchToPaymentMethod(),
      })
      .subscribe({
        next: () => {
          this.paymentSuccess.emit();
        },
        error: (err) => {
          console.error(err);
        },
      });
  }
}
