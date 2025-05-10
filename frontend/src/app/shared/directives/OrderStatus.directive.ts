import {
  Directive,
  ElementRef,
  Input,
  OnChanges,
  Renderer2,
} from '@angular/core';

@Directive({
  selector: '[appOrderStatus]',
  standalone: true,
})
export class OrderStatusDirective implements OnChanges {
  @Input('appOrderStatus') status: string = '';

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngOnChanges(): void {
    this.resetClasses();

    this.renderer.addClass(this.el.nativeElement, 'pi');
    this.renderer.addClass(this.el.nativeElement, 'text-white');

    switch (this.status) {
      case 'PENDING':
        this.renderer.addClass(this.el.nativeElement, 'pi-clock');
        this.renderer.addClass(this.el.nativeElement, 'bg-yellow-500');
        break;
      case 'COMPLETED':
        this.renderer.addClass(this.el.nativeElement, 'pi-check');
        this.renderer.addClass(this.el.nativeElement, 'bg-green-500');
        break;
      case 'CANCELLED':
        this.renderer.addClass(this.el.nativeElement, 'pi-times');
        this.renderer.addClass(this.el.nativeElement, 'bg-red-500');
        break;
      default:
        this.renderer.addClass(this.el.nativeElement, 'pi-question');
        this.renderer.addClass(this.el.nativeElement, 'bg-gray-500');
        break;
    }
  }

  private resetClasses() {
    const iconClasses = ['pi-clock', 'pi-check', 'pi-times', 'pi-question'];
    const bgClasses = [
      'bg-yellow-500',
      'bg-green-500',
      'bg-red-500',
      'bg-gray-500',
    ];

    [...iconClasses, ...bgClasses].forEach((cls) => {
      this.renderer.removeClass(this.el.nativeElement, cls);
    });
  }
}
