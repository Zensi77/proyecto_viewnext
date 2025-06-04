import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScrollingModule } from '@angular/cdk/scrolling';

import { AdminService } from '../../services/admin.service';
import {
  Product,
  SearchProduct,
  SearchProductResponse,
} from '../../../home/interfaces/Data.interface';
import { HomeService } from '../../../home/services/home.service';

import { TableModule } from 'primeng/table';
import { BehaviorSubject, debounceTime } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import Swal from 'sweetalert2';
import { ProductDialogComponent } from '../../components/product-dialog/product-dialog.component';
import { DialogModule } from 'primeng/dialog';
import { FormsModule } from '@angular/forms';

interface Column {
  field: string;
  header: string;
}
@Component({
  imports: [
    DialogModule,
    CommonModule,
    TableModule,
    ScrollingModule,
    CommonModule,
    FormsModule,
    ButtonModule,
    ProductDialogComponent,
  ],
  providers: [MessageService],

  templateUrl: './product-page.component.html',
  styleUrl: './product-page.component.scss',
})
export class ProductPageComponent implements OnInit {
  private readonly _adminService = inject(AdminService);
  private readonly _homeService = inject(HomeService);

  private querySubject = new BehaviorSubject<string>('');
  query$ = this.querySubject.asObservable();

  products: Product[] = [];
  search!: SearchProductResponse;
  cols!: Column[];

  query = '';
  paginateParams: SearchProduct = {
    page: 0,
    size: 10,
    sortBy: 'id',
    orderBy: 'asc',
    filterProvider: [],
    filterCategory: [],
    filterPriceMin: 0,
    filterPriceMax: 3000,
    filterName: this.query,
  };

  constructor(private messageService: MessageService) {}

  ngOnInit(): void {
    this.loadMore();

    this.query$.pipe(debounceTime(700)).subscribe(() => {
      this.paginateParams.filterName = this.query;
      this.paginateParams.page = 0;
      this.products = [];
      this.loadMore();
    });
  }

  onScroll(index: number) {
    const threshold = 10; // cuántos elementos antes del final
    const itemsLoaded = this.products.length;

    if (
      index >= itemsLoaded - threshold &&
      this.search.currentPage < this.search.totalPages - 1
    ) {
      this.loadMore();
    }
  }

  loadMore() {
    this._homeService.searchProducts(this.paginateParams).subscribe({
      next: (res) => {
        this.products = [...this.products, ...res.products];
        this.search = res;
        this.paginateParams.page = this.search.currentPage + 1;
      },
      error: () => {
        console.error('Error al obtener los productos');
      },
    });
  }

  searchProducts() {
    this.querySubject.next(this.query);
  }

  showProductDialog = false;
  productToEdit: Product | null = null;
  onEditProduct(product: Product) {
    this.productToEdit = product;
    this.showProductDialog = true;
  }

  actionCompleted() {
    this.showProductDialog = !this.showProductDialog;

    this.paginateParams.page = 0;
    this.products = [];
    this.loadMore();
  }

  onAddProduct() {
    this.productToEdit = null;
    this.showProductDialog = true;
  }

  onDeleteProduct(productId: string) {
    Swal.fire({
      title: '¿Estás seguro?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, borrar producto',
    }).then((result) => {
      if (result.isConfirmed) {
        this._adminService.deleteProduct(productId).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Producto eliminado',
              life: 3000,
            });
            this.products = this.products.filter(
              (product) => product.id !== productId
            );
            this.loadMore();
          },
          error: (err) => {
            console.error('Error al eliminar el producto', err);
          },
        });
      }
    });
  }
}
