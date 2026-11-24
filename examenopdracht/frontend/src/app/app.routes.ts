import { Routes } from '@angular/router';
import { authGuard } from './auth.guard';
import { LoginPage } from './login-page';
import { MenuPage } from './menu-page';

export const routes: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'menu', component: MenuPage, canActivate: [authGuard] },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
  { path: '**', redirectTo: 'login' },
];
