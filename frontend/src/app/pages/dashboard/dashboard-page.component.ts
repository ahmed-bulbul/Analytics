import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { KpiResponse, GrowthResponse, LtvResponse, CohortRow, ChannelRow } from '../../models/dashboard.models';
import { AuthService } from '../../services/auth.service';

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

  readonly from = '2026-01-01';
  readonly to = '2026-01-03';

  constructor(private dashboard: DashboardService, private auth: AuthService) {}

  ngOnInit(): void {
    const shopId = this.auth.getShopId();
    this.dashboard.getKpis(shopId, this.from, this.to).subscribe(data => this.kpis = data);
    this.dashboard.getGrowth(shopId, this.from, this.to).subscribe(data => this.growth = data);
    this.dashboard.getLtv(shopId).subscribe(data => this.ltv = data);
    this.dashboard.getCohorts(shopId, this.from, this.to).subscribe(data => this.cohorts = data);
    this.dashboard.getChannels(shopId, this.from, this.to).subscribe(data => this.channels = data);
  }
}
