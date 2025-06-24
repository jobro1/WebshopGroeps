import { Injectable, inject } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { LoginService } from '../services/login.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  private loginService = inject(LoginService);
  private router = inject(Router);

  canActivate(): boolean | UrlTree {
    if (this.loginService.isLoggedIn() && this.loginService.isAdmin()) {
      return true;
    }
    return this.router.parseUrl('/');
  }
}
