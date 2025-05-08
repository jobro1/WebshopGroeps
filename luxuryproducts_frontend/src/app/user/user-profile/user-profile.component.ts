import {Component, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {CustomUserService} from '../../services/customUser.service.';
import {ActivatedRoute, Router} from '@angular/router';
import {OrderService} from '../../services/order.service';
import {LoginService} from '../../services/login.service';


@Component({
  selector: 'app-user-profile',
  imports: [],
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
  private loggedInUser = this.loginService.getAuthToken();
  error = signal('');

  protected userInfo = this.customUserService.getUserInfo();
  protected usersOrders = this.orderService.getOrders()

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



}
