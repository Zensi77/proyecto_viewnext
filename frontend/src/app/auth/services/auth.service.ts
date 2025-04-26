import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';

import { Role, User, UserResponse } from '../interfaces/user.interface';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment.development';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _http = inject(HttpClient);
  private _router = inject(Router);

  loading = signal<boolean>(false);

  user = signal<User | null>(null);

  signUp(user: User) {
    const url = environment['sign-up'];
    this.loading.set(true);
    this._http.post<UserResponse>('sign-up', url).subscribe({
      next: (res) => {
        sessionStorage.setItem('token', res.token);
        this.user.set(res.user);
        Swal.fire({
          icon: 'success',
          text: 'Registro exitoso',
        });
        this.loading.set(false);
        this._router.navigate(['/']);
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          text: 'Error en el registro',
        });
        this.loading.set(false);
      },
    });
  }

  // Iniciar sesión con email y contraseña
  signIn(user: User) {
    const url = environment['sign-in'];

    this.loading.set(true);
    this._http.post<UserResponse>(url, user).subscribe({
      next: (res) => {
        sessionStorage.setItem('token', res.token || '');
        this.user.set(res.user);
        Swal.fire({
          icon: 'success',
          text: `Bienvenido, ${res.user.name}`,
        });
        this.loading.set(false);
        this._router.navigate(['/']);
      },
      error: (err) => {
        console.error(err);
        Swal.fire({
          icon: 'error',
          text: 'Error en las credenciales',
        });
        this.loading.set(false);
      },
    });
  }

  // Cerrar sesión
  signOut() {
    sessionStorage.removeItem('token');
    this._router.navigate(['/']);
  }

  get isAdmin() {
    const user = this.user();
    if (user) {
      return user.role === Role.admin;
    }
    return false;
  }
}
