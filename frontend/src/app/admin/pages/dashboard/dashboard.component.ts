import { Component, computed, inject, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { MenuModule } from 'primeng/menu';
import { BadgeModule } from 'primeng/badge';
import { RippleModule } from 'primeng/ripple';
import { AvatarModule } from 'primeng/avatar';
import { RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  imports: [
    MenuModule,
    BadgeModule,
    RippleModule,
    AvatarModule,
    RouterModule,
    RouterLink,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly _authService = inject(AuthService);

  user = computed(() => this._authService.user());

  items: MenuItem[] | undefined;

  ngOnInit() {
    this.items = [
      {
        separator: true,
      },
      {
        label: 'Productos',
        icon: 'pi pi-fw pi-box',
        routerLink: './products',
      },
      {
        label: 'Categorias',
        icon: 'pi pi-fw pi-tags',
        routerLink: './categories',
      },
      {
        label: 'Proveedores',
        icon: 'pi pi-fw pi-users',
        routerLink: './providers',
      },
      {
        label: 'Usuarios',
        icon: 'pi pi-fw pi-users',
        routerLink: './users',
      },
    ];
  }

  get userName() {
    return this._authService.user()?.username;
  }

  logout() {
    this._authService.signOut();
  }
}
