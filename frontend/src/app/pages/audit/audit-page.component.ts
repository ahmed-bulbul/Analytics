import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface AuditRow {
  id: number;
  action: string;
  actorUserId?: number | null;
  actorEmail?: string | null;
  targetUserId?: number | null;
  targetShopId?: number | null;
  metadata?: string | null;
  ipAddress?: string | null;
  userAgent?: string | null;
  createdAt: string;
}

interface PageResult<T> {
  items: T[];
  total: number;
  limit: number;
  offset: number;
  nextCursor?: string | null;
}

@Component({
  selector: 'app-audit-page',
  templateUrl: './audit-page.component.html',
  styleUrls: ['./audit-page.component.scss']
})
export class AuditPageComponent implements OnInit {
  action = '';
  shopId?: number | null;
  rows: AuditRow[] = [];
  total = 0;
  limit = 25;
  offset = 0;
  error?: string;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.error = undefined;
    const params: any = { limit: this.limit, offset: this.offset };
    if (this.action) params.action = this.action;
    if (this.shopId) params.shopId = this.shopId;
    this.http.get<PageResult<AuditRow>>(`${environment.apiUrl}/audit`, { params }).subscribe({
      next: (res) => {
        this.rows = res.items ?? [];
        this.total = res.total ?? 0;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load audit logs';
      }
    });
  }
}
