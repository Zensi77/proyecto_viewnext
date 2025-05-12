import { Component, computed, inject, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { MenuModule } from 'primeng/menu';
import { BadgeModule } from 'primeng/badge';
import { RippleModule } from 'primeng/ripple';
import { AvatarModule } from 'primeng/avatar';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  imports: [MenuModule, BadgeModule, RippleModule, AvatarModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styles: `.bg-cristal {
    background: rgba(255, 255, 255, 0.3);
    -webkit-backdrop-filter: blur(5px);
    backdrop-filter: blur(3px);
    border: 1.5px solid rgba(209, 213, 219, 0.3);
    overflow: hidden;
  }`,
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
            icon: 'pi pi-inbox',
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
          },
        ],
      },
    ];
  }

  get userName() {
    console.log(this.user());

    return this.user()?.name;
  }
}
