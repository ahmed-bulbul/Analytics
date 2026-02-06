import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './pages/login/login-page.component';
import { DashboardPageComponent } from './pages/dashboard/dashboard-page.component';
import { OnboardingPageComponent } from './pages/onboarding/onboarding-page.component';
import { UsersPageComponent } from './pages/users/users-page.component';
import { ShopsPageComponent } from './pages/shops/shops-page.component';
import { AuditPageComponent } from './pages/audit/audit-page.component';
import { AuthGuard } from './services/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginPageComponent },
  { path: 'dashboard', component: DashboardPageComponent, canActivate: [AuthGuard] },
  { path: 'onboarding', component: OnboardingPageComponent, canActivate: [AuthGuard] },
  { path: 'users', component: UsersPageComponent, canActivate: [AuthGuard] },
  { path: 'shops', component: ShopsPageComponent, canActivate: [AuthGuard] },
  { path: 'audit', component: AuditPageComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
