import { Payment } from './payment';

export interface BulkResult {
  total: number;
  success: number;
  failure: number;
  rowErrors: { [rowNumber: string]: string[] };
  payments: Payment[];
}
