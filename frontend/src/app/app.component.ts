import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { MenuComponent } from './shared/components/menu/menu.component';
import { SharedDataService } from './shared/services/shared-data.service';
import { AutocompleteSearchComponent } from './shared/components/autocomplete-search/autocomplete-search.component';

interface AutoCompleteCompleteEvent {
  originalEvent: Event;
  query: string;
}

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MenuComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'Tecno Shop';
}
