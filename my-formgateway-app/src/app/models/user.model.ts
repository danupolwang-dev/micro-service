export interface Role {
  id: number;
  name: string;
  status: string;
}

export interface UserRole {
  id: number;
  role: Role;
  status: string;
}

export interface User {
  id: number;
  userName: string;
  email: string;
  mobileNo: string;
  createdDt: string;
  suspended: boolean;
  userRoles?: UserRole[]; // Optional because sometimes we might not fetch roles
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}
