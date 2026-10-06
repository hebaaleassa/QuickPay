import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ConnectionService {
  // navigator.onLine can say "online" while nothing really works, so for "Try again"
  // we send a tiny real request. If the server answers, the connection is back.
  // fetch is used on purpose (not HttpClient): the interceptor must not react to this check.
  isServerReachable(): Promise<boolean> {
    return fetch('/', { method: 'HEAD', cache: 'no-store' })
      .then((response) => response.ok)
      .catch(() => false);
  }
}
