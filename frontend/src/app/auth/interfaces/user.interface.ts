import { Product } from '../../home/interfaces/Data.interface';

export enum Role {
  user = 'ROLE_USER',
  admin = 'ROLE_ADMIN',
}

interface userRole {
  authority: Role;
}

export interface User {
  id: string;
  email: string;
  username: string;
  password?: string;
  wishList: Product[];
  roles: userRole[];
  enabled: boolean;
  accountLocked: boolean;
}

export interface UserResponse {
  user: User;
  token: string;
}
