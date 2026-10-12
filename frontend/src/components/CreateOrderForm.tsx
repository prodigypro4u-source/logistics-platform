import { useState } from 'react'
import type { FormEvent } from 'react'
import type { CreateOrderRequest } from '../types/order'

interface CreateOrderFormProps {
  onCreated: () => void
}

interface ApiError {
  message: string
  fieldErrors: Record<string, string>
}

export default function CreateOrderForm({
  onCreated,
}: CreateOrderFormProps) {
  const [customerName, setCustomerName] = useState('')
  const [deliveryAddress, setDeliveryAddress] = useState('')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<
    Record<string, string>
  >({})
  const [success, setSuccess] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (saving) return

    setSaving(true)
    setError(null)
    setFieldErrors({})
    setSuccess(false)

    const request: CreateOrderRequest = {
      customerName,
      deliveryAddress,
    }

    try {
      const response = await fetch('/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request),
      })

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
            `Could not create order (HTTP ${response.status})`,
        )
      }

      setCustomerName('')
      setDeliveryAddress('')
      setSuccess(true)
      onCreated()
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'Could not create order',
      )
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="order-form-section">
      <h2>Create order</h2>

      <form onSubmit={handleSubmit}>
        <fieldset disabled={saving}>
          <legend>Customer and delivery details</legend>

          <label htmlFor="customerName">Customer name</label>
          <input
            id="customerName"
            value={customerName}
            onChange={(event) => {
              setCustomerName(event.target.value)
              setSuccess(false)
            }}
            required
            maxLength={120}
            aria-invalid={Boolean(fieldErrors.customerName)}
            aria-describedby="customerName-error"
          />
          <span id="customerName-error" className="field-error">
            {fieldErrors.customerName}
          </span>

          <label htmlFor="deliveryAddress">Delivery address</label>
          <textarea
            id="deliveryAddress"
            value={deliveryAddress}
            onChange={(event) => {
              setDeliveryAddress(event.target.value)
              setSuccess(false)
            }}
            required
            maxLength={500}
            rows={3}
            aria-invalid={Boolean(fieldErrors.deliveryAddress)}
            aria-describedby="deliveryAddress-error"
          />
          <span id="deliveryAddress-error" className="field-error">
            {fieldErrors.deliveryAddress}
          </span>

          <button type="submit">
            {saving ? 'Creating…' : 'Create order'}
          </button>
        </fieldset>
      </form>

      {error && <p role="alert">{error}</p>}
      {success && <p role="status">Order created successfully.</p>}
    </section>
  )
}