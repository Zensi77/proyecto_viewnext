import { Component, inject } from '@angular/core';
import {
  ActivatedRoute,
  NavigationEnd,
  Router,
  RouterOutlet,
} from '@angular/router';
import { MenuComponent } from './shared/components/menu/menu.component';
import { AutocompleteSearchComponent } from './shared/components/autocomplete-search/autocomplete-search.component';
import { filter } from 'rxjs';
import { AuthService } from './auth/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
    MenuComponent,
    AutocompleteSearchComponent,
    CommonModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'Tecno Shop';

  readonly authService = inject(AuthService);

  showSearchBar = false;
  routesWithSearchBar = ['/', '/search', '/product'];

  constructor(private router: Router, private route: ActivatedRoute) {
    // Muestra el componente de búsqueda en las rutas especificadas
    this.router.events
      .pipe(filter((e) => e instanceof NavigationEnd))
      .subscribe(() => {
        const path = this.router.url.split('?')[0];
        this.showSearchBar = this.routesWithSearchBar.some(
          (route) =>
            route === path ||
            (route === '/product' && path.startsWith('/product/'))
        );
      });
  }
}
