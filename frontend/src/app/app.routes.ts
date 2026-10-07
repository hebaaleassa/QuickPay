import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { BulkUpload } from './pages/bulk-upload/bulk-upload';
import { Login } from './pages/login/login';
import { NotFound } from './pages/not-found/not-found';
import { PaymentDetails } from './pages/payment-details/payment-details';
import { PaymentForm } from './pages/payment-form/payment-form';
import { PaymentsList } from './pages/payments-list/payments-list';

// One route per page. New pages get a new line here.
export const routes: Routes = [
  // The first page is the login page.
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  // canActivate = the guard must allow the user in, otherwise they go to /login.
  { path: 'payments', component: PaymentsList, canActivate: [authGuard] },
  { path: 'payments/new', component: PaymentForm, canActivate: [authGuard] },
  { path: 'payments/bulk', component: BulkUpload, canActivate: [authGuard] },
  { path: 'payments/:id', component: PaymentDetails, canActivate: [authGuard] },
  { path: '**', component: NotFound },
];
