enum Role {
  user = 'USER',
  admin = 'ADMIN',
}

export interface User {
  id: string;
  email: string;
  password?: string;
  role: Role;
}
