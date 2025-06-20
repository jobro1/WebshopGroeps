import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {User} from '../models/customUser';


@Injectable({
  providedIn: 'root'
})
export class RegisterService{
  private httpClient = inject(HttpClient);



  public creatUser(newUser: User)  {
    const subscription = this.httpClient.post<User>(
      `${environment.apiUrl}/auth/register`,newUser)
    return subscription;
  }


}
