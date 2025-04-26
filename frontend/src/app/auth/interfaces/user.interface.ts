export enum Role {
  user = 'USER',
  admin = 'ADMIN',
}

export interface User {
  id: string;
  email: string;
  name: string;
  password?: string;
  role?: Role;
}

export interface UserResponse {
  user: User;
  token: string;
}
