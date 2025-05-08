import {CanMatchFn, RedirectCommand, Router, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AllProductsComponent} from './pages/all-products/all-products.component';
import {CartComponent} from './shopping-cart/cart/cart.component';
import {CheckoutComponent} from './checkout/checkout.component';
import {LoginComponent} from './user/login/login.component';
import {UserProfileComponent} from './user/user-profile/user-profile.component';
import {inject} from '@angular/core';
import {LoginService} from './services/login.service';
import {ProductDetailComponent} from './products/product-detail/product-detail.component';
import {ProductsByCategoryComponent} from './categories/products-by-category/products-by-category.component';
import {RegisterComponent} from './user/register/register.component';


const canAccessUserProfile: CanMatchFn = (route, segments) => {
  const router = inject(Router)
  const loginService = inject(LoginService)
  if(loginService.isLoggedIn()){
    return true;
  }
  return new RedirectCommand(router.parseUrl("/"))
}

export const routes: Routes = [
  {
    path: "",
    component: HomeComponent,
  },
  {
    path: 'products',
    component: AllProductsComponent,
  },
  {
    path: 'products/:productId',
    component: ProductDetailComponent,
  },
  {
    path: 'category/:categoryId',
    component: ProductsByCategoryComponent
  },
  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: 'cart',
    component: CartComponent,
  },
  {
    path: 'checkout',
    component: CheckoutComponent,
  },
  {
    path: 'userProfile/:email',
    canMatch: [canAccessUserProfile],
    component: UserProfileComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  }
];
