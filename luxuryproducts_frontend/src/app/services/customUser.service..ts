import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/customUser';
import {environment} from '../../environments/environment';
import {tap} from 'rxjs';

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
}
