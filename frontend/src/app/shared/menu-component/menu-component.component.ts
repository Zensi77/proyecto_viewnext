import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ScrollService } from '../../services/scroll.service';

@Component({
  selector: 'app-menu-component',
  templateUrl: './menu-component.component.html',
  styleUrls: [],
  imports: [RouterLink],
})
export class MenuComponent {
  private _scrollService = inject(ScrollService);

  navigateToSection(sectionId: string): void {
    this._scrollService.scrollToSection(sectionId);
  }
}
