import { useState } from 'react'
import type { Order, OrderStatus } from '../types/order'

interface OrderStatusActionsProps {
  order: Order
  onUpdated: () => void
}

const allowedTransitions: Record<OrderStatus, OrderStatus[]> = {
  CREATED: ['PROCESSING', 'CANCELLED'],
  PROCESSING: ['CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
}

export default function OrderStatusActions({
  order,
  onUpdated,
}: OrderStatusActionsProps) {
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function updateStatus(status: OrderStatus) {
    if (saving) return

    setSaving(true)
    setError(null)

    try {
      const response = await fetch(`/api/orders/${order.id}/status`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status }),
      })

      if (!response.ok) {
        let message = `Could not update order (HTTP ${response.status})`

        try {
          const details = await response.json()
          if (typeof details?.message === 'string') {
            message = details.message
          }
        } catch {
          // Keep the fallback message if the response isn't JSON.
        }

        throw new Error(message)
      }

      onUpdated()
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'Could not update order',
      )
    } finally {
      setSaving(false)
    }
  }

  const nextStatuses = allowedTransitions[order.status]

  return (
    <div>
      <div className="status-actions">
        {nextStatuses.length === 0 ? (
          <span>No further changes</span>
        ) : (
          nextStatuses.map((status) => (
            <button
              key={status}
              type="button"
              disabled={saving}
              onClick={() => void updateStatus(status)}
            >
              {status}
            </button>
          ))
        )}
      </div>

      {saving && <p role="status">Updating…</p>}
      {error && <p role="alert">{error}</p>}
    </div>
  )
}