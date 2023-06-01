import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Order } from '../models';
import { PizzaService } from '../pizza.service';
import { BeforeLeavingComponent } from '../utils';

const SIZES: string[] = [
  'Personal - 6 inches',
  'Regular - 9 inches',
  'Large - 12 inches',
  'Extra Large - 15 inches',
];

const PIZZA_TOPPINGS: string[] = [
  'chicken',
  'seafood',
  'beef',
  'vegetables',
  'cheese',
  'arugula',
  'pineapple',
];

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
})
export class MainComponent implements OnInit, BeforeLeavingComponent {
  pizzaSize = SIZES[0];
  form!: FormGroup;
  toppingsArr: String[] = [];

  constructor(private fb: FormBuilder, private pizzaService: PizzaService) {}

  ngOnInit(): void {
    this.form = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: this.fb.control<string>('', [Validators.required]),
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.email,
      ]),
      size: this.fb.control<number>(0, [
        Validators.required,
        Validators.min(0),
        Validators.max(3),
      ]),
      base: this.fb.control<string>('', [Validators.required]),
      sauce: this.fb.control<string>('', [Validators.required]),
      comments: this.fb.control<string>(''),
    });
  }

  updateSize(size: string) {
    this.pizzaSize = SIZES[parseInt(size)];
  }

  onCheckOrUncheck($event: any): void {
    const toggle = $event.target.value;
    if (this.toppingsArr.includes(toggle)) {
      this.toppingsArr = this.toppingsArr.filter(
        (topping) => topping !== toggle
      );
    } else {
      this.toppingsArr.push(toggle);
    }
    console.info(this.toppingsArr);
  }

  hasNoToppings(): boolean {
    return this.toppingsArr.length < 1;
  }

  process() {
    const order: Order = {
      ...this.form.value,
      toppings: this.toppingsArr,
    };
    console.info(order);
    this.pizzaService.placeOrder(order);
    this.form.reset();
  }

  formNotSaved(): boolean {
    return this.form.dirty;
  }

  confirmMessage(): string {
    return 'You have not completed ordering.\n Are you sure you want to leave?';
  }
}
