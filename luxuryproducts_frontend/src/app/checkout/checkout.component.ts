import {Component, DestroyRef, inject, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {LoginService} from '../services/login.service';
import {CustomUserService} from '../services/customUser.service.';
import {CartService} from '../services/cart.service';
import {TranslatePipe} from "@ngx-translate/core";


@Component({
  selector: 'app-checkout',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent {
  private router = inject(Router);
  private loginService = inject(LoginService);
  private customUserService = inject(CustomUserService);
  private cartService = inject(CartService);
  protected user = this.customUserService.getUserInfo();
  error = signal('');
  protected loginRequest = false
  private destroyRef = inject(DestroyRef);



  protected checkoutForm = new FormGroup({
    "firstName": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "lastName": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "infix": new FormControl("",[ Validators.minLength(2)]),
    "address": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "houseNumber": new FormControl("",[Validators.required, Validators.minLength(1)]),
    "postcode": new FormControl("",[Validators.required, Validators.minLength(6)]),
    "phoneNumber": new FormControl("",[Validators.required, Validators.pattern("[0-9 ]{9}")]),
    "email": new FormControl("",[Validators.required, Validators.email]),

  });
  get firstName() {return this.checkoutForm.get("firstName");}
  get lastName() {return this.checkoutForm.get("lastName");}
  get infix() {return this.checkoutForm.get("infix");}
  get address() {return this.checkoutForm.get("address");}
  get houseNumber() {return this.checkoutForm.get("houseNumber");}
  get postcode() {return this.checkoutForm.get("postcode");}
  get phoneNumber() {return this.checkoutForm.get("phoneNumber");}
  get email() {return this.checkoutForm.get("email");}



  protected onSubmit() {

    if (this.loginService.isLoggedIn()) {
      const userEmail = this.email?.value as string;
      const subscription =
      this.customUserService.loadUserByEmail(userEmail).subscribe({
          error: (error: Error) => {

            this.error.set(error.message);
          },
          complete: () => {
            const updatedUser = this.user();
            if (updatedUser) {
              console.log(updatedUser);
              this.cartService.createOrder(updatedUser);
              this.router.navigate([`userProfile/${userEmail}`]);
            }


          }
      });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      })


    } else {

      this.loginRequest = true;
    }
    this.checkoutForm.reset();
  }


  onLoginButton() {
    this.router.navigate(['/login']);
  }

  onRegisterButton() {
    this.router.navigate(['/register']);
  }
}
