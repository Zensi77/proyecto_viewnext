import { Component, inject } from '@angular/core';
import { NgxTypewriterComponent } from '@omnedia/ngx-typewriter';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { ProductName } from '../../interfaces/ProductName.interface';
import { CategoriesLayerComponent } from '../../components/home-page/categories-layer/categories-layer.component';
import { ProductsLayerComponent } from '../../components/home-page/products-layer/products-layer.component';

@Component({
  imports: [
    NgxTypewriterComponent,
    FormsModule,
    CategoriesLayerComponent,
    ProductsLayerComponent,
  ],
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss'],
})
export default class HomePageComponent {
  private readonly _route = inject(ActivatedRoute);
  private readonly _sharedService = inject(SharedDataService);

  constructor() {
    const data: ProductName[] = this._route.snapshot.data['names'];

    this._sharedService.setProductNames(data);
  }

  hora = new Date().getHours();
  words = [
    `${
      this.hora < 12
        ? 'Buenos días'
        : this.hora < 18
        ? 'Buenas tardes'
        : 'Buenas noches'
    }, `,
    'Bienvenido a TecnoShop',
    'Encuentra lo que buscas',
  ];
}
