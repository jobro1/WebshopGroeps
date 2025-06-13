import {inject, Injectable, signal, WritableSignal} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {firstValueFrom} from "rxjs";
import {VariationFormPayload} from "../models/variationFormPayload";
import {Router} from "@angular/router";
import {UpdateVariantDTO} from "../models/dtos/updateVariantDTO";

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private httpClient = inject(HttpClient);
  private router = inject(Router);
  private isCurrentUserAnAdmin = signal<boolean>(false)

  async isAdmin(userId: string) {
    try {
      const url = environment.apiUrl + "/users/isAdmin/" + userId
      const isAdmin = await firstValueFrom(this.httpClient.get<boolean>(url));
      return isAdmin ?? false;
    } catch {
      return false;
    }
  }

  async createNewVariation(payload: VariationFormPayload): Promise<any> {
    const url = environment.apiUrl + "/admin/createVariant";
    try {
      return await firstValueFrom(this.httpClient.post(url, payload));
    } catch (error) {
      console.error("Fout bij aanmaken variatie:", error);
      throw error;
    }
  }

  public deleteVariation(sku: string) {
    const url = environment.apiUrl + "/admin/delete/" + sku;
    this.httpClient.delete(url).subscribe();
    this.reloadRoute();
  }

  private reloadRoute() {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
      this.router.navigate([currentUrl]);
    });

  }

  updateVariation(updatedVariant: UpdateVariantDTO) {
    const url = environment.apiUrl + "/admin/updateVariation";
    this.httpClient.put(url, updatedVariant).subscribe();
    this.reloadRoute();
  }
}
