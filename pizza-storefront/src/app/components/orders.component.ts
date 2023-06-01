import { Component, OnDestroy, OnInit } from '@angular/core';
import { PizzaService } from '../pizza.service';
import { ActivatedRoute } from '@angular/router';
import { PendingOrder } from '../models';
import { Subject, Subscription, firstValueFrom } from 'rxjs';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit, OnDestroy {
  pendingOrders: PendingOrder[] = [];
  pendingOrders$!: Subscription;
  email: string = this.activatedRoute.snapshot.params['email'];

  constructor(
    private pizzaService: PizzaService,
    private activatedRoute: ActivatedRoute,
    private title: Title
  ) {}

  ngOnInit(): void {
    this.title.setTitle(`Pending Pizza Orders for ${this.email}`);
    this.pendingOrders$ = this.pizzaService
      .getOrders(this.email)
      .subscribe((data) => {
        this.pendingOrders = data;
      });
  }

  ngOnDestroy(): void {
    this.pendingOrders$.unsubscribe();
  }

  onDelivered(index: number) {
    const orderIdToDelete = this.pendingOrders[index]['orderId'];
    firstValueFrom(this.pizzaService.delivered(orderIdToDelete))
      .then((data) => {
        alert(`Order Id ${orderIdToDelete} successfully delivered`);
        this.pendingOrders.splice(index, 1);
      })
      .catch((err) => {
        alert(JSON.stringify(err));
      });
  }
}
