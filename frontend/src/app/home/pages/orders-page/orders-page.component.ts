import {
  Component,
  computed,
  effect,
  inject,
  OnInit,
  signal,
} from '@angular/core';
import { HomeService } from '../../services/home.service';
import { OrderResponse, ProductOrder } from '../../interfaces/order.interface';
import { CommonModule } from '@angular/common';
import { OrderStatusDirective } from '../../../shared/directives/OrderStatus.directive';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { RouterLink } from '@angular/router';
import { paymentMethodPipe } from '../../../shared/pipes/paymentMethod.pipe';
import { FormsModule } from '@angular/forms';
import { DropdownModule } from 'primeng/dropdown';

@Component({
  imports: [
    CommonModule,
    OrderStatusDirective,
    ConfirmPopupModule,
    ToastModule,
    ButtonModule,
    Dialog,
    RouterLink,
    paymentMethodPipe,
    FormsModule,
    DropdownModule,
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './orders-page.component.html',
  styles: ``,
})
export default class OrdersPageComponent implements OnInit {
  private readonly _homeService = inject(HomeService);

  allOrders = computed(() => this._homeService.orders());
  filteredOrders = signal<OrderResponse[]>([]);

  // Opciones para el selector de estado de pedido
  orderStatusOptions = [
    { label: 'Todos los pedidos', value: 'ALL' },
    { label: 'En proceso', value: 'PENDING' },
    { label: 'Cancelado', value: 'CANCELLED' },
  ];

  // Valores seleccionados
  selectedStatus: string = 'ALL';

  constructor(
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {
    effect(() => {
      this.filterOrders();
    });
  }

  ngOnInit(): void {
    this._homeService.getOrders();
  }
  filterOrders(): void {
    const orders = this.allOrders();

    // Si no hay órdenes, salimos
    if (!orders || orders.length === 0) {
      this.filteredOrders.set([]);
      return;
    }

    // Filtramos por estado
    let filtered = [...orders];

    if (this.selectedStatus !== 'ALL') {
      filtered = filtered.filter(
        (order) => order.status === this.selectedStatus
      );
    }

    // Actualizamos las órdenes filtradas
    this.filteredOrders.set(filtered);
  }

  cancelOrder(event: Event, orderId: string) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: '¿Estás seguro de que deseas cancelar este pedido?',
      icon: 'pi pi-info-circle',
      rejectButtonProps: {
        label: 'No',
        icon: 'pi pi-times',
        severity: 'secondary',
        outlined: true,
      },
      acceptButtonProps: {
        label: 'Borrar',
        icon: 'pi pi-check',
        severity: 'danger',
      },
      accept: () => {
        this._homeService.cancel_order(orderId);
        this.messageService.add({
          severity: 'success',
          summary: 'Pedido cancelado',
          life: 3000,
        });
      },
    });
  }

  showOrderDetails = false;
  orderToShow!: ProductOrder[];
  viewOrderDetails(order: ProductOrder[]) {
    this.showOrderDetails = !this.showOrderDetails;
    this.orderToShow = order;
  }
  animationClass(index: number) {
    index = Math.min(index, 5);
    return `animate__animated animate__fadeIn `;
  }
}
