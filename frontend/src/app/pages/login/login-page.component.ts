import { Component } from '@angular/core';
import { AuthService, LoginRequest } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent {
  email = 'demo@shop.com';
  password = 'Demo1234!';
  error?: string;

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    this.error = undefined;
    const payload: LoginRequest = { email: this.email, password: this.password };
    this.auth.login(payload).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: () => this.error = 'Login failed. Check credentials.'
    });
  }
}
