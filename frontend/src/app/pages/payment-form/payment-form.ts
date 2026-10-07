import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, signal } from '@angular/core';
import { FormField, SchemaPath, form, pattern, required, validate } from '@angular/forms/signals';
import { Router, RouterLink } from '@angular/router';
import { CURRENCIES } from '../../models/currency';
import { Payment, PaymentRequest } from '../../models/payment';
import { PaymentService } from '../../services/payment.service';

function notOnlySpaces(field: SchemaPath<string>, label: string): void {
  validate(field, ({ value }) =>
    value().length > 0 && value().trim() === ''
      ? { kind: 'blank', message: label + ' cannot be only spaces' }
      : undefined,
  );
}

@Component({
  selector: 'app-payment-form',
  imports: [FormField, RouterLink],
  templateUrl: './payment-form.html',
  styleUrl: './payment-form.scss',
})
export class PaymentForm {
  paymentModel = signal<PaymentRequest>({
    senderAccount: '',
    receiverAccount: '',
    amount: null,
    currency: '',
    notes: '',
    creditorName: '',
  });

  currencies = CURRENCIES;

  paymentForm = form(this.paymentModel, (path) => {
    required(path.senderAccount, { message: 'Sender account is required' });
    notOnlySpaces(path.senderAccount, 'Sender account');
    pattern(path.senderAccount, /^\s*\S*\s*$/, { message: 'Sender account cannot contain spaces' });

    required(path.receiverAccount, { message: 'Receiver account is required' });
    notOnlySpaces(path.receiverAccount, 'Receiver account');
    pattern(path.receiverAccount, /^\s*\S*\s*$/, { message: 'Receiver account cannot contain spaces' });

    required(path.amount, { message: 'Amount is required' });
    validate(path.amount, ({ value }) => {
      const amount = value();
      return amount !== null && amount <= 0
        ? { kind: 'positive', message: 'Amount must be greater than zero' }
        : undefined;
    });

    required(path.currency, { message: 'Please select a currency' });

    required(path.creditorName, { message: 'Creditor name is required' });
    notOnlySpaces(path.creditorName, 'Creditor name');

    notOnlySpaces(path.notes, 'Notes');
  });

  saving = false;
  errorMessage = '';

  constructor(
    private paymentService: PaymentService,
    private router: Router,
    private changeDetector: ChangeDetectorRef,
  ) {}

  save(event: Event): void {
    event.preventDefault();

    if (this.paymentForm().invalid()) {
      this.paymentForm().markAsTouched();
      return;
    }

    this.saving = true;
    this.errorMessage = '';

    const model = this.paymentModel();
    const request: PaymentRequest = {
      ...model,
      senderAccount: model.senderAccount.trim(),
      receiverAccount: model.receiverAccount.trim(),
      creditorName: model.creditorName.trim(),
      notes: model.notes.trim(),
    };

    this.paymentService.createPayment(request).subscribe({
      next: (_created: Payment) => {
        this.router.navigate(['/payments']);
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage =
          typeof error.error === 'string' && error.error
            ? error.error
            : error.status === 400
              ? 'Some fields are missing or invalid. Please check them.'
              : error.status === 403
                ? 'You are not allowed to create payments.'
                : 'Could not create the payment.';
        this.saving = false;
        this.changeDetector.markForCheck();
      },
    });
  }
}
