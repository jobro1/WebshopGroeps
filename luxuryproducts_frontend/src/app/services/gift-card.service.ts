import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Giftcard, GiftcardStatus } from '../models/giftcard';

export interface CreateGiftCardRequest {
    value: number;
    recipientEmail?: string;
    message?: string;
}

@Injectable({
    providedIn: 'root'
})
export class GiftCardService {
    private apiUrl = `${environment.apiUrl}/giftcards`;

    // eslint-disable-next-line @angular-eslint/prefer-inject
    constructor(private http: HttpClient) {}

    getValidAmounts(): Observable<number[]> {
        return this.http.get<number[]>(`${this.apiUrl}/valid-amounts`);
    }

    createGiftCard(request: CreateGiftCardRequest): Observable<Giftcard> {
        return this.http.post<Giftcard>(this.apiUrl, request);
    }

    linkGiftCard(code: string): Observable<Giftcard> {
        return this.http.post<Giftcard>(`${this.apiUrl}/${code}/link`, {});
    }

    getMyGiftCards(): Observable<Giftcard[]> {
        return this.http.get<Giftcard[]>(`${this.apiUrl}/my-cards`);
    }

    updateGiftCardBalance(code: string, newBalance: number): Observable<Giftcard> {
        return this.http.patch<Giftcard>(`${this.apiUrl}/${code}/balance`, { balance: newBalance });
    }

    // Admin endpoints
    getAllGiftCards(params?: {
        status?: GiftcardStatus;
        email?: string;
        expired?: boolean;
    }): Observable<Giftcard[]> {
        return this.http.get<Giftcard[]>(`${this.apiUrl}/admin`, { params });
    }
}