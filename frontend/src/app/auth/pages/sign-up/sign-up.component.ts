import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { isFieldEquals, message } from '../../utils/validators';
import {
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';

import { FloatLabelModule } from 'primeng/floatlabel';
import { PasswordModule } from 'primeng/password';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';
import { User } from '../../interfaces/user.interface';
import { ValidatorService } from '../../services/validator.service';

@Component({
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    PasswordModule,
    FloatLabelModule,
  ],
  templateUrl: './sign-up.component.html',
  styles: `
    ::ng-deep .p-password {
      width: 100% !important;
    }

    ::ng-deep .p-password input {
      width: 100% !important;
      max-width: 100% !important;
    }
  `,
})
export default class SignUpComponent {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly _authService = inject(AuthService);
  private readonly _validator = inject(ValidatorService);

  readonly registerForm = this.fb.group(
    {
      email: [
        '',
        [Validators.required, Validators.email],
        [this._validator.validateEmail()],
      ],
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: isFieldEquals('password', 'confirmPassword') }
  );

  error(field: string) {
    return message(field)(this.registerForm);
  }

  disableCheckPassword() {
    const valid = this.registerForm.get('password')?.valid;
    return !valid;
  }

  onSubmit() {
    if (!this.registerForm.valid) return this.registerForm.markAllAsTouched();

    const email = this.registerForm.get('email')?.value as string;
    const password = this.registerForm.get('password')?.value as string;
    const username = this.registerForm.get('username')?.value as string;
    this._authService.signUp({ email, password, username } as User);
  }
}
