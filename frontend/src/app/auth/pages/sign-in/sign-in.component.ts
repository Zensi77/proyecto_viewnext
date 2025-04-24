import { Component, inject } from '@angular/core';
import {
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { GoogleButtonComponent } from '../../ui/google-button/google-button.component';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { message } from '../../utils/validators';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    GoogleButtonComponent,
    RouterLink,
  ],
  templateUrl: './sign-in.component.html',
  styles: ``,
})
export default class SignInComponent {
  private readonly _authService = inject(AuthService);
  private readonly _router = inject(Router);
  private readonly fb = inject(NonNullableFormBuilder);

  readonly loginForm = this.fb.group({
    email: ['juanma@gmail.com', [Validators.required, Validators.email]],
    password: ['123456', [Validators.required, Validators.minLength(6)]],
  });

  error(field: string) {
    return message(field)(this.loginForm);
  }

  async submitWithGoogle() {
    await this._authService.submitWithGoogle();
    this._router.navigateByUrl('/dashboard');
  }

  async submit() {
    if (this.loginForm.invalid) {
      return;
    }

    const email = this.loginForm.get('email')?.value as string;
    const password = this.loginForm.get('password')?.value as string;
    this._authService
      .signIn({ email, password })
      .then(() => {
        Swal.fire({
          icon: 'success',
          title: `Bienvenido! ${
            this._authService.userLogged()?.displayName ||
            this._authService.userLogged()?.email
          }`,
          showConfirmButton: false,
          timer: 1000,
        });
        this._router.navigateByUrl('/dashboard');
      })
      .catch((error) => {
        Swal.fire({
          icon: 'error',
          text: 'Credenciales incorrectas',
        });
      });
  }
}
