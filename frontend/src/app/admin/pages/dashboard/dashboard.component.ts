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
        items: [
          {
            label: 'New',
            icon: 'pi pi-plus',
          },
          {
            label: 'Search',
            icon: 'pi pi-search',
            routerLink: './products',
          },
        ],
      },
      {
        label: 'Categorias',
        items: [
          {
            label: 'New',
            icon: 'pi pi-plus',
            shortcut: '⌘+O',
          },
          {
            label: 'Search',
            icon: 'pi pi-search',
            routerLink: './categories',
          },
        ],
      },
      {
        label: 'Proveedores',
        items: [
          {
            label: 'New',
            icon: 'pi pi-plus',
            shortcut: '⌘+N',
          },
          {
            label: 'Search',
            icon: 'pi pi-search',
            routerLink: './providers',
          },
        ],
      },
      {
        label: 'Usuarios',
        items: [
          {
            label: 'New',
            icon: 'pi pi-plus',
          },
          {
            label: 'Search',
            icon: 'pi pi-search',
            routerLink: './users',
          },
        ],
      },
    ];
  }

  get userName() {
    return this.user()?.name;
  }

  logout() {
    this._authService.signOut();
  }
}
