export interface JwtPayload {
    sub: string;
    email?: string;
    role?: string;
    roles?: string[];
    exp: number;
    [key: string]: unknown;
}