import { Component } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private auth: AuthService, private router: Router) {}

  get isLoggedIn(): boolean {
    return !!this.auth.getToken();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
