import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SyncStatusResponse } from '../models/sync-status.models';

@Injectable({ providedIn: 'root' })
export class SyncStatusService {
  private baseUrl = `${environment.apiUrl}/sync`;

  constructor(private http: HttpClient) {}

  getStatus(shopId: number): Observable<SyncStatusResponse> {
    return this.http.get<SyncStatusResponse>(`${this.baseUrl}/status`, {
      params: { shopId }
    });
  }
}
