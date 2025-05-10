import { Component, computed, inject, OnInit } from '@angular/core';
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
  ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './orders-page.component.html',
  styles: ``,
})
export class OrdersPageComponent implements OnInit {
  private readonly _homeService = inject(HomeService);

  orders = computed(() => this._homeService.orders());

  constructor(
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this._homeService.getOrders();
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
