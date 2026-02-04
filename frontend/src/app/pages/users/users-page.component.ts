import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface Paged<T> { items: T[]; total: number; limit: number; offset: number; nextCursor?: string | null; }
interface UserRow { userId: number; email: string; role: string; }

@Component({
  selector: 'app-users-page',
  templateUrl: './users-page.component.html',
  styleUrls: ['./users-page.component.scss']
})
export class UsersPageComponent {
  shopId = 1;
  email = '';
  users: UserRow[] = [];
  total = 0;
  limit = 20;
  offset = 0;

  constructor(private http: HttpClient) {}

  load(): void {
    this.http.get<Paged<UserRow>>(`${environment.apiUrl}/shops/users`, {
      params: { shopId: this.shopId, email: this.email, limit: this.limit, offset: this.offset }
    }).subscribe(res => {
      this.users = res.items;
      this.total = res.total;
    });
  }
}
