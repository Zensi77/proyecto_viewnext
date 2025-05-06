import { CommonModule } from '@angular/common';
import {
  Component,
  computed,
  effect,
  EventEmitter,
  inject,
  Input,
  Output,
} from '@angular/core';
import { SharedDataService } from '../../services/shared-data.service';

@Component({
  selector: 'app-cart',
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styles: ``,
})
export class CartComponent {
  private readonly _sharedService = inject(SharedDataService);

  cart = computed(() => this._sharedService.cart());

  @Input() showCart = false;
  @Output() showCartChange = new EventEmitter<boolean>();

  constructor() {}

  changeVisibility() {
    this.showCart = !this.showCart;
    this.showCartChange.emit(this.showCart);
  }
}
