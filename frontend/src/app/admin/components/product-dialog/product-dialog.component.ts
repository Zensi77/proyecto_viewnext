import {
  Component,
  computed,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  Output,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { HomeService } from '../../../home/services/home.service';
import { DropdownModule } from 'primeng/dropdown';
import { CommonModule } from '@angular/common';
import {
  Product,
  Category,
  Provider,
} from '../../../home/interfaces/Data.interface';
import { ButtonModule } from 'primeng/button';
import { AdminService } from '../../services/admin.service';
import { CreateProduct } from '../../interfaces/admin-data.interface';

@Component({
  selector: 'product-dialog',
  imports: [DropdownModule, CommonModule, ReactiveFormsModule, ButtonModule],
  templateUrl: './product-dialog.component.html',
  styles: ``,
})
export class ProductDialogComponent implements OnChanges {
  private readonly _homeService = inject(HomeService);
  private readonly _adminService = inject(AdminService);
  private readonly _fb = inject(FormBuilder);

  categories = computed(() => this._homeService.categories());
  providers = computed(() => this._homeService.providers());

  @Input() product: Product | null = null;
  @Output() closeDialog = new EventEmitter<void>();

  imagePreviewUrl: string | null = null;

  ngOnChanges(): void {
    if (this.product != null) {
      this.productForm.patchValue({
        name: this.product.name,
        description: this.product.description,
        price: this.product.price,
        category: this.product.category,
        provider: this.product.provider,
        stock: this.product.stock,
        image: this.product.image,
      });
      this.imagePreviewUrl = this.product.image || null;
    }
  }

  productForm = this._fb.group({
    name: ['', Validators.required],
    description: [''],
    price: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(1)]],
    category: [null as Category | null, Validators.required],
    provider: [null as Provider | null, Validators.required],
    image: [''],
  });

  onSaveProduct() {
    if (this.productForm.invalid) return;

    const product: CreateProduct = {
      name: this.productForm.get('name')?.value || '',
      description: this.productForm.get('description')?.value || '',
      price: this.productForm.get('price')?.value || 0,
      stock: this.productForm.get('stock')?.value || 0,
      category: this.productForm.get('category')!.value!.id,
      provider: this.productForm.get('provider')!.value!.id,
      image: this.productForm.get('image')?.value || '',
    };

    this.product &&
      this._adminService
        .updateProduct(this.product ? this.product.id : '', product)
        .subscribe({
          next: () => {
            this.closeDialog.emit();
          },
        });

    this.product === null &&
      this._adminService.createProduct(product).subscribe({
        next: () => {
          this.closeDialog.emit();
        },
      });
  }

  updateImagePreview() {
    const url = this.productForm.get('image')?.value;
    this.imagePreviewUrl = url && url.trim() !== '' ? url : null;
  }
}
