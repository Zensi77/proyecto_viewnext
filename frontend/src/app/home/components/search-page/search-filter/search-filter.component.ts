import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  OnInit,
  Output,
  ViewChild,
  computed,
  inject,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ListboxModule } from 'primeng/listbox';
import { Slider } from 'primeng/slider';
import { Select } from 'primeng/select';

import { ActivatedRoute } from '@angular/router';
import { Chip } from 'primeng/chip';
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

  categories = computed(() => this._homeService.categories());
  providers = computed(() => this._homeService.providers());

  isMenuOpen = false;

  ngOnInit(): void {
    this._route.queryParams.subscribe((params) => {
      const category = params['category'] || null;
      const provider = params['provider'] || null;

      this.selectedCategoryValue =
        this.categories().find((cat) => cat.name === category) || null;

      this.selectedProviderValue =
        this.providers().filter((prov) => provider?.includes(prov.name)) ||
        null;

      this.selectedCategory.emit(this.selectedCategoryValue);
      this.selectedProvider.emit(this.selectedProviderValue);
      this.rangePrices.emit(this.rangePricesValue);
    });
  }

  @ViewChild('filter') miElemento!: ElementRef;

  @HostListener('document:click', ['$event'])
  clickFuera(event: MouseEvent) {
    if (
      this.miElemento &&
      !this.miElemento.nativeElement.contains(event.target)
    ) {
      this.isMenuOpen = false;
    }
  }

  clearFilters() {
    this.selectedCategoryValue = null;
    this.selectedProviderValue = null;

    this.rangePricesValue = [0, 3000];
    this.selectedCategory.emit(this.selectedCategoryValue);
    this.selectedProvider.emit(this.selectedProviderValue);
    this.rangePrices.emit(this.rangePricesValue);
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }
}
