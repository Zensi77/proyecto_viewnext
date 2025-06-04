import { Component, computed, inject, OnInit } from '@angular/core';
import { HomeService } from '../../../home/services/home.service';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import Swal from 'sweetalert2';
import { MessageService } from 'primeng/api';
import { AdminService } from '../../services/admin.service';
import { Category } from '../../../home/interfaces/Data.interface';
import { Toast } from 'primeng/toast';

@Component({
  standalone: true,
  imports: [TableModule, CommonModule, FormsModule, ButtonModule, Toast],
  providers: [MessageService],
  templateUrl: './categories-page.component.html',
  styles: ``,
})
export class CategoriesPageComponent {
  private readonly _homeService = inject(HomeService);
  private readonly _adminService = inject(AdminService);

  categories = computed(() => this._homeService.categories());
  categoriesFiltered: Category[] = this.categories();

  name = '';
  query = '';

  constructor(private messageService: MessageService) {}

  searchCategories() {
    this.categoriesFiltered = this.categories().filter((category) =>
      category.name.toLowerCase().includes(this.query.toLowerCase())
    );
  }

  async onAddCategory() {
    const { value: name } = await Swal.fire({
      input: 'text',
      inputLabel: 'Nombre de la categoría',
      inputPlaceholder: 'Ingrese el nombre de la categoría',
    });
    if (name === '') return;

    this._adminService.createCategory(name).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Categoria creada',
        });
        this._homeService.getCategories();
      },
    });
  }

  updateCategory(category: Category) {
    this._adminService.updateCategory(category.id, category).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Categoria actualizada',
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo actualizar la categoria',
        });
      },
    });
  }

  onDeleteCategory(categoryId: string) {
    Swal.fire({
      title: '¿Estás seguro?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, borrar categoria',
    }).then((result) => {
      if (result.isConfirmed) {
        this._adminService.deleteCategory(categoryId).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Categoria eliminada',
            });
          },
          error: () => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'No se pudo eliminar la categoria',
            });
          },
        });
      }
    });
  }
}
