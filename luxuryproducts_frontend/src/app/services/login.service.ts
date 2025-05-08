import {inject, Injectable, signal} from '@angular/core';
import {Login} from '../models/login';
import {HttpClient} from '@angular/common/http';
import {Token} from '../models/token';
import {tap} from 'rxjs';
import {environment} from '../../environments/environment';




@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private httpClient=  inject(HttpClient);
  private loggedIn: boolean = false;
  private token: string | null = null;


  public isLoggedIn() {
    return this.loggedIn;
  }
  public getAuthToken() {
    return this.token;
  }

  public logout() {
    this.loggedIn = false;
    this.token = null;
    this.removeTokenFromLocalStorage();
  }

  constructor() {
    this.loadTokenFromLocalStorage();
    if(this.token!=null){
      this.loggedIn=true;
    }
  }

  private loadTokenFromLocalStorage() {
    this.token = localStorage.getItem('authToken');
  }

  public login(login: Login) {
    const subscription = this.httpClient.post<Token>(
      `${environment.apiUrl}/auth/login`, login
    ).pipe(
      tap(Token => {
        if (Token.token) {
          this.loggedIn = true;
          this.saveTokenInLocalStorage(Token.token);
          this.token=Token.token;
        }
      })
    )

    return subscription;
  }
  private saveTokenInLocalStorage(token: string) {
    localStorage.setItem('authToken', token);
  }
  private removeTokenFromLocalStorage() {
    localStorage.removeItem('authToken');
  }
}
