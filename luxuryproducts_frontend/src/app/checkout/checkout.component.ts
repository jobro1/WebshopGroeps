import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {LoginService} from '../services/login.service';
import {CustomUserService} from '../services/customUser.service.';
import {CartService} from '../services/cart.service';
import {GiftCardService} from '../services/gift-card.service';
import {Giftcard, GiftcardStatus} from '../models/giftcard';
import {firstValueFrom} from 'rxjs';
import {CartItem} from '../models/cartItem';
import {TranslatePipe} from "@ngx-translate/core";
import { CommonModule } from '@angular/common';

interface CheckoutFormGroup {
  firstName: FormControl<string | null>;
  lastName: FormControl<string | null>;
  infix: FormControl<string | null>;
  address: FormControl<string | null>;
  houseNumber: FormControl<string | null>;
  postcode: FormControl<string | null>;
  phoneNumber: FormControl<string | null>;
  email: FormControl<string | null>;
  [key: string]: FormControl<string | null>; // For dynamic gift card recipient fields
}

@Component({
  selector: 'app-checkout',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe, CommonModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit {
  private router = inject(Router);
  private loginService = inject(LoginService);
  private customUserService = inject(CustomUserService);
  private cartService = inject(CartService);
  private giftCardService = inject(GiftCardService);
  protected user = this.customUserService.getUserInfo();
  error = signal('');
  protected loginRequest = false;
  private destroyRef = inject(DestroyRef);

  protected originalTotal = this.cartService.getCartCount();
  protected finalTotal = signal<number>(this.originalTotal);
  protected activeGiftCards = signal<Giftcard[]>([]);
  protected isLoadingGiftCards = signal(false);
  protected giftCardItems = signal<CartItem[]>([]);

  protected checkoutForm = new FormGroup<CheckoutFormGroup>({
    "firstName": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "lastName": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "infix": new FormControl("",[ Validators.minLength(2)]),
    "address": new FormControl("",[Validators.required, Validators.minLength(2)]),
    "houseNumber": new FormControl("",[Validators.required, Validators.minLength(1)]),
    "postcode": new FormControl("",[Validators.required, Validators.minLength(6)]),
    "phoneNumber": new FormControl("",[Validators.required, Validators.pattern("[0-9 ]{9}")]),
    "email": new FormControl("",[Validators.required, Validators.email]),
  });

  ngOnInit() {
    // Fill form with current user data if logged in
    if (this.loginService.isLoggedIn()) {
      const currentUser = this.user();
      if (currentUser) {
        this.checkoutForm.patchValue({
          firstName: currentUser.firstName,
          lastName: currentUser.lastName,
          infix: currentUser.infix || '',
          address: currentUser.address,
          houseNumber: currentUser.houseNumber.toString(),
          postcode: currentUser.postcode,
          phoneNumber: currentUser.phoneNumber,
          email: currentUser.email
        });

        // Load active gift cards for payment
        this.loadActiveGiftCards();

        // Check for gift cards in cart and add recipient fields
        const cartItems = this.cartService.getCartItems()();
        const giftCards = cartItems.filter((item: CartItem) => item.productVariation.sku?.startsWith('GC'));
        this.giftCardItems.set(giftCards);

        // Add form controls for gift card recipients
        giftCards.forEach((item: CartItem) => {
          const controlName = 'giftCardRecipient' + item.productVariation.sku;
          this.checkoutForm.addControl(controlName, new FormControl('', [Validators.required, Validators.email]));
        });
      }
    }
  }

  protected hasGiftCardsInCart(): boolean {
    return this.giftCardItems().length > 0;
  }

  private async loadActiveGiftCards() {
    this.isLoadingGiftCards.set(true);
    try {
      const giftCards = await firstValueFrom(this.giftCardService.getMyGiftCards());
      const activeCards = giftCards.filter(card =>
          card.status === GiftcardStatus.ACTIVE &&
          card.currentBalance > 0 &&
          new Date(card.expirationDate) > new Date()
      );

      this.activeGiftCards.set(activeCards);

      // Calculate potential savings without applying gift cards yet
      if (activeCards.length > 0) {
        let potentialTotal = this.originalTotal;
        for (const card of activeCards) {
          if (potentialTotal >= card.currentBalance) {
            potentialTotal -= card.currentBalance;
          } else {
            potentialTotal = 0;
            break;
          }
        }
        this.finalTotal.set(potentialTotal);
      } else {
        this.finalTotal.set(this.originalTotal);
      }
    } finally {
      this.isLoadingGiftCards.set(false);
    }
  }

  get firstName() {return this.checkoutForm.get("firstName");}
  get lastName() {return this.checkoutForm.get("lastName");}
  get infix() {return this.checkoutForm.get("infix");}
  get address() {return this.checkoutForm.get("address");}
  get houseNumber() {return this.checkoutForm.get("houseNumber");}
  get postcode() {return this.checkoutForm.get("postcode");}
  get phoneNumber() {return this.checkoutForm.get("phoneNumber");}
  get email() {return this.checkoutForm.get("email");}

  protected async onSubmit() {
    if (this.loginService.isLoggedIn()) {
      const currentUser = this.user();
      if (currentUser) {
          // First handle gift card purchases if any
          for (const item of this.giftCardItems()) {
            const recipientEmail = this.checkoutForm.get('giftCardRecipient' + item.productVariation.sku)?.value;
            if (recipientEmail) {
              // Create gift cards based on quantity purchased
              for (let i = 0; i < item.quantity; i++) {
                await firstValueFrom(this.giftCardService.createGiftCard({
                  value: item.productVariation.price,
                  recipientEmail: recipientEmail,
                  message: `Gift card from ${currentUser.firstName} ${currentUser.lastName}`
                }));
              }
            }
          }

          // Calculate final total after applying gift cards
          const finalTotal = await this.cartService.calculateTotalWithGiftCards();
          
          // Create order with the final total
          await this.cartService.createOrder(currentUser, finalTotal);

          // Reload user data to reflect updated gift card balances
          await firstValueFrom(this.customUserService.loadUserByEmail(currentUser.email));
          
          // Navigate to profile to see the order
          this.router.navigate([`userProfile/${currentUser.email}`]);
        }
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
