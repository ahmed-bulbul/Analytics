import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, BehaviorSubject } from 'rxjs';
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
  private selectedShopSubject = new BehaviorSubject<number | null>(this.getShopId());

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('shopId', String(response.shopId));
        this.setSelectedShopId(response.shopId);
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

  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) return false;
    const parts = token.split('.');
    if (parts.length !== 3) return false;
    try {
      const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
      if (!payload.exp) return true;
      const now = Math.floor(Date.now() / 1000);
      return payload.exp > now;
    } catch {
      return false;
    }
  }

  getShopId(): number | null {
    const selected = localStorage.getItem('selectedShopId');
    if (selected) return Number(selected);
    const value = localStorage.getItem('shopId');
    return value ? Number(value) : null;
  }

  getPrimaryShopId(): number | null {
    const value = localStorage.getItem('shopId');
    return value ? Number(value) : null;
  }

  setSelectedShopId(shopId: number): void {
    localStorage.setItem('selectedShopId', String(shopId));
    this.selectedShopSubject.next(shopId);
  }

  selectedShopChanges(): Observable<number | null> {
    return this.selectedShopSubject.asObservable();
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('shopId');
    localStorage.removeItem('selectedShopId');
    this.selectedShopSubject.next(null);
  }
}
