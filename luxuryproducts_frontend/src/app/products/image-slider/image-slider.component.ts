import {Component, Input} from '@angular/core';
import {environment} from '../../../environments/environment';
import {ImageUrl} from '../../models/imageUrl';

@Component({
  selector: 'app-image-slider',
  imports: [],
  templateUrl:'./image-slider.component.html',
  styleUrl: './image-slider.component.scss'
})
export class ImageSliderComponent {
  protected readonly environment = environment;
  @Input() images!: ImageUrl[];
  currentIndex = 0;

  nextImage() {
    this.currentIndex = (this.currentIndex + 1) % this.images.length;
  }

  prevImage() {
    this.currentIndex = (this.currentIndex - 1 + this.images.length) % this.images.length;
  }
}
