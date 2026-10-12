export type OrderStatus =
  | 'CREATED'
  | 'PROCESSING'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED'

export interface Order {
  id: number
  customerName: string
  deliveryAddress: string
  status: OrderStatus
  createdAt: string
}

export interface OrderPage {
  content: Order[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}
export interface CreateOrderRequest {
  customerName: string
  deliveryAddress: string
}