// Shape of one payment returned by GET /api/payments (backend: PaymentResponse).
// Field names must match the backend JSON exactly.
export interface Payment {
  id: number;
  senderAccount: string;
  receiverAccount: string;
  amount: number;
  currency: string;
  status: string;
  // The backend sends a date as text, e.g. "2026-10-06T09:22:55Z".
  createdAt: string;
  notes: string;
}

export interface PaymentRequest {
  senderAccount: string;
  receiverAccount: string;
  amount: number | null;
  currency: string;
  notes: string;
  creditorName: string;
}
