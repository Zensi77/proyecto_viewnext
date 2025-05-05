import {
  Component,
  inject,
  Input,
  OnChanges,
  OnInit,
  SimpleChange,
  SimpleChanges,
} from '@angular/core';
import {
  SearchProductResponse,
  SearchProduct,
  Category,
  Provider,
} from '../../../interfaces/Data.interface';
import { HomeService } from '../../../services/home.service';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { ButtonModule } from 'primeng/button';
import { DividerModule } from 'primeng/divider';
import { Slider, SliderChangeEvent } from 'primeng/slider';
import { ProgressSpinner } from 'primeng/progressspinner';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { ProductCardComponent } from '../../../ui/product-card/product-card.component';
import { CommonModule } from '@angular/common';
import { Select, SelectChangeEvent } from 'primeng/select';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-search-result',
  imports: [
    PaginatorModule,
    ButtonModule,
    DividerModule,
    Slider,
    ProgressSpinner,
    FormsModule,
    ProductCardComponent,
    CommonModule,
    Select,
  ],
  templateUrl: './search-result.component.html',
  styles: ``,
})
export class SearchResultComponent implements OnInit, OnChanges {
  private readonly _homeService = inject(HomeService);
  private readonly _route = inject(ActivatedRoute);
  private sliderSubject = new Subject<number>(); // Subject para manejar los cambios del slider
  private rangePricesSubject = new Subject<number[]>();

  loading: boolean = false;
  productsSearch: SearchProductResponse | null = null;
  orderBy = [
    { id: 1, name: 'Menores precio primero' },
    { id: 2, name: 'Mayores precio primero' },
    { id: 3, name: 'A - Z' },
    { id: 4, name: 'Z - A' },
  ];

  @Input({ required: true }) rangePrices: number[] = [0, 3000];
  @Input({ required: true }) selectedCategory: Category | null = null;
  @Input({ required: true }) selectedProvider: Provider[] | null = [];
  @Input() query: string = '';

  searchParams: SearchProduct = {
    page: 0,
    size: 12,
    sortBy: 'id',
    orderBy: 'asc',
    filterProvider: [],
    filterCategory: [],
    filterPriceMin: 0,
    filterPriceMax: 3000,
    filterName: this.query,
  };

  ngOnInit(): void {
    this._route.queryParams.subscribe((params) => {
      const category = params['category'];
      const provider = params['provider'];
      const query = params['query'];

      this.setSearchParams(category, provider, query);
      this.getProducts();
    });

    // Configurar debounce para el slider
    this.sliderSubject.pipe(debounceTime(700)).subscribe((size) => {
      this.searchParams.page = 0;
      this.searchParams.size = size;
      this.getProducts();
    });

    // Configurar debounce para el rango de precios
    this.rangePricesSubject.pipe(debounceTime(500)).subscribe((rangePrices) => {
      this.searchParams.filterPriceMin = rangePrices[0];
      this.searchParams.filterPriceMax = rangePrices[1];
      this.getProducts();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedCategoryChange: SimpleChange = changes['selectedCategory'];
    const selectedProviderChange: SimpleChange = changes['selectedProvider'];
    const rangePricesChange: SimpleChange = changes['rangePrices'];
    const queryChange: SimpleChange = changes['query'];

    if (selectedCategoryChange && !selectedCategoryChange.firstChange) {
      this.searchParams.filterCategory = []; // Limpiar el filtro de categoría
      if (this.selectedCategory) {
        this.searchParams.filterCategory.push(this.selectedCategory.id);
      }
      this.getProducts();
    }

    if (selectedProviderChange && !selectedProviderChange.firstChange) {
      this.searchParams.filterProvider = []; // Limpiar el filtro de proveedor
      if (this.selectedProvider) {
        this.selectedProvider.forEach((provider) => {
          this.searchParams.filterProvider.push(provider.id);
        });
      }
      this.getProducts();
    }

    if (rangePricesChange) {
      this.searchParams.filterPriceMin = 0;
      this.searchParams.filterPriceMax = 3000;
      this.rangePricesSubject.next(this.rangePrices);
    }

    if (queryChange) {
      this.searchParams.filterName = this.query;
      this.getProducts();
    }
  }

  setSearchParams(category: string, provider: string, query: string) {
    this.searchParams.page = 0;
    this.searchParams.filterCategory = []; // Limpiar el filtro de categoría
    this.searchParams.filterProvider = []; // Limpiar el filtro de proveedor
    this.searchParams.filterName = ''; // Limpiar el filtro de nombre

    if (category) {
      const categoryId = this._homeService
        .categories()
        .filter(
          (cat) => cat.name.toLowerCase() === category.toLowerCase()
        )[0]?.id;
      this.searchParams.filterCategory.push(categoryId);
    }

    if (provider) {
      const providerId = this._homeService
        .providers()
        .filter((prov) => prov.name === provider)[0]?.id;
      this.searchParams.filterProvider.push(providerId);
    }

    if (query) {
      this.searchParams.filterName = query;
    }
  }

  getProducts() {
    this.loading = true;
    this._homeService.searchProducts(this.searchParams).subscribe({
      next: (res) => {
        console.log('Productos obtenidos:', res);

        this.productsSearch = res;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching products', err);
        this.loading = false;
      },
    });
  }

  onPageChange(event: PaginatorState) {
    this.searchParams.page = event.page ?? 0;
    this.searchParams.size = event.rows ?? 10;
    this.getProducts();
  }

  onSizePageChange(event: SliderChangeEvent) {
    // Emitir el valor del slider al Subject
    this.sliderSubject.next(event.value as number);
  }

  onOrderByChange($event: SelectChangeEvent) {
    switch ($event.value.id) {
      case 1:
        this.searchParams.sortBy = 'price';
        this.searchParams.orderBy = 'asc';
        break;
      case 2:
        this.searchParams.sortBy = 'price';
        this.searchParams.orderBy = 'desc';
        break;
      case 3:
        this.searchParams.sortBy = 'name';
        this.searchParams.orderBy = 'asc';
        break;
      case 4:
        this.searchParams.sortBy = 'name';
        this.searchParams.orderBy = 'desc';
        break;
    }
    this.getProducts();
  }
}
