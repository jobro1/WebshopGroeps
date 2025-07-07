import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/customUser';
import {environment} from '../../environments/environment';
import {tap} from 'rxjs';
import {jwtDecode} from "jwt-decode";
import {JwtPayload} from "../models/JwtPayload";

@Injectable({
  providedIn: 'root'
})
export class CustomUserService {
  private user = signal<User | null> (null)
  private httpClient = inject(HttpClient);

  getUserInfo() {
    return this.user.asReadonly();
  }
  public loadUserByEmail(email: string) {
     return  this.httpClient.get<User>(environment.apiUrl + `/users/${email}`).pipe(tap({
      next: (data) => this.user.set(data),
      error: (err) => console.error('Error loading user:', err)
    }))
  }
  public getEmail(): string | null {
        const token = localStorage.getItem('authToken');
        if (!token) return null;

        try {
            const decoded = jwtDecode<JwtPayload>(token);
            return decoded.email ?? null;
        } catch (err) {
            console.error('Failed to decode token:', err);
            return null;
        }
  }

}
