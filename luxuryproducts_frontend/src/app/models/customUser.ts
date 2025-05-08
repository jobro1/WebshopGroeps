import {Order} from './order';

export interface User {
  userId?: number;
  firstName: string;
  infix?: string;
  lastName: string;
  address: string;
  houseNumber: number;
  postcode: string;
  dateOfBirth: string;
  phoneNumber: string;
  userType: string;
  email: string;
  password?: string;
  orders?: Order[];
}
