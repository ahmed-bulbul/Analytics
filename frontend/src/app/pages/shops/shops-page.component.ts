import { Component, OnInit } from '@angular/core';
import { ShopAccessService } from '../../services/shop-access.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PageResult, ShopRow } from '../../models/shop.models';

@Component({
  selector: 'app-shops-page',
  templateUrl: './shops-page.component.html',
  styleUrls: ['./shops-page.component.scss']
})
export class ShopsPageComponent implements OnInit {
  domain = '';
  shops: ShopRow[] = [];
  total = 0;
  limit = 20;
  offset = 0;
  rateShopId?: number | null;
  rateCapacity = 120;
  rateRefill = 120;
  rateEnabled = true;

  constructor(private shopAccess: ShopAccessService, private http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.shopAccess.getMyShops(this.limit, this.offset, this.domain).subscribe((res: PageResult<ShopRow>) => {
      this.shops = res.items ?? [];
      this.total = res.total ?? 0;
    });
  }

  softDelete(shopId: number): void {
    if (!confirm('Soft delete this shop?')) return;
    this.http.delete(`${environment.apiUrl}/admin/shops/${shopId}`).subscribe({
      next: () => this.load()
    });
  }

  updateRateLimit(): void {
    if (!this.rateShopId) return;
    const payload = {
      enabled: this.rateEnabled,
      capacity: this.rateCapacity,
      refillPerMinute: this.rateRefill
    };
    this.http.post(`${environment.apiUrl}/admin/shops/${this.rateShopId}/rate-limit`, payload).subscribe({
      next: () => alert('Rate limit updated.')
    });
  }
}
