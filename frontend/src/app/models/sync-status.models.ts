export interface SyncStatusResponse {
  shopId: number;
  status: string;
  currentType?: string | null;
  operationId?: string | null;
  updatedAt?: string | null;
  lastIncrementalSyncAt?: string | null;
}
