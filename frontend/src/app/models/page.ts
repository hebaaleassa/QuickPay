// One page of a list, as returned by the backend list endpoints (backend: PageResponse).
// "page" starts at 0.
export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
