export interface OnboardRequest {
  shopDomain: string;
  adminEmail: string;
  adminPassword: string;
}

export interface OnboardResponse {
  shopId: number;
  shopDomain: string;
  oauthUrl: string;
}
