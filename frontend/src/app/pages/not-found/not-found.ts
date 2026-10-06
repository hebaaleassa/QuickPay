import { Location } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-not-found',
  templateUrl: './not-found.html',
  styleUrl: './not-found.scss',
})
export class NotFound {
  // Location is Angular's wrapper around the browser history.
  constructor(private location: Location) {}

  // Same as pressing the browser's Back button.
  goBack(): void {
    this.location.back();
  }
}
