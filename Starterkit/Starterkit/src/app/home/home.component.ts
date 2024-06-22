import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  currentSlide = 0;
  slides = [
    { image: '../assets/images/carousel_1.png', alt: 'Image 1' },
    { image: '../assets/images/carousel_2.png', alt: 'Image 2' },
    { image: '../assets/images/carousel_1.png', alt: 'Image 3' }
  ];

  partenaires = [
    { name: 'Natixis-Paris EUR-GBP', image: '../assets/images/part_1.jpg', description: 'Description du Partenaire 1' },
    { name: 'Intesa Sanpaolo SPA EUR', image: '../assets/images/part_2.jpg', description: 'Description du Partenaire 2' },
    { name: 'Commerzbank AG- Frankfurt EUR-DKK-JPY', image: '../assets/images/part_3.png', description: 'Description du Partenaire 3' }
  ];

  services = [
    { name: 'Compte Chèque', image: '../assets/images/service_1.jpg', description: 'Vous permet d\'effectuer vos opérations bancaires courantes ' },
    { name: 'Compte courant commercial', image: '../assets/images/service_2.jpg', description: 'Vous permet de réaliser vos transactions régulières' },
    { name: 'Compte courant agricole', image: '../assets/images/service_3.jpg', description: 'Vous exercez une activité agricole, ce compte vous permet d\'accéder aux avantages liés à votre profession...' }
  ];

  prevSlide() {
    this.currentSlide = (this.currentSlide === 0) ? this.slides.length - 1 : this.currentSlide - 1;
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide === this.slides.length - 1) ? 0 : this.currentSlide + 1;
  }
}
