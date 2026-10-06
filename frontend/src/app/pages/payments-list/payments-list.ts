import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Payment } from '../../models/payment';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-payments-list',
  // DatePipe powers the "| date" in the template.
  imports: [RouterLink, DatePipe],
  templateUrl: './payments-list.html',
  styleUrl: './payments-list.scss',
})
export class PaymentsList implements OnInit {
  payments: Payment[] = [];
  loading = true;
  // Text shown when loading fails. Empty = nothing to show.
  errorMessage = '';

  constructor(
    private paymentService: PaymentService,
    private changeDetector: ChangeDetectorRef,
  ) {}

  // ngOnInit runs once, when the page opens. That is where we load the data.
  ngOnInit(): void {
    this.paymentService.getPayments().subscribe({
      next: (payments) => {
        this.payments = payments;
        this.loading = false;
        // This app has no zone.js, so Angular does not notice changes made inside
        // subscribe(). markForCheck() tells it: "please redraw this page".
        this.changeDetector.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        // 401 is handled by the interceptor (goes to login). Here we show other errors.
        this.errorMessage =
          typeof error.error === 'string' && error.error
            ? error.error
            : 'Could not load payments.';
        this.loading = false;
        this.changeDetector.markForCheck();
      },
    });
  }
}
