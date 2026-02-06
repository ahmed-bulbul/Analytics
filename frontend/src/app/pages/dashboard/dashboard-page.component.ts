import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { KpiResponse, GrowthResponse, LtvResponse, CohortRow, ChannelRow } from '../../models/dashboard.models';
import { AuthService } from '../../services/auth.service';
import { SyncStatusService } from '../../services/sync-status.service';
import { SyncStatusResponse } from '../../models/sync-status.models';

@Component({
  selector: 'app-dashboard-page',
  templateUrl: './dashboard-page.component.html',
  styleUrls: ['./dashboard-page.component.scss']
})
export class DashboardPageComponent implements OnInit {
  kpis?: KpiResponse;
  growth?: GrowthResponse;
  ltv?: LtvResponse;
  cohorts: CohortRow[] = [];
  channels: ChannelRow[] = [];
  syncStatus?: SyncStatusResponse;
  syncError?: string;
  syncLoading = false;
  shopId?: number;

  readonly from = '2026-01-01';
  readonly to = '2026-01-03';

  constructor(
    private dashboard: DashboardService,
    private auth: AuthService,
    private syncStatusService: SyncStatusService
  ) {}

  ngOnInit(): void {
    this.auth.selectedShopChanges().subscribe(shopId => {
      if (!shopId) {
        this.syncError = 'No shop selected. Please login again or onboard a shop.';
        return;
      }
      this.shopId = shopId;
      this.refreshSyncStatus();
      this.dashboard.getKpis(shopId, this.from, this.to).subscribe(data => this.kpis = data);
      this.dashboard.getGrowth(shopId, this.from, this.to).subscribe(data => this.growth = data);
      this.dashboard.getLtv(shopId).subscribe(data => this.ltv = data);
      this.dashboard.getCohorts(shopId, this.from, this.to).subscribe(data => this.cohorts = data);
      this.dashboard.getChannels(shopId, this.from, this.to).subscribe(data => this.channels = data);
    });
  }

  refreshSyncStatus(): void {
    if (!this.shopId) return;
    this.syncLoading = true;
    this.syncError = undefined;
    this.syncStatusService.getStatus(this.shopId).subscribe({
      next: (data) => {
        this.syncStatus = data;
        this.syncLoading = false;
      },
      error: (err) => {
        this.syncError = err?.error?.message ?? 'Failed to load sync status';
        this.syncLoading = false;
      }
    });
  }
}
