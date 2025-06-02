import { CommonModule } from '@angular/common';
import {
  Component,
  computed,
  EventEmitter,
  inject,
  Input,
  OnChanges,
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
export class CartComponent implements OnChanges {
  private readonly _sharedService = inject(SharedDataService);

  cart = computed(() => this._sharedService.cart());

  @Input() showCart = false;
  @Output() showCartChange = new EventEmitter<boolean>();

  ngOnChanges() {
    if (this.showCart) document.body.style.overflow = 'hidden';
    else document.body.style.overflow = 'auto';

    if (!this.showCart) this.animateExit = false;
    else this.animateExit = true;
  }

  animateExit = false;
  changeVisibility() {
    this.animateExit = !this.animateExit;
    setTimeout(() => {
      this.showCart = !this.showCart;
      this.showCartChange.emit(this.showCart);
    }, 400);
  }

  deleteCartItem(cartitem: ProductCart) {
    this._sharedService.deleteProductFromCart(cartitem.product.id);
  }

  animationClass() {
    return this.animateExit
      ? 'animate__animated animate__fadeInRight animate__faster'
      : 'animate__animated animate__fadeOutRight animate__faster';
  }
}
