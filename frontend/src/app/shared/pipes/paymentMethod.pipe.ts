import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'paymentMethod',
})
export class paymentMethodPipe implements PipeTransform {
  transform(value: any, ...args: any[]): any {
    switch (value) {
      case 'CREDIT_CARD':
        return 'Tarjeta de crédito';
      case 'PAYPAL':
        return 'Paypal';
      case 'GOOGLE_PAY':
        return 'Google Pay';
      case 'APPLE_PAY':
        return 'Apple Pay';
      default:
        return value;
    }
  }
}
