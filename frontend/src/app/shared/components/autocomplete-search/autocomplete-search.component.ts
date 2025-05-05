import { FormsModule } from '@angular/forms';
import { Component, computed, inject, Input } from '@angular/core';
import { AutoComplete, AutoCompleteSelectEvent } from 'primeng/autocomplete';
import { Button } from 'primeng/button';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { Router } from '@angular/router';
import { ProductName } from '../../../home/interfaces/ProductName.interface';

interface AutoCompleteCompleteEvent {
  originalEvent: Event;
  query: string;
}

@Component({
  selector: 'autocomplete-search',
  imports: [AutoComplete, Button, FormsModule],
  template: `
    <div class="flex justify-around items-center w-full gap-4 px-16 mt-24">
      <p-autocomplete
        placeholder="Buscar productos en Tecno Shop"
        class="w-full!"
        [(ngModel)]="query"
        [suggestions]="filteredNames"
        (completeMethod)="search($event)"
        (onSelect)="productSelected($event)"
        styleClass="w-full"
        [dropdown]="true"
        autofocus="true"
        [forceSelection]="false"
        [field]="'name'"
        (keydown.enter)="onSearch()"
      ></p-autocomplete>
      <p-button
        label="Buscar"
        icon="pi pi-search"
        (click)="onSearch()"
        [disabled]="query.length < 1"
      ></p-button>
    </div>
  `,
  styles: ``,
})
export class AutocompleteSearchComponent {
  private readonly _sharedService = inject(SharedDataService);
  private readonly _router = inject(Router);

  names = computed(() => this._sharedService.productNames());
  filteredNames: ProductName[] = [];

  @Input() query: string = '';

  search(event: AutoCompleteCompleteEvent) {
    const query = event.query.toLowerCase();
    this.filteredNames = this.names().filter((item: ProductName) =>
      item.name.toLowerCase().includes(query)
    );
  }

  onSearch() {
    this._router.navigate(['search'], {
      queryParams: { query: this.query },
    });
  }

  productSelected(event: AutoCompleteSelectEvent) {
    const selectedProduct = event.value as ProductName;
    this._router.navigate(['product', selectedProduct.id]);
  }
}
