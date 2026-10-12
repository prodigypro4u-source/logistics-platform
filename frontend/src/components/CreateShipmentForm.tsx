import { useState } from 'react'
import type { FormEvent } from 'react'
import type { CreateShipmentRequest } from '../types/shipment'

interface CreateShipmentFormProps {
  orderId: number
  onCreated: () => void
}

interface ApiError {
  message: string
  fieldErrors: Record<string, string>
}

export default function CreateShipmentForm({
  orderId,
  onCreated,
}: CreateShipmentFormProps) {
  const [carrier, setCarrier] = useState('')
  const [trackingNumber, setTrackingNumber] = useState('')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<
    Record<string, string>
  >({})

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (saving) return

    setSaving(true)
    setError(null)
    setFieldErrors({})

    const request: CreateShipmentRequest = {
      carrier,
      trackingNumber,
    }

    try {
      const response = await fetch(
        `/api/orders/${orderId}/shipment`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(request),
        },
      )

      if (!response.ok) {
        let details: ApiError | null = null

        try {
          details = await response.json()
        } catch {
          // A failed response may not contain JSON.
        }

        setFieldErrors(details?.fieldErrors ?? {})

        throw new Error(
          details?.message ??
            `Could not create shipment (HTTP ${response.status})`,
        )
      }

      setCarrier('')
      setTrackingNumber('')
      onCreated()
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'Could not create shipment',
      )
    } finally {
      setSaving(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <fieldset disabled={saving}>
        <legend>Create shipment for order #{orderId}</legend>

        <label htmlFor={`carrier-${orderId}`}>Carrier</label>
        <input
          id={`carrier-${orderId}`}
          value={carrier}
          onChange={(event) => setCarrier(event.target.value)}
          required
          maxLength={100}
          aria-invalid={Boolean(fieldErrors.carrier)}
          aria-describedby={`carrier-error-${orderId}`}
        />
        <span
          id={`carrier-error-${orderId}`}
          className="field-error"
        >
          {fieldErrors.carrier}
        </span>

        <label htmlFor={`tracking-${orderId}`}>
          Tracking#
        </label>
        <input
          id={`tracking-${orderId}`}
          value={trackingNumber}
          onChange={(event) =>
            setTrackingNumber(event.target.value)
          }
          required
          maxLength={120}
          aria-invalid={Boolean(fieldErrors.trackingNumber)}
          aria-describedby={`tracking-error-${orderId}`}
        />
        <span
          id={`tracking-error-${orderId}`}
          className="field-error"
        >
          {fieldErrors.trackingNumber}
        </span>

        <button type="submit">
          {saving ? 'Creating…' : 'Create shipment'}
        </button>
      </fieldset>

      {error && <p role="alert">{error}</p>}
    </form>
  )
}