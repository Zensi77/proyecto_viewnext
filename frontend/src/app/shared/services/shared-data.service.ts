import { Injectable, signal } from '@angular/core';
import { ProductName } from '../../home/interfaces/ProductName.interface';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  productNames = signal<ProductName[]>([]);

  setProductNames(names: ProductName[]) {
    this.productNames.set(names);
  }
}
