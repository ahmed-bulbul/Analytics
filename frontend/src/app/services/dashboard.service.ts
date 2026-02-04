import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { KpiResponse, GrowthResponse, LtvResponse, CohortRow, ChannelRow } from '../models/dashboard.models';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private baseUrl = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getKpis(shopId: number | null, from: string, to: string): Observable<KpiResponse> {
    const params: any = { from, to };
    if (shopId !== null) {
      params.shopId = shopId;
    }
    return this.http.get<KpiResponse>(`${this.baseUrl}/kpis`, { params });
  }

  getGrowth(shopId: number | null, from: string, to: string): Observable<GrowthResponse> {
    const params: any = { from, to };
    if (shopId !== null) {
      params.shopId = shopId;
    }
    return this.http.get<GrowthResponse>(`${this.baseUrl}/growth`, { params });
  }

  getLtv(shopId: number | null): Observable<LtvResponse> {
    const params: any = {};
    if (shopId !== null) {
      params.shopId = shopId;
    }
    return this.http.get<LtvResponse>(`${this.baseUrl}/ltv`, { params });
  }

  getCohorts(shopId: number | null, from: string, to: string): Observable<CohortRow[]> {
    const params: any = { from, to };
    if (shopId !== null) {
      params.shopId = shopId;
    }
    return this.http.get<CohortRow[]>(`${this.baseUrl}/cohorts`, { params });
  }

  getChannels(shopId: number | null, from: string, to: string): Observable<ChannelRow[]> {
    const params: any = { from, to };
    if (shopId !== null) {
      params.shopId = shopId;
    }
    return this.http.get<ChannelRow[]>(`${this.baseUrl}/channels`, { params });
  }
}
