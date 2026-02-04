import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface Paged<T> { items: T[]; total: number; limit: number; offset: number; nextCursor?: string | null; }
interface ShopRow { shopId: number; shopDomain: string; timezone: string; currency: string; }

@Component({
  selector: 'app-shops-page',
  templateUrl: './shops-page.component.html',
  styleUrls: ['./shops-page.component.scss']
})
export class ShopsPageComponent {
  userId = 1;
  domain = '';
  shops: ShopRow[] = [];
  total = 0;
  limit = 20;
  offset = 0;

  constructor(private http: HttpClient) {}

  load(): void {
    this.http.get<Paged<ShopRow>>(`${environment.apiUrl}/shops/shops`, {
      params: { userId: this.userId, domain: this.domain, limit: this.limit, offset: this.offset }
    }).subscribe(res => {
      this.shops = res.items;
      this.total = res.total;
    });
  }
}
