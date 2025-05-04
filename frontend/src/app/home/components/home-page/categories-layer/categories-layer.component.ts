import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

import { CommonModule } from '@angular/common';
import { HomeService } from '../../../services/home.service';
import { Category } from '../../../interfaces/Data.interface';
import { CategoryButtonComponent } from '../../../ui/category-button/category-button.component';

@Component({
  selector: 'app-categories-layer',
  imports: [CategoryButtonComponent, CommonModule],
  templateUrl: './categories-layer.component.html',
  styles: '',
})
export class CategoriesLayerComponent {
  private readonly _homeService = inject(HomeService);

  categories: Category[] = [];

  constructor() {
    this._homeService.getCategoryes().subscribe((res: Category[]) => {
      this.categories = res.sort(() => Math.random() - 0.5); // Shuffle categories
    });
  }

  animationClass(index: number) {
    index = Math.min(index, 5);
    return `animate__animated animate__fadeIn `;
  }
}
