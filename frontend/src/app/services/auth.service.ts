import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { RegisterRequest, RegisterResponse } from '../models/auth.models';
import { OnboardRequest, OnboardResponse } from '../models/onboarding.models';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  shopId: number;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('shopId', String(response.shopId));
      })
    );
  }

  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/register`, request);
  }

  onboard(request: OnboardRequest): Observable<OnboardResponse> {
    return this.http.post<OnboardResponse>(`${environment.apiUrl}/shops/onboard`, request);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getShopId(): number | null {
    const value = localStorage.getItem('shopId');
    return value ? Number(value) : null;
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('shopId');
  }
}
