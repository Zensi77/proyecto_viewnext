import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Input,
} from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';

@Component({
  selector: 'category-button',
  imports: [ButtonModule, RippleModule, CommonModule],
  template: `
    <button
      pRipple
      type="button"
      [ngClass]="buttonClass()"
      class="transition-all duration-400 ease-in-out shadow-3xl w-full flex gap-4 items-center justify-center font-bold hover:scale-102 hover:shadow-4xl cursor-pointer"
      (click)="onClick()"
    >
      <i [ngClass]="selectIcon()"></i> {{ category | titlecase }}
    </button>
  `,
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryButtonComponent {
  private readonly _router = inject(Router);

  @Input({ required: true }) category: string = '';
  @Input() color: string = 'bg-green-500';
  @Input() textColor: string = 'text-white';
  @Input() hoverColor: string = 'hover:bg-green-700';
  @Input() hoverTextColor: string = 'hover:text-white';
  @Input() borderColor: string = 'border-green-500';
  @Input() borderRadius: string = 'rounded-md';
  @Input() padding: string = 'p-6';
  @Input() margin: string = 'm-2';

  selectIcon() {
    switch (this.category) {
      case 'ordenador':
        return 'pi pi-desktop';
      case 'portatil':
        return 'pi pi-laptop';
      case 'televisor':
        return 'pi pi-tv';
      case 'monitor':
        return 'pi pi-desktop';
      case 'movil':
        return 'pi pi-mobile';
      case 'tablet':
        return 'pi pi-tablet';
      case 'raton gamer':
        return 'pi pi-mouse-pointer';
      case 'refrigerador':
        return 'pi pi-box';
      case 'consola':
        return 'pi pi-gamepad';
      case 'router':
        return 'pi pi-wifi';
      case 'auriculares':
        return 'pi pi-headphones';
      case 'camara':
        return 'pi pi-camera';
      case 'impresora':
        return 'pi pi-print';
      case 'smartwatch':
        return 'pi pi-clock';
      case 'dron':
        return 'pi pi-compass';
      case 'microprocesador':
        return 'pi pi-microchip';
      default:
        return 'pi pi-box';
    }
  }

  buttonClass() {
    return `${this.color} ${this.textColor} ${this.hoverColor} ${this.hoverTextColor} ${this.borderColor} ${this.borderRadius} ${this.padding} ${this.margin}`;
  }

  onClick() {
    this._router.navigate(['/search'], {
      queryParams: { category: this.category },
    });
    window.scrollTo(0, 0);
  }
}
