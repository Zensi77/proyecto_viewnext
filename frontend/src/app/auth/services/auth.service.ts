import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';

import { Subscription, User } from '../interfaces/user.interface';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private _http = inject(HttpClient);
  private _router = inject(Router);

  loading = signal<boolean>(false);

  private userProfileSubject = new BehaviorSubject<User | null>(null);
  userProfile$ = this.userProfileSubject.asObservable();

  // Registrar un usuario con email y contraseña
  signUp(user: { email: string; password: string }) {}

  // Iniciar sesión con email y contraseña
  signIn(user: { email: string; password: string }) {}

  // Cerrar sesión
  signOut() {
    sessionStorage.removeItem('token');
    this._router.navigate(['/']);
  }

  // Iniciar sesión con Google
  submitWithGoogle() {}

  private validateUser() {}

  get isAdmin() {
    const user = this.userProfileSubject.value;
    return user?.role === 'admin';
  }
}
