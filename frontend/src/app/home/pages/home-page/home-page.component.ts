import { AfterViewInit, Component, computed, inject } from '@angular/core';
import { AuthService } from '../../../auth/services/auth.service';
import Typewriter from 'typewriter-effect';

@Component({
  imports: [],
  templateUrl: './home-page.component.html',
  styles: ``,
})
export class HomePageComponent implements AfterViewInit {
  ngAfterViewInit() {
    const typewriter = new Typewriter(document.querySelector('#typewritter'), {
      loop: false,
      delay: 75,
    });

    typewriter
      .typeString('¡Bienvenido!') // Texto a teclear
      //.pauseFor(500) // Espera inicial (ms)
      // .pauseFor(1500) // Pausa tras escribir
      //.deleteChars(7) // Borra últimos 7 caracteres
      // .typeString('<strong>Angular + Typewriter</strong>') // Nuevo texto (con HTML)
      // .pauseFor(2000) // Pausa final
      .start();
  }

  private readonly _authService = inject(AuthService);

  _user = computed(() => this._authService.user);
}
