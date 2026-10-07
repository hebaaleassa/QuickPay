import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe, DecimalPipe } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Payment } from '../../models/payment';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-payment-details',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './payment-details.html',
  styleUrl: './payment-details.scss',
})
export class PaymentDetails implements OnInit {
  payment: Payment | null = null;
  loading = true;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private paymentService: PaymentService,
    private changeDetector: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.paymentService.getPayment(id).subscribe({
      next: (payment: Payment) => {
        this.payment = payment;
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage =
          error.status === 404
            ? 'This payment does not exist.'
            : typeof error.error === 'string' && error.error
              ? error.error
              : 'Could not load the payment.';
        this.loading = false;
        this.changeDetector.markForCheck();
      },
    });
  }
}
