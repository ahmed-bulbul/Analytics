import { Component, OnInit } from '@angular/core';
import { DashboardService } from './services/dashboard.service';
import { AuthService, LoginRequest } from './services/auth.service';
import { RegisterRequest } from './models/auth.models';
import { OnboardRequest } from './models/onboarding.models';
import { KpiResponse, GrowthResponse, LtvResponse, CohortRow, ChannelRow } from './models/dashboard.models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  providers: [DashboardService]
})
export class AppComponent implements OnInit {
  kpis?: KpiResponse;
  growth?: GrowthResponse;
  ltv?: LtvResponse;
  cohorts: CohortRow[] = [];
  channels: ChannelRow[] = [];

  email = 'demo@shop.com';
  password = 'Demo1234!';
  error?: string;
  registerEmail = '';
  registerPassword = '';
  registerRole: 'ADMIN' | 'VIEWER' = 'VIEWER';
  registerMessage?: string;
  onboardShopDomain = '';
  onboardAdminEmail = '';
  onboardAdminPassword = '';
  onboardMessage?: string;
  onboardOauthUrl?: string;

  readonly from = '2026-01-01';
  readonly to = '2026-01-03';

  constructor(private dashboard: DashboardService, private auth: AuthService) {}

  ngOnInit(): void {
    const token = this.auth.getToken();
    if (token) {
      this.loadDashboard();
    }
  }

  login(): void {
    this.error = undefined;
    const payload: LoginRequest = { email: this.email, password: this.password };
    this.auth.login(payload).subscribe({
      next: () => this.loadDashboard(),
      error: () => this.error = 'Login failed. Check credentials.'
    });
  }

  register(): void {
    this.registerMessage = undefined;
    const shopId = this.auth.getShopId();
    if (!shopId) {
      this.registerMessage = 'Login required.';
      return;
    }
    const payload: RegisterRequest = {
      email: this.registerEmail,
      password: this.registerPassword,
      role: this.registerRole,
      shopId
    };
    this.auth.register(payload).subscribe({
      next: (res) => {
        this.registerMessage = `User created: ${res.email} (${res.role})`;
        this.registerEmail = '';
        this.registerPassword = '';
        this.registerRole = 'VIEWER';
      },
      error: () => this.registerMessage = 'Registration failed. Check admin permissions.'
    });
  }

  onboard(): void {
    this.onboardMessage = undefined;
    this.onboardOauthUrl = undefined;
    const payload: OnboardRequest = {
      shopDomain: this.onboardShopDomain,
      adminEmail: this.onboardAdminEmail,
      adminPassword: this.onboardAdminPassword
    };
    this.auth.onboard(payload).subscribe({
      next: (res) => {
        this.onboardMessage = `Shop created: ${res.shopDomain} (ID ${res.shopId})`;
        this.onboardOauthUrl = res.oauthUrl;
      },
      error: (err) => {
        const msg = err?.error?.message || 'Onboarding failed. Check inputs.';
        this.onboardMessage = msg;
      }
    });
  }

  logout(): void {
    this.auth.logout();
    this.kpis = undefined;
    this.growth = undefined;
    this.ltv = undefined;
    this.cohorts = [];
    this.channels = [];
    this.registerMessage = undefined;
    this.onboardMessage = undefined;
    this.onboardOauthUrl = undefined;
  }

  private loadDashboard(): void {
    const shopId = this.auth.getShopId();
    this.dashboard.getKpis(shopId, this.from, this.to).subscribe(data => this.kpis = data);
    this.dashboard.getGrowth(shopId, this.from, this.to).subscribe(data => this.growth = data);
    this.dashboard.getLtv(shopId).subscribe(data => this.ltv = data);
    this.dashboard.getCohorts(shopId, this.from, this.to).subscribe(data => this.cohorts = data);
    this.dashboard.getChannels(shopId, this.from, this.to).subscribe(data => this.channels = data);
  }
}
