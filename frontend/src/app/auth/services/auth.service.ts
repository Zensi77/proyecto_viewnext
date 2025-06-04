import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';

import { Role, User, UserResponse } from '../interfaces/user.interface';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _http = inject(HttpClient);
  private _router = inject(Router);

  loading = signal<boolean>(false);

  user = signal<User | null>(null);

  constructor() {
    const token = sessionStorage.getItem('token');
    if (token) {
      const user = JSON.parse(atob(token.split('.')[1]));
      this.user.set(user);
    } else {
      this.user.set(null);
    }
  }

  signUp(user: User) {
    const url = environment.base_url + environment.sign_up;
    this.loading.set(true);

    this._http.post<UserResponse>(url, user).subscribe({
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
      error: () => {
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
    const url = environment.base_url + environment.sign_in;

    this.loading.set(true);
    this._http.post<UserResponse>(url, user).subscribe({
      next: (res) => {
        sessionStorage.setItem('token', res.token || '');
        this.user.set(res.user);
        Swal.fire({
          icon: 'success',
          text: `Bienvenido, ${res.user.username || res.user.email}`,
        });
        this.loading.set(false);
        if (res.user.roles[0]?.authority === Role.admin) {
          this._router.navigate(['/admin']);
        } else {
          this._router.navigate(['/']);
        }
      },
      error: (err) => {
        console.error(err);

        err.status === 401 &&
          Swal.fire({
            icon: 'error',
            text: 'Error en las credenciales',
          });
        err.status === 403 &&
          Swal.fire({
            icon: 'warning',
            text: 'Tu usuario ha sido deshabilitado',
          });
        err.status === 429 &&
          Swal.fire({
            icon: 'warning',
            text: 'Usuario bloqueado',
          });

        this.loading.set(false);
      },
    });
  }

  // Cerrar sesión
  signOut() {
    sessionStorage.removeItem('token');
    this.user.set(null);
    Swal.fire({
      icon: 'success',
      text: 'Sesión cerrada',
    });
    this._router.navigate(['/']);
  }

  get isAdmin() {
    const user = this.user();
    if (user) {
      return user.roles[0]?.authority === Role.admin;
    }
    return false;
  }
}
