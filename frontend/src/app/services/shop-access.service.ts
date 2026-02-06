import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PageResult, ShopRow } from '../models/shop.models';

@Injectable({ providedIn: 'root' })
export class ShopAccessService {
  private baseUrl = `${environment.apiUrl}/shops`;

  constructor(private http: HttpClient) {}

  getMyShops(limit = 200, offset = 0, domain = ''): Observable<PageResult<ShopRow>> {
    return this.http.get<PageResult<ShopRow>>(`${this.baseUrl}/my`, {
      params: { limit, offset, domain }
    });
  }
}
