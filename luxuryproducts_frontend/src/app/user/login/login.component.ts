import {Component, inject} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {LoginService} from '../../services/login.service';
import {FormsModule} from '@angular/forms';
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-login',
    imports: [FormsModule, RouterLink, TranslatePipe],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  protected email = "";
  protected password = "";
  protected passwordInValid = false;
  protected loginService = inject(LoginService);
  private router = inject(Router);

  login() {
    const subscription = this.loginService.login({email: this.email, password: this.password})
    subscription.subscribe({
      next: () => {
        this.router.navigate([`userProfile/${this.email}`]);
      },
      error: (error) => {
        console.log(error);
        this.passwordInValid = true;
      }
    });
    if (this.loginService.isLoggedIn()) {
      this.router.navigate([`userProfile/${this.email}`]);
    }
  }
}
