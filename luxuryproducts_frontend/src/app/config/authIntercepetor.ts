import {inject} from '@angular/core';
import {LoginService} from '../services/login.service';
import {HttpHandlerFn, HttpRequest} from '@angular/common/http';

export function authInterceptor ( req: HttpRequest<unknown>, next: HttpHandlerFn ) {
  const loginService = inject(LoginService);
  const authToken = loginService.getAuthToken();

  if(authToken != null) {
    const newRequest = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${authToken} ` ),
    })
    return next(newRequest);
  }
  return next(req);
}
