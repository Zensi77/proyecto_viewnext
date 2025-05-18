import { inject, Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { debounceTime, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ValidatorService {
  private readonly _http = inject(HttpClient);

  validateEmail(): AsyncValidatorFn {
    return (control: AbstractControl) => {
      const url = `${environment.base_url}${environment.check_email}`;
      const email = control.value;

      return this._http
        .get<boolean>(url, {
          // Paso el email como query param al ser get si fuera post sería en el body como un objeto {email}
          params: { email },
        })
        .pipe(
          debounceTime(300),
          map((res) => (res ? { emailTaken: true } : null)) // Falso si el email ya está en uso
        );
    };
  }
}
