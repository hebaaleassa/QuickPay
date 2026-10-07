import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Page } from '../../models/page';
import { Payment } from '../../models/payment';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-payments-list',
  // DatePipe powers the "| date" in the template.
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './payments-list.html',
  styleUrl: './payments-list.scss',
})
export class PaymentsList implements OnInit {
  // The payments of the current page.
  payments: Payment[] = [];
  pageSize = 10;
  // 0 = first page.
  page = 0;
  totalPages = 0;
  loading = true;
  // Text shown when loading fails. Empty = nothing to show.
  errorMessage = '';

  constructor(
    private paymentService: PaymentService,
    private router: Router,
    private changeDetector: ChangeDetectorRef,
  ) {}

  // ngOnInit runs once, when the page opens. That is where we load the data.
  ngOnInit(): void {
    this.loadPage();
  }

  loadPage(): void {
    this.loading = true;
    this.paymentService.getPayments(this.page, this.pageSize).subscribe({
      next: (result: Page<Payment>) => {
        this.payments = result.content;
        this.totalPages = result.totalPages;
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

  previous(): void {
    this.page--;
    this.loadPage();
  }

  next(): void {
    this.page++;
    this.loadPage();
  }

  openDetails(payment: Payment): void {
    this.router.navigate(['/payments', payment.id]);
  }
}
