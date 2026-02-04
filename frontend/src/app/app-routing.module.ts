import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginPageComponent } from './pages/login/login-page.component';
import { DashboardPageComponent } from './pages/dashboard/dashboard-page.component';
import { OnboardingPageComponent } from './pages/onboarding/onboarding-page.component';
import { UsersPageComponent } from './pages/users/users-page.component';
import { ShopsPageComponent } from './pages/shops/shops-page.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginPageComponent },
  { path: 'dashboard', component: DashboardPageComponent },
  { path: 'onboarding', component: OnboardingPageComponent },
  { path: 'users', component: UsersPageComponent },
  { path: 'shops', component: ShopsPageComponent },
  { path: '**', redirectTo: 'dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
