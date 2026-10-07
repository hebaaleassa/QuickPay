import { ChangeDetectorRef, Component, HostListener } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { OfflineModal } from './components/offline-modal/offline-modal';
import { AuthService } from './services/auth.service';
import { ConnectionService } from './services/connection.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, OfflineModal],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  // The browser tells us at start-up whether it is online.
  isOnline = navigator.onLine;
  checking = false;

  constructor(
    public authService: AuthService,
    private router: Router,
    private connectionService: ConnectionService,
    private changeDetector: ChangeDetectorRef,
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // @HostListener listens to browser events. The browser fires "offline" / "online"
  // on the window when the connection is lost / comes back.
  @HostListener('window:offline')
  onOffline(): void {
    this.isOnline = false;
  }

  @HostListener('window:online')
  onOnline(): void {
    this.isOnline = true;
  }

  // "Try again" button: really check, instead of trusting the browser flag.
  tryAgain(): void {
    this.checking = true;
    this.connectionService.isServerReachable().then((reachable) => {
      this.isOnline = reachable;
      this.checking = false;
      // No zone.js, so tell Angular to redraw after this async result.
      this.changeDetector.markForCheck();
    });
  }
}
