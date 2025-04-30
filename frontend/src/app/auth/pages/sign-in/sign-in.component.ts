import { Component, inject } from '@angular/core';
import {
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { message } from '../../utils/validators';
import { CommonModule } from '@angular/common';
import { InputTextModule } from 'primeng/inputtext';
import Swal from 'sweetalert2';
import { User } from '../../interfaces/user.interface';

@Component({
  imports: [CommonModule, ReactiveFormsModule, RouterLink, InputTextModule],
  templateUrl: './sign-in.component.html',
  styles: ``,
})
export default class SignInComponent {
  private readonly _authService = inject(AuthService);
  private readonly _router = inject(Router);
  private readonly fb = inject(NonNullableFormBuilder);

  readonly loginForm = this.fb.group({
    email: ['prueba@gmail.com', [Validators.required, Validators.email]],
    password: [
      'Jm123456',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/(?=.*[A-Z])(?=.*[a-z])(?=.*\d)/),
      ],
    ],
  });

  error(field: string) {
    return message(field)(this.loginForm);
  }

  submit() {
    if (this.loginForm.invalid) {
      return;
    }

    const { email, password } = this.loginForm.value;

    this._authService.signIn({ email, password } as User);
  }
}
