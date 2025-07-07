import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {CustomUserService} from '../../services/customUser.service.';
import {ActivatedRoute, Router} from '@angular/router';
import {OrderService} from '../../services/order.service';
import {LoginService} from '../../services/login.service';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from "@angular/common";
import {TranslatePipe} from "@ngx-translate/core";
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
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  error = signal('');
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

  ngOnInit(): void {
    this.route.paramMap.subscribe(() => {
      const email = this.customUserService.getEmail();

      if (email) {
        const subscription = this.customUserService.loadUserByEmail(email).subscribe({
          error: (error: Error) => {
            this.error.set(error.message);
          },
          complete: () => {
            const user = this.userInfo();
            const userId = user?.userId;

            if (userId !== undefined) {
              this.orderService.loadOrdersByUserId(userId);
            }
          }
        });

        this.destroyRef.onDestroy(() => {
          subscription.unsubscribe();
        });
      }
    });
  }

  onLogout() {
    this.loginService.logout();
    this.router.navigate([`/login`]);
  }
}
