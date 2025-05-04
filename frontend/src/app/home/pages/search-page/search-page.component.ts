import { Component, inject } from '@angular/core';
import { Category, Provider } from '../../interfaces/Data.interface';
import { SearchFilterComponent } from '../../components/search-page/search-filter/search-filter.component';
import { SearchResultComponent } from '../../components/search-page/search-result/search-result.component';
import { AutocompleteSearchComponent } from '../../../shared/components/autocomplete-search/autocomplete-search.component';
import { ProductName } from '../../interfaces/ProductName.interface';
import { ActivatedRoute } from '@angular/router';
import { SharedDataService } from '../../../shared/services/shared-data.service';

@Component({
  imports: [
    SearchFilterComponent,
    SearchResultComponent,
    AutocompleteSearchComponent,
  ],
  templateUrl: './search-page.component.html',
  styles: ``,
})
export class SearchPageComponent {
  private readonly _route = inject(ActivatedRoute);
  private readonly _sharedService = inject(SharedDataService);

  rangePrices: number[] = [0, 1500];
  selectedCategory: Category | null = null;
  selectedProvider: Provider[] | null = [];

  constructor() {
    const data: ProductName[] = this._route.snapshot.data['names'];

    this._sharedService.setProductNames(data);
  }

  changeCategory(event: Category | null) {
    this.selectedCategory = event;
  }

  changeProvider(event: Provider[] | null) {
    this.selectedProvider = event;
  }

  changeRangePrices(event: number[]) {
    this.rangePrices = event;
  }
}
