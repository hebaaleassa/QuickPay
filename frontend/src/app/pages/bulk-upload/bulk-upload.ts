import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BulkResult } from '../../models/bulk-result';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'app-bulk-upload',
  imports: [RouterLink, DatePipe, FormsModule],
  templateUrl: './bulk-upload.html',
  styleUrl: './bulk-upload.scss',
})
export class BulkUpload {
  file: File | null = null;
  uploading = false;
  result: BulkResult | null = null;
  errorMessage = '';

  constructor(
    private paymentService: PaymentService,
    private changeDetector: ChangeDetectorRef,
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.file = input.files && input.files.length > 0 ? input.files[0] : null;
    this.result = null;
    this.errorMessage = '';
  }

  upload(): void {
    if (!this.file) {
      return;
    }
    this.uploading = true;
    this.result = null;
    this.errorMessage = '';

    this.paymentService.uploadBulk(this.file).subscribe({
      next: (result: BulkResult) => {
        this.result = result;
        this.uploading = false;
        this.changeDetector.markForCheck();
      },
      error: (error: HttpErrorResponse) => {
        this.errorMessage =
          typeof error.error === 'string' && error.error
            ? error.error
            : error.status === 403
              ? 'You are not allowed to upload payments.'
              : 'Upload failed.';
        this.uploading = false;
        this.changeDetector.markForCheck();
      },
    });
  }

  get rowErrors(): { row: string; messages: string[] }[] {
    if (!this.result) {
      return [];
    }
    return Object.entries(this.result.rowErrors ?? {}).map(([row, messages]) => ({ row, messages }));
  }

  get outcome(): 'success' | 'partial' | 'failed' {
    if (!this.result || this.result.success === 0) {
      return 'failed';
    }
    return this.result.failure === 0 ? 'success' : 'partial';
  }

  get fileSize(): string {
    return this.file ? (this.file.size / 1024).toFixed(1) + ' KB' : '';
  }

  get wasSaved(): boolean {
    return !!this.result && this.result.payments.some((payment) => payment.id != null);
  }
}
