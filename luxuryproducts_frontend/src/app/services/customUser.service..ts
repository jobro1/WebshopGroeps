import {inject, Injectable, signal} from '@angular/core';
import {CartItem} from '../models/cartItem';
import {HttpClient} from '@angular/common/http';
import {User} from '../models/customUser';
import {Product} from '../models/product';
import {environment} from '../../environments/environment';
import {catchError, map, tap, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CustomUserService {
  private users = signal<User[]>([]);
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




  // loadUserByEmail(email: string) {
  //   return this.getUserByEmail(email).pipe(tap({
  //     next: (user) => {this.user.set(user)}
  //   }));
  // }
  //
  // private getUserByEmail(email: string) {
  //   return this.httpClient.get<{ user: User }>(environment.apiUrl + `/users/${email}`).pipe(
  //     map(response => response.user),
  //     catchError((error) => {
  //       console.log(error);
  //       return throwError(()=> new Error("something went wrong, Please try again later"));
  //     })
  //
  //   )
  //
  // }


}
