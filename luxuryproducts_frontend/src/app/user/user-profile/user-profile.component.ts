import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {CustomUserService} from '../../services/customUser.service.';
import {ActivatedRoute, Router} from '@angular/router';
import {OrderService} from '../../services/order.service';
import {LoginService} from '../../services/login.service';
import {firstValueFrom, last} from "rxjs";
import {DatePipe, DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
import {OrderItem} from "../../models/orderItem";
import {GiftCardService} from "../../services/gift-card.service";
import {GiftcardStatus} from "../../models/giftcard";

@Component({
  selector: 'app-user-profile',
  imports: [
    NgIf,
    NgForOf,
    DecimalPipe,
    TranslatePipe,
    DatePipe
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss'
})
export class UserProfileComponent implements OnInit {
  private customUserService = inject(CustomUserService);
  private orderService = inject(OrderService);
  private loginService = inject(LoginService);
  private giftCardService = inject(GiftCardService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private loggedInUser = this.loginService.getAuthToken();
  error = signal('');
  linkingGiftCard = signal(false);
  activeTab = signal<'active' | 'other'>('active');

  protected userInfo = this.customUserService.getUserInfo();
  protected usersOrders = this.orderService.getOrders();

  protected get filteredGiftCards() {
    const giftcards = this.userInfo()?.giftcards || [];
    
    if (this.activeTab() === 'active') {
      return giftcards.filter(card => 
        card.status === GiftcardStatus.ACTIVE && 
        card.currentBalance > 0 &&
        new Date(card.expirationDate) > new Date()
      );
    } else {
      return giftcards.filter(card => 
        card.status === GiftcardStatus.USED || 
        card.status === GiftcardStatus.EXPIRED || 
        card.status === GiftcardStatus.CANCELLED ||
        (card.status === GiftcardStatus.ACTIVE && 
         (card.currentBalance <= 0 || new Date(card.expirationDate) <= new Date()))
      );
    }
  }

  protected switchTab(tab: 'active' | 'other') {
    this.activeTab.set(tab);
  }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const email = params.get('email');

      if (email) {
        const subscription =
          this.customUserService.loadUserByEmail(email).subscribe({
            error: (error: Error) => {

              this.error.set(error.message);
            },
            complete: () => {
              const user = this.userInfo();
              const userId = user?.userId
              if (userId !== undefined) {
                this.loadOrdersByUserId(userId);
              }

            }
          });
        this.destroyRef.onDestroy(() => {
          subscription.unsubscribe();
        })


      }


    });

  }

  loadOrdersByUserId(userId: number) {
    this.orderService.loadOrdersByUserId(userId);
  }

  onLogout() {
    this.loginService.logout();
    this.router.navigate([`/login`]);
  }


  protected async linkGiftCard(orderItem: OrderItem, recipientEmail: string) {
    if (!recipientEmail || orderItem.sku?.startsWith('GC')) {
      alert('Please enter a valid recipient email address and select a gift card product.');
      return;
    }

    try {
      this.linkingGiftCard.set(true);

      // Create a gift card for the recipient
      await firstValueFrom(this.giftCardService.createGiftCard({
        value: orderItem.subtotal,
        recipientEmail: recipientEmail,
        // was {orderItem.orderId}
        message: `Gift card from order #${orderItem.orderItemId}`
      }));

      // Refresh user info to show updated gift cards
      const user = this.userInfo();
      if (user?.email) {
        await firstValueFrom(this.customUserService.loadUserByEmail(user.email));
      }

      // Show success message
      alert('Gift card has been linked to ' + recipientEmail);
    } catch (error) {
      console.error('Failed to link gift card:', error);
      alert('Failed to link gift card. Please try again.');
    } finally {
      this.linkingGiftCard.set(false);
    }
  }

  protected readonly last = last;
}
