import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'payment-form',
  imports: [CommonModule],
  templateUrl: './payment-form.component.html',
  styles: ``,
})
export class PaymentFormComponent {
  paymentMethods: string = 'credit-card';

  paymentSelect(
    method: 'paypal' | 'credit-card' | 'google-pay' | 'apple-pay',
    event: Event
  ) {
    this.cleanButtons();

    if (this.paymentMethods === method) {
      this.paymentMethods = 'credit-card';
      return;
    }

    this.paymentMethods = method;

    (event.target as HTMLElement)?.classList.add('border-red-800');
  }

  cleanButtons() {
    const buttons = document.querySelectorAll('button');

    buttons.forEach((button) => {
      button.classList.remove('border-red-800');
    });
  }
}
