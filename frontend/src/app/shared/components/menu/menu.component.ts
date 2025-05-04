import { Component, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MenuItem } from 'primeng/api';
import { Menubar } from 'primeng/menubar';
import { BadgeModule } from 'primeng/badge';
import { AvatarModule } from 'primeng/avatar';
import { InputTextModule } from 'primeng/inputtext';
import { Ripple } from 'primeng/ripple';
import { Router, RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';

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
  ],
  templateUrl: './menu.component.html',
  styles: ``,
})
export class MenuComponent implements OnInit {
  private readonly _authService = inject(AuthService);
  private readonly _router = inject(Router);
  private _user = computed(() => this._authService.user());

  items: MenuItem[] | undefined;

  ngOnInit() {
    this.items = [
      {
        label: 'Home',
        icon: 'pi pi-home',
        routerLink: '/',
      },
      {
        label: 'Suscripcion',
        icon: 'pi pi-money-bill',
      },
      {
        label: 'Stats',
        icon: 'pi pi-chart-bar',
        replaceUrl: true,
        routerLink: '/stats',
      },
      {
        label: 'Admin Panel',
        icon: 'pi pi-cog',
        routerLink: '/admin',
      },
    ];

    const darkMode = localStorage.getItem('dark-mode');
    if (darkMode) this.toogleDarkMode();
  }

  toogleDarkMode() {
    const element = document.querySelector('html');
    element?.classList.toggle('dark-mode');

    if (element?.classList.contains('dark-mode')) {
      localStorage.setItem('dark-mode', 'true');
    } else {
      localStorage.removeItem('dark-mode');
    }

    document.querySelector('#darkMode')?.classList.remove('pi-moon');
    document.querySelector('#darkMode')?.classList.add('pi-sun');
  }

  isDarkMode = () =>
    document.querySelector('html')?.classList.contains('dark-mode');

  getIconMode() {
    return this.isDarkMode() ? 'pi pi-sun' : 'pi pi-moon';
  }

  isLogged() {
    return this._user() != null ? 'pi pi-sign-out' : 'pi pi-sign-in';
  }

  actionLogged() {
    console.log('actionLogged', this._user());

    if (this._user() != null) {
      this._authService.signOut();
    } else {
      this._router.navigateByUrl('/auth/sign-in');
    }
  }

  actionCart() {
    if (this._user() != null) {
      this._router.navigateByUrl('/cart');
    } else {
      this._router.navigateByUrl('/auth/sign-in');
    }
  }
}
