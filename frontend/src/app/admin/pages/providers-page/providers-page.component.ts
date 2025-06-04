import { Component, computed, inject } from '@angular/core';
import { MessageService } from 'primeng/api';
import Swal from 'sweetalert2';
import { Provider } from '../../../home/interfaces/Data.interface';
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

  constructor(private messageService: MessageService) {
    this._homeService.getProviders();
  }

  providers = computed(() => this._homeService.providers());
  providersFiltered: Provider[] = this.providers();

  name = '';
  query = '';

  searchProviders() {
    this.providersFiltered = this.providers().filter((provider) =>
      provider.name.toLowerCase().includes(this.query.toLowerCase())
    );
  }

  async onAddProvider() {
    Swal.fire({
      title: 'Agregar proveedor',
      html: `
        <input id="name" class="swal2-input" placeholder="Nombre del proveedor">
        <input id="address" class="swal2-input" placeholder="Direccion del proveedor">
      `,
      focusConfirm: false,
      preConfirm: () => {
        const name = (document.getElementById('name') as HTMLInputElement)
          .value;
        const address = (document.getElementById('address') as HTMLInputElement)
          .value;

        return { name, address };
      },
    }).then((result) => {
      if (result.isConfirmed) {
        const { name, address } = result.value;
        if (name === '' || address === '') return;

        this._adminService
          .createProvider({ name, address } as Provider)
          .subscribe({
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: 'Proveedor creado',
              });
              this._homeService.getProviders();
            },
          });
      }
    });
  }

  updateProvider(provider: Provider) {
    this._adminService.updateProvider(provider.id, provider).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Proveedor actualizado',
        });
      },
      error: () => {
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
          error: () => {
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
