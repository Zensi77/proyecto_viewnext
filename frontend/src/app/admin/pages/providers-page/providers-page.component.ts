import { Component, computed, inject } from '@angular/core';
import { MessageService } from 'primeng/api';
import Swal from 'sweetalert2';
import { Category, Provider } from '../../../home/interfaces/Data.interface';
import { HomeService } from '../../../home/services/home.service';
import { AdminService } from '../../services/admin.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { Toast } from 'primeng/toast';

@Component({
  selector: 'app-providers-page',
  imports: [TableModule, CommonModule, FormsModule, ButtonModule, Toast],
  providers: [MessageService],

  templateUrl: './providers-page.component.html',
  styles: ``,
})
export class ProvidersPageComponent {
  private readonly _homeService = inject(HomeService);
  private readonly _adminService = inject(AdminService);

  constructor(private messageService: MessageService) {}

  providers = computed(() => this._homeService.providers());
  providersFiltered: Provider[] = this.providers();

  name = '';
  query = '';

  searchProviders(event: Event) {
    this.providersFiltered = this.providers().filter((provider) =>
      provider.name.toLowerCase().includes(this.query.toLowerCase())
    );
  }

  updateProduct(provider: Provider) {
    this._adminService.updateProvider(provider.id, provider).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Proveedor actualizado',
        });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo actualizar el proveedor',
        });
      },
    });
  }

  onDeleteProvider(providerId: string) {
    Swal.fire({
      title: '¿Estás seguro?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Sí, borrar proveedor',
    }).then((result) => {
      if (result.isConfirmed) {
        this._adminService.deleteProvider(providerId).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Proveedor eliminado',
            });
          },
          error: (err) => {
            console.log('error', err);

            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: 'No se pudo eliminar el proveedor',
            });
          },
        });
      }
    });
  }
}
