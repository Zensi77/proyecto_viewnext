import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  OnInit,
} from '@angular/core';
import { HomeService } from '../../services/home.service';

@Component({
  selector: 'app-wish-list-page',
  imports: [],
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
  wishList = computed(() => this._homeService.whisList());

  ngOnInit(): void {
    this._homeService.getWishList();
    console.log('Wish List:', this.wishList());
  }
}
