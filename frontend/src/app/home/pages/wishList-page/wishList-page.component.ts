import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { HomeService } from '../../services/home.service';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SharedDataService } from '../../../shared/services/shared-data.service';

@Component({
  selector: 'app-wish-list-page',
  imports: [CommonModule, RouterLink],
  templateUrl: './wishList-page.component.html',
  styles: `
    :host {
      display: block;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class WishListPageComponent implements OnInit {
  private readonly _homeService = inject(HomeService);
  private readonly _sharedService = inject(SharedDataService);
  private readonly _router = inject(Router);
  wishList = computed(() => this._homeService.whisList());

  buttonClicked = signal<string | null>(null);

  ngOnInit(): void {
    this._homeService.getWishList();
  }

  updateWishList(productId: string) {
    this._homeService.modifyWishList(productId);
  }

  addToCart(productId: string) {
    this.buttonClicked.set(productId);
    setTimeout(() => {
      this.buttonClicked.set(null);
    }, 1000);

    if (this._sharedService.cart() === null) {
      this._router.navigate(['/auth/sign-in']);
      return;
    }

    this._sharedService.addProductToCart(productId);
  }
}
