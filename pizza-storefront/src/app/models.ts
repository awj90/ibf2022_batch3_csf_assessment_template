export interface Order {
  name: string;
  email: string;
  size: number;
  base: string;
  sauce: string;
  toppings: string[];
  comments: string;
}

export interface PlaceOrderResponse {
  orderId: string;
  date: number;
  name: string;
  email: string;
  total: number;
}

export interface PendingOrder {
  orderId: string;
  total: number;
  date: number;
  delivered?: boolean;
}
