export interface ShopRow {
  shopId: number;
  shopDomain: string;
  timezone: string;
  currency: string;
}

export interface PageResult<T> {
  items: T[];
  total?: number | null;
  limit?: number | null;
  offset?: number | null;
  nextCursor?: string | null;
}
