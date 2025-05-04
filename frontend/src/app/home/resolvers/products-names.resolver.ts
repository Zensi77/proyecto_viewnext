import { inject, Injectable } from '@angular/core';
import {
  Resolve,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable } from 'rxjs';
import { HomeService } from '../services/home.service';

@Injectable({
  providedIn: 'root',
})
export class NamesResolver implements Resolve<Observable<string>> {
  private readonly _homeService = inject(HomeService);

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<any> {
    return this._homeService.getProductsNamesObs();
  }
}
