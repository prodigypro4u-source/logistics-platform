import { useState } from 'react'
import type { Shipment } from '../types/shipment'


interface ShipmentDetailsProps {
  orderId: number
}

export default function ShipmentDetails({
  orderId,
}: ShipmentDetailsProps) {
  const [shipment, setShipment] = useState<Shipment | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function loadShipment() {
    if (loading) return

    setLoading(true)
    setError(null)

    try {
      const response = await fetch(
        `/api/orders/${orderId}/shipment`,
      )

      if (!response.ok) {
        if (response.status === 404) {
          throw new Error('No shipment found for this order')
        }

        throw new Error(
          `Could not load shipment (HTTP ${response.status})`,
        )
      }

      const result: Shipment = await response.json()
      setShipment(result)
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'Could not load shipment',
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      {!shipment && (
        <button
          type="button"
          onClick={loadShipment}
          disabled={loading}
        >
          {loading ? 'Loading…' : 'View shipment'}
        </button>
      )}

      {shipment && (
        <div>
          <p>Carrier: {shipment.carrier}</p>
          <p>Tracking: {shipment.trackingNumber}</p>
        </div>
      )}

      {error && <p role="alert">{error}</p>}
    </div>
  )
}