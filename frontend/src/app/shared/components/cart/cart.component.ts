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
import { ProductCart } from '../../interfaces/data-shared.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.component.html',
  styles: ``,
})
export class CartComponent {
  private readonly _sharedService = inject(SharedDataService);

  cart = computed(() => this._sharedService.cart());

  @Input() showCart = false;
  @Output() showCartChange = new EventEmitter<boolean>();

  changeVisibility() {
    this.showCart = !this.showCart;
    this.showCartChange.emit(this.showCart);
  }

  deleteCartItem(cartitem: ProductCart) {
    this._sharedService.deleteProductFromCart(cartitem.product.id);
  }
}
