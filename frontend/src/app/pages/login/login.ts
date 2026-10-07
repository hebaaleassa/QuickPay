import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  // FormsModule is what makes [(ngModel)] work in the template.
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  // These are linked to the inputs in login.html with [(ngModel)].
  username = '';
  password = '';

  // Text shown under the form when login fails. Empty = nothing to show.
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private changeDetector: ChangeDetectorRef,
  ) {}

  // Runs when the user submits the form.
  login(): void {
    this.errorMessage = '';

    this.authService.login(this.username, this.password).subscribe({
      // Success: the token is already saved by AuthService, so we only move on.
      next: () => {
        this.router.navigate(['/payments']);
      },
      // Failure (e.g. 401): show the message instead of moving on.
      error: (error: HttpErrorResponse) => {
        this.errorMessage = this.authService.getErrorMessage(error);
        // No zone.js in this app, so tell Angular to redraw the page with the new message.
        this.changeDetector.markForCheck();
      },
    });
  }
}
