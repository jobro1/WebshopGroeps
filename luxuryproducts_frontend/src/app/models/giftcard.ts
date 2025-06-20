class CustomUser {
}

export interface Giftcard {
    id: number;
    code: string;
    initialValue: number;
    currentBalance: number;
    createdAt: string;
    expirationDate: string;

    activatedAt: string | null;
    message: string | null;
    recipientEmail: string;
    linkedUser: CustomUser | null;
    status: GiftcardStatus;
    failedAttempts: number;
    lastFailedAttempt: string | null;

}

export enum GiftcardStatus {
    ACTIVE = 'ACTIVE',
    EXPIRED = 'EXPIRED',
    USED = 'USED',
    CANCELLED = 'CANCELLED'
}
