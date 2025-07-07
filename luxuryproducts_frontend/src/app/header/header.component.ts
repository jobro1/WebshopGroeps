import {Component, inject} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {CustomUserService} from '../services/customUser.service.';
import {LoginService} from '../services/login.service';
import {FormsModule} from '@angular/forms';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';
import {SwitchLanguageComponent} from './switch-language/switch-language.component';
import translationsEN from "../../../public/i18n/en.json";
import translationsNL from "../../../public/i18n/nl.json";
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-header',
  imports: [RouterLink, FormsModule, TranslatePipe, SwitchLanguageComponent, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  private customUserService = inject(CustomUserService);
  private loginService = inject(LoginService);
  private router = inject(Router);

  onUserClick() {
    if (this.loginService.isLoggedIn()) {
      const email = this.customUserService.getEmail()
      this.router.navigate([`userProfile/${email}`]);
    } else {
      this.router.navigate(['/login']);
    }
  }

  isNavbarActive = false;

  toggleNavbar(): void {
    this.isNavbarActive = !this.isNavbarActive;
  }

  private translateService = inject(TranslateService);

  constructor() {
    this.initialiseTranslateService();
  }

  private initialiseTranslateService() {
    this.translateService.addLangs(['nl', 'en']);
    this.translateService.setTranslation('en', translationsEN)
    this.translateService.setTranslation('nl', translationsNL)
  }

  isAdmin(): boolean {
    return this.loginService.isAdmin();
  }
}
