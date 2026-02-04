export interface RegisterRequest {
  email: string;
  password: string;
  role: 'ADMIN' | 'VIEWER';
  shopId: number;
}

export interface RegisterResponse {
  userId: number;
  email: string;
  role: string;
  shopId: number;
}
