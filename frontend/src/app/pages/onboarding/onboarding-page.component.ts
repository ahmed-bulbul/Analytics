import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { OnboardRequest } from '../../models/onboarding.models';

@Component({
  selector: 'app-onboarding-page',
  templateUrl: './onboarding-page.component.html',
  styleUrls: ['./onboarding-page.component.scss']
})
export class OnboardingPageComponent {
  onboardShopDomain = '';
  onboardAdminEmail = '';
  onboardAdminPassword = '';
  onboardMessage?: string;
  onboardOauthUrl?: string;

  constructor(private auth: AuthService) {}

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
}
