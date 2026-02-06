import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../services/auth.service';

interface Paged<T> { items: T[]; total: number; limit: number; offset: number; nextCursor?: string | null; }
interface UserRow { userId: number; email: string; role: string; }

@Component({
  selector: 'app-users-page',
  templateUrl: './users-page.component.html',
  styleUrls: ['./users-page.component.scss']
})
export class UsersPageComponent implements OnInit {
  shopId?: number | null;
  email = '';
  users: UserRow[] = [];
  total = 0;
  limit = 20;
  offset = 0;

  constructor(private http: HttpClient, private auth: AuthService) {}

  ngOnInit(): void {
    this.auth.selectedShopChanges().subscribe(() => this.load());
  }

  load(): void {
    this.shopId = this.auth.getShopId();
    if (!this.shopId) return;
    this.http.get<Paged<UserRow>>(`${environment.apiUrl}/shops/users`, {
      params: { shopId: this.shopId, email: this.email, limit: this.limit, offset: this.offset }
    }).subscribe(res => {
      this.users = res.items;
      this.total = res.total;
    });
  }

  softDelete(userId: number): void {
    if (!confirm('Soft delete this user?')) return;
    this.http.delete(`${environment.apiUrl}/admin/users/${userId}`).subscribe({
      next: () => this.load()
    });
  }
}
