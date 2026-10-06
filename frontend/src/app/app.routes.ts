import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { PaymentsList } from './pages/payments-list/payments-list';

// One route per page. New pages get a new line here.
export const routes: Routes = [
  // The first page is the login page.
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  // canActivate = the guard must allow the user in, otherwise they go to /login.
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: 'payments', component: PaymentsList, canActivate: [authGuard] },
];
