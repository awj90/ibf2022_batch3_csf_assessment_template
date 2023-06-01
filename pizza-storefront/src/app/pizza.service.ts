import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Order, PendingOrder, PlaceOrderResponse } from './models';
import { Observable, firstValueFrom } from 'rxjs';
import { Router } from '@angular/router';

@Injectable()
export class PizzaService {
  constructor(private http: HttpClient, private router: Router) {}

  // TODO: Task 3
  // You may add any parameters and return any type from placeOrder() method
  // Do not change the method name
  placeOrder(order: Order) {
    firstValueFrom(this.http.post<PlaceOrderResponse>('/api/order', order))
      .then((resp) => {
        this.router.navigate(['/orders', resp.email]);
      })
      .catch((err) => {
        alert(JSON.stringify(err));
      });
  }

  // TODO: Task 5
  // You may add any parameters and return any type from getOrders() method
  // Do not change the method name
  getOrders(email: string): Observable<PendingOrder[]> {
    return this.http.get<PendingOrder[]>(`/api/orders/${email}`);
  }

  // TODO: Task 7
  // You may add any parameters and return any type from delivered() method
  // Do not change the method name
  delivered(orderId: string): Observable<{}> {
    return this.http.delete<{}>(`/api/order/${orderId}`);
  }
}
