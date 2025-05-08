import {Component, inject} from '@angular/core';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';



@Component({
  selector: 'app-switch-language',
  imports: [
    TranslatePipe
  ],
  templateUrl: './switch-language.component.html',
  styleUrl: './switch-language.component.scss'
})
export class SwitchLanguageComponent {
  private translateService = inject(TranslateService)

  switchLanguage(language: string) {
    this.translateService.use(language);
  }


}
