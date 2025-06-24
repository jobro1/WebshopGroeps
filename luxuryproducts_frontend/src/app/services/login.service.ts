import { inject, Injectable } from '@angular/core';
import { Login } from '../models/login';
import { HttpClient } from '@angular/common/http';
import { Token } from '../models/token';
import { tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
  sub: string;
  role?: string;
  roles?: string[];
  exp: number;
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private httpClient = inject(HttpClient);
  private loggedIn = false;
  private token: string | null = null;

  constructor() {
    this.loadTokenFromLocalStorage();
    if (this.token && !this.isTokenExpired()) {
      this.loggedIn = true;
    } else {
      this.logout(); // clear expired tokens
    }
  }

  public login(login: Login) {
    return this.httpClient.post<Token>(
      `${environment.apiUrl}/auth/login`,
      login
    ).pipe(
      tap((res) => {
        if (res.token) {
          this.loggedIn = true;
          this.token = res.token;
          this.saveTokenInLocalStorage(res.token);
          this.saveUserIdToLocalStorage(res.userId);
        }
      })
    );
  }

  public logout() {
    this.loggedIn = false;
    this.token = null;
    this.removeTokenFromLocalStorage();
    this.removeUserIdFromLocalStorage();
  }

  public isLoggedIn(): boolean {
    return this.loggedIn;
  }

  public getAuthToken(): string | null {
    return this.token;
  }

  public getLoggedInUserid(): string {
    return localStorage.getItem('userId') ?? '';
  }

  public isAdmin(): boolean {
    if (!this.token) return false;

    try {
      const decoded = jwtDecode<JwtPayload>(this.token);
      return decoded.role === 'ADMIN' || (decoded.roles?.includes('ADMIN') ?? false);
    } catch (err) {
      console.error('Invalid token:', err);
      return false;
    }
  }

  private isTokenExpired(): boolean {
    if (!this.token) return true;

    try {
      const decoded = jwtDecode<JwtPayload>(this.token);
      const now = Math.floor(Date.now() / 1000);
      return decoded.exp < now;
    } catch {
      return true;
    }
  }

  private saveTokenInLocalStorage(token: string) {
    localStorage.setItem('authToken', token);
  }

  private removeTokenFromLocalStorage() {
    localStorage.removeItem('authToken');
  }

  private saveUserIdToLocalStorage(userId: number) {
    localStorage.setItem('userId', String(userId));
  }

  private removeUserIdFromLocalStorage() {
    localStorage.removeItem('userId');
  }

  private loadTokenFromLocalStorage() {
    this.token = localStorage.getItem('authToken');
  }
}
