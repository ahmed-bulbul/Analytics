export interface KpiResponse {
  revenueGross: number;
  netSales: number;
  orders: number;
  adSpend: number;
  mer: number;
  aov: number;
}

export interface GrowthResponse {
  newOrders: number;
  returningOrders: number;
  newRevenue: number;
  returningRevenue: number;
}

export interface LtvResponse {
  averageCustomerLtv: number;
  ltv30: number;
  ltv60: number;
  ltv90: number;
}

export interface CohortRow {
  cohortMonth: string;
  monthsSinceFirstOrder: number;
  customersInCohort: number;
  netSales: number;
  cumulativeNetSales: number;
}

export interface ChannelRow {
  channelKey: string;
  channelName: string;
  date: string;
  spend: number;
  impressions: number;
  clicks: number;
  attributedOrders: number;
  attributedRevenue: number;
}
