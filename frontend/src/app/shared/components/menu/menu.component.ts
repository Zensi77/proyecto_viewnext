import { Component, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MenuItem } from 'primeng/api';
import { Menubar } from 'primeng/menubar';
import { BadgeModule } from 'primeng/badge';
import { OverlayBadgeModule } from 'primeng/overlaybadge';
import { AvatarModule } from 'primeng/avatar';
import { InputTextModule } from 'primeng/inputtext';
import { Ripple } from 'primeng/ripple';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { CartComponent } from '../cart/cart.component';
import { SharedDataService } from '../../services/shared-data.service';

@Component({
  selector: 'app-menu',
  imports: [
    RouterModule,
    RouterLink,
    Menubar,
    BadgeModule,
    AvatarModule,
    InputTextModule,
    Ripple,
    CommonModule,
    CartComponent,
    OverlayBadgeModule,
  ],
  templateUrl: './menu.component.html',
  styles: ``,
})
export class MenuComponent implements OnInit {
  private readonly _authService = inject(AuthService);
  private readonly _sharedService = inject(SharedDataService);
  private readonly _router = inject(Router);

  private _user = computed(() => this._authService.user());
  cart = computed(() => this._sharedService.cart());

  items: MenuItem[] | undefined;
  showCart = false;
  animationCart = false;

  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-home',
        routerLink: '/',
      },
      {
        label: 'Mis pedidos',
        icon: 'pi pi-shopping-cart',
        routerLink: '/orders',
      },
      {
        label: 'Lista de deseos',
        icon: 'pi pi-heart',
        replaceUrl: true,
        routerLink: '/wishlist',
      },
    ];
  }

  isLogged() {
    return this._user() != null ? 'pi pi-sign-out' : 'pi pi-sign-in';
  }

  actionLogged() {
    if (this._user() != null) {
      this._authService.signOut();
      this._sharedService.cart.set(null);
      this._router.navigateByUrl('/');
    } else {
      this._router.navigateByUrl('/auth/sign-in');
    }
  }

  actionCart() {
    if (this._user() != null) {
      this.showCart = true;
    } else {
      this._router.navigateByUrl('/auth/sign-in');
    }
  }
  showCartChange() {
    this.showCart = !this.showCart;
  }
}
