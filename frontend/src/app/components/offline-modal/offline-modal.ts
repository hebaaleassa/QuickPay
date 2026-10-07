import { Component, EventEmitter, Input, Output } from '@angular/core';

// Presentational component: it knows nothing about the network.
// The parent tells it what to show (@Input) and hears about the click (@Output).
@Component({
  selector: 'app-offline-modal',
  templateUrl: './offline-modal.html',
  styleUrl: './offline-modal.scss',
})
export class OfflineModal {
  // true while "Try again" is checking the connection.
  @Input() checking = false;

  // Fires when the user clicks "Try again".
  @Output() retry = new EventEmitter<void>();
}
