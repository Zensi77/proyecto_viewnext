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
      class="transition-all max-h-10 duration-400 ease-in-out shadow-3xl w-full flex gap-1 md:gap-4 items-center justify-center font-bold hover:scale-102 hover:shadow-4xl cursor-pointer"
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
  @Input() color: string = 'bg-green-600';
  @Input() textColor: string = 'text-white';
  @Input() hoverColor: string = 'hover:bg-green-800';
  @Input() hoverTextColor: string = 'hover:text-white';
  @Input() borderColor: string = 'border-green-800';
  @Input() borderRadius: string = 'rounded-full';
  @Input() padding: string = 'px-6 py-4';
  @Input() margin: string = 'm-2';

  selectIcon(): string {
    const categoryNormalized = this.category.trim().toLowerCase();

    const iconMap: Record<string, string> = {
      // Informática y Electrónica
      ordenador: 'pi pi-desktop',
      computadora: 'pi pi-desktop',
      pc: 'pi pi-desktop',
      portátil: 'pi pi-desktop',
      laptop: 'pi pi-laptop',
      monitor: 'pi pi-desktop',
      televisor: 'pi pi-desktop',
      televisión: 'pi pi-desktop',
      tv: 'pi pi-tv',
      móvil: 'pi pi-mobile',
      movil: 'pi pi-mobile',
      teléfono: 'pi pi-mobile',
      celular: 'pi pi-mobile',
      tablet: 'pi pi-tablet',
      tableta: 'pi pi-tablet',
      smartwatch: 'pi pi-clock',
      'reloj inteligente': 'pi pi-clock',
      reloj: 'pi pi-clock',
      cámara: 'pi pi-camera',
      camara: 'pi pi-camera',
      impresora: 'pi pi-print',
      escáner: 'pi pi-print',
      escaner: 'pi pi-print',
      microprocesador: 'pi pi-microchip',
      procesador: 'pi pi-microchip',
      cpu: 'pi pi-microchip',
      gpu: 'pi pi-microchip',
      'tarjeta gráfica': 'pi pi-microchip',
      'tarjeta grafica': 'pi pi-microchip',
      'disco duro': 'pi pi-hdd',
      ssd: 'pi pi-hdd',
      'memoria ram': 'pi pi-hdd',
      ratón: 'pi pi-mouse',
      raton: 'pi pi-mouse',
      mouse: 'pi pi-mouse',
      teclado: 'pi pi-keyboard',
      router: 'pi pi-wifi',
      módem: 'pi pi-wifi',
      modem: 'pi pi-wifi',
      altavoces: 'pi pi-volume-up',
      parlantes: 'pi pi-volume-up',
      auriculares: 'pi pi-headphones',
      audífonos: 'pi pi-headphones',
      cascos: 'pi pi-headphones',
      micrófono: 'pi pi-microphone',
      microfono: 'pi pi-microphone',
      dron: 'pi pi-compass',
      drone: 'pi pi-compass',

      // Electrodomésticos
      refrigerador: 'pi pi-sliders-h',
      nevera: 'pi pi-sliders-h',
      frigorífico: 'pi pi-sliders-h',
      frigorifico: 'pi pi-sliders-h',
      lavadora: 'pi pi-cog',
      secadora: 'pi pi-cog',
      lavavajillas: 'pi pi-cog',
      horno: 'pi pi-fire',
      microondas: 'pi pi-fire',
      cafetera: 'pi pi-coffee',
      tostadora: 'pi pi-fire',
      licuadora: 'pi pi-cog',
      batidora: 'pi pi-cog',
      aspiradora: 'pi pi-trash',
      plancha: 'pi pi-cog',
      ventilador: 'pi pi-refresh',
      'aire acondicionado': 'pi pi-refresh',
      calefactor: 'pi pi-fire',

      // Entretenimiento y Juegos
      consola: 'pi pi-discord',
      videojuego: 'pi pi-gamepad',
      juegos: 'pi pi-gamepad',
      joystick: 'pi pi-gamepad',
      mando: 'pi pi-gamepad',
      control: 'pi pi-gamepad',
      dvd: 'pi pi-video',
      'blu-ray': 'pi pi-video',
      proyector: 'pi pi-video',
      'cine en casa': 'pi pi-video',
      karaoke: 'pi pi-microphone',
    };

    return iconMap[categoryNormalized] ?? 'pi pi-question-circle';
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
