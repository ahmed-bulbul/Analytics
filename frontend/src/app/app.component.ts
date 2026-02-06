import { Component, DoCheck } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';
import { ShopAccessService } from './services/shop-access.service';
import { ShopRow } from './models/shop.models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements DoCheck {
  shops: ShopRow[] = [];
  selectedShopId?: number | null;
  private lastToken?: string | null;

  constructor(
    private auth: AuthService,
    private router: Router,
    private shopAccess: ShopAccessService
  ) {}

  get isLoggedIn(): boolean {
    return !!this.auth.getToken();
  }

  ngDoCheck(): void {
    const token = this.auth.getToken();
    if (token !== this.lastToken) {
      this.lastToken = token;
      if (token) {
        this.loadShops();
      } else {
        this.shops = [];
        this.selectedShopId = null;
      }
    }
  }

  loadShops(): void {
    this.shopAccess.getMyShops(200, 0).subscribe({
      next: (res) => {
        this.shops = res.items ?? [];
        const stored = this.auth.getShopId();
        const first = this.shops.length > 0 ? this.shops[0].shopId : null;
        this.selectedShopId = stored ?? first;
        if (this.selectedShopId) {
          this.auth.setSelectedShopId(this.selectedShopId);
        }
      }
    });
  }

  onShopChange(value: string): void {
    const parsed = Number(value);
    if (!Number.isNaN(parsed)) {
      this.selectedShopId = parsed;
      this.auth.setSelectedShopId(parsed);
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
