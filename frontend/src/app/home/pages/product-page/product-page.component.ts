import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../../interfaces/Data.interface';
import { HomeService } from '../../services/home.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-product-page',
  imports: [CommonModule],
  templateUrl: './product-page.component.html',
  styles: ``,
})
export class ProductPageComponent implements OnInit {
  private readonly _route = inject(ActivatedRoute);
  private readonly _homeService = inject(HomeService);

  product!: Product;
  id!: number;

  ngOnInit(): void {
    this._route.params.subscribe((params) => {
      this.id = params['id'];
    });

    this._homeService.getProduct(this.id).subscribe((res: Product) => {
      this.product = res;
      console.log(res);
    });
  }
}
