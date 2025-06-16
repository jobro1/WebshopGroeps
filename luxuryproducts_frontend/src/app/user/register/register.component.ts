import {Component, DestroyRef, inject} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {RegisterService} from '../../services/register.service';
import {User} from '../../models/customUser';
import {CommonModule} from '@angular/common';
import {TranslatePipe} from "@ngx-translate/core";

@Component({
  selector: 'app-register',
    imports: [
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule
    ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  private registerService = inject(RegisterService)
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  protected registerForm = new FormGroup({
    "firstName": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "lastName": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "infix": new FormControl("",[ Validators.minLength(2)]),
    "dateOfBirth": new FormControl("", [Validators.required]),
    "address": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "houseNumber": new FormControl("",[Validators.required, Validators.minLength(1)]),
    "postcode": new FormControl("",[Validators.required, Validators.minLength(6)]),
    "phoneNumber": new FormControl("",[Validators.required, Validators.pattern("[0-9 ]{9}")]),
    "email": new FormControl("",[Validators.required, Validators.email]),
    "password": new FormControl("",[Validators.required,
                Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")]),

  });
  get firstName() {return this.registerForm.get("firstName");}
  get lastName() {return this.registerForm.get("lastName");}
  get infix() {return this.registerForm.get("infix");}
  get dateOfBirth() {return this.registerForm.get("dateOfBirth");}
  get address() {return this.registerForm.get("address");}
  get houseNumber() {return this.registerForm.get("houseNumber");}
  get postcode() {return this.registerForm.get("postcode");}
  get phoneNumber() {return this.registerForm.get("phoneNumber");}
  get email() {return this.registerForm.get("email");}
  get password() {return this.registerForm.get("password");}


  protected onSubmit() {
    if (this.registerForm.valid) {
      const newUser = this.registerForm.value as unknown as User;
      const subscription =
      this.registerService.creatUser(newUser).subscribe({
        next: () => {
          this.router.navigate([`userProfile/${this.email}`])
        },
        error: (err) => alert('Error: ' + err.message)
      });
      this.destroyRef.onDestroy(() => {
        subscription.unsubscribe();
      })
    }

    this.registerForm.reset();
  }

}
