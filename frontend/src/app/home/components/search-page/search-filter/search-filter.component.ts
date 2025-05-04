import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  OnInit,
  Output,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ListboxModule } from 'primeng/listbox';
import { Slider } from 'primeng/slider';
import { Select } from 'primeng/select';

import { ActivatedRoute } from '@angular/router';
import { Chip } from 'primeng/chip';
import { combineLatest, tap } from 'rxjs';
import { CommonModule } from '@angular/common';
import { Category, Provider } from '../../../interfaces/Data.interface';
import { HomeService } from '../../../services/home.service';

@Component({
  selector: 'search-filter',
  imports: [ListboxModule, Slider, FormsModule, Select, Chip, CommonModule],
  templateUrl: './search-filter.component.html',
  styles: ``,
})
export class SearchFilterComponent implements OnInit {
  private readonly _homeService = inject(HomeService);
  private readonly _route = inject(ActivatedRoute);

  @Output() selectedCategory = new EventEmitter<Category | null>();
  @Output() selectedProvider = new EventEmitter<Provider[] | null>();
  @Output() rangePrices = new EventEmitter<number[]>();

  selectedCategoryValue: Category | null = null;
  selectedProviderValue: Provider[] | null = null;
  rangePricesValue: number[] = [0, 3000];

  categories: Category[] = [];
  providers: Provider[] = [];

  ngOnInit() {
    this._homeService
      .getCategoryes()
      .pipe(
        tap((categories) =>
          categories.forEach((category) => {
            category.name =
              category.name.charAt(0).toUpperCase() + category.name.slice(1);
          })
        )
      )
      .subscribe((categories) => {
        this.categories = categories;
        console.log('Categorías transformadas:', this.categories);
      });

    this._homeService
      .getProviders()
      .pipe(
        tap((providers) => {
          providers.forEach((provider) => {
            provider.name =
              provider.name.charAt(0).toUpperCase() + provider.name.slice(1);
          });
        })
      )
      .subscribe((providers) => {
        this.providers = providers;
      });

    const categories$ = this._homeService.getCategoryes();
    const queryParams$ = this._route.queryParams;

    combineLatest([categories$, queryParams$]).subscribe(
      ([categories, params]) => {
        this.categories = categories;

        if (params['category']) {
          const category = params['category'].toLowerCase();

          const categoryFound = this.categories.filter(
            (c) => c.name.toLowerCase() === category
          );

          if (categoryFound.length > 0) {
            this.selectedCategoryValue = categoryFound[0];
          } else {
            this.selectedCategoryValue = null;
          }
        }
      }
    );
  }

  clearFilters() {
    this.selectedCategoryValue = null;
    this.selectedProviderValue = null;
    this.rangePricesValue = [0, 3000];
    this.selectedCategory.emit(this.selectedCategoryValue);
    this.selectedProvider.emit(this.selectedProviderValue);
    this.rangePrices.emit(this.rangePricesValue);
  }
}
