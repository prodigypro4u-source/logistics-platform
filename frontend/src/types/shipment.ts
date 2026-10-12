export interface Shipment {
  id: number
  orderId: number
  carrier: string
  trackingNumber: string
  createdAt: string
}
export interface CreateShipmentRequest {
  carrier: string
  trackingNumber: string
}