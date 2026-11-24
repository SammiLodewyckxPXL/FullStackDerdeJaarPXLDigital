import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const role = inject(AuthService).role();
  if (!role) return next(request);
  return next(
    request.clone({
      setHeaders: {
        'X-User-Role': role,
        'X-User': role === 'KLANT' ? 'jamie.vermeulen' : role.toLowerCase(),
      },
    }),
  );
};
