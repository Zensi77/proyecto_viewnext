import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import Aura from '@primeng/themes/aura';
import { providePrimeNG } from 'primeng/config';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { UserInterceptor } from './auth/interceptors/user.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    providePrimeNG({
      ripple: true,
      inputStyle: 'filled',
      csp: {
        nonce: 'nonce-value',
      },
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.dark-mode',
          prefix: 'p',
          cssLayer: false,
        },
      },
    }),
    provideHttpClient(withInterceptors([UserInterceptor])),
  ],
};
