import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BulkResult } from '../models/bulk-result';
import { Page } from '../models/page';
import { Payment, PaymentRequest } from '../models/payment';

// All payment HTTP calls live here. The token is added by the interceptor, not here.
@Injectable({ providedIn: 'root' })
export class PaymentService {
  constructor(private http: HttpClient) {}

  // The backend sends one page at a time: ?page=0&size=10.
  getPayments(page: number, size: number): Observable<Page<Payment>> {
    return this.http.get<Page<Payment>>('/api/payments', { params: { page, size } });
  }

  uploadBulk(file: File): Observable<BulkResult> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<BulkResult>('/api/payments/bulk', formData);
  }

  createPayment(request: PaymentRequest): Observable<Payment> {
    return this.http.post<Payment>('/api/payments', request);
  }

  getPayment(id: number): Observable<Payment> {
    return this.http.get<Payment>('/api/payments/' + id);
  }
}
