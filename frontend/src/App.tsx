import { useEffect, useState } from 'react'
import type { Order, OrderPage , OrderStatus} from './types/order'
import './App.css'
import CreateOrderForm from './components/CreateOrderForm'
import OrderStatusActions from './components/OrderStatusActions'
import ShipmentDetails from './components/ShipmentDetails';
import CreateShipmentForm from './components/CreateShipmentForm'
import AnalyticsDashboard from './components/AnalyticsDashboard'



const PAGE_SIZE = 5

interface OrderTableProps {
  orders: Order[]
  onUpdated: () => void
}

function OrderTable({ orders, onUpdated }: OrderTableProps)  {
  return (
    <div className="table-wrapper">
      <table>
        <caption>Orders on the current page</caption>
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">Customer</th>
            <th scope="col">Delivery address</th>
            <th scope="col">Status</th>
            <th scope="col">Created</th>
            <th scope="col">Actions</th>
            <th scope="col">Shipment</th>

          </tr>

        </thead>
        
         

        <tbody>
          {orders.map((order) => (
            <tr key={order.id}>
              <td>{order.id}</td>
              <td>{order.customerName}</td>
              <td>{order.deliveryAddress}</td>
              <td>{order.status}</td>
              
              <td>
                {new Date(order.createdAt).toLocaleString()}
              </td>
              <td>
  <OrderStatusActions
    order={order}
    onUpdated={onUpdated}
  />
</td>
<td>
  {order.status === 'PROCESSING' ? (
    <CreateShipmentForm
      orderId={order.id}
      onCreated={onUpdated}
    />
  ) : order.status === 'SHIPPED' ||
    order.status === 'DELIVERED' ? (
    <ShipmentDetails orderId={order.id} />
  ) : (
    <span>No shipment</span>
  )}
</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default function App() {
  const [page, setPage] = useState(0)
  const [data, setData] = useState<OrderPage | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [refreshCount, setRefreshCount] = useState(0)
const [statusFilter, setStatusFilter] = useState<OrderStatus | ''>('')
  useEffect(() => {
    const controller = new AbortController()
    let active = true

    async function loadOrders() {
      setLoading(true)
      setError(null)

      try {
       const params = new URLSearchParams({
  page: String(page),
  size: String(PAGE_SIZE),
})

if (statusFilter) {
  params.set('status', statusFilter)
}

const response = await fetch(`/api/orders?${params.toString()}`, {
  signal: controller.signal,
})

        if (!response.ok) {
          throw new Error(
            `Could not load orders (HTTP ${response.status})`,
          )
        }

        const result: OrderPage = await response.json()

        if (active) {
          setData(result)
        }
      } catch (cause) {
        if (active) {
          setError(
            cause instanceof Error
              ? cause.message
              : 'Could not load orders',
          )
        }
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    void loadOrders()

    return () => {
      active = false
      controller.abort()
    }
  }, [page, refreshCount, statusFilter])

  const currentData = data?.page === page ? data : null

  return (
    <main className="dashboard">
      <header className="dashboard-header">
        <div>
          <h1>Logistics orders</h1>
          <p>Track customer orders and deliveries.</p>
        </div>

        <button
          disabled={loading}
          onClick={() => setRefreshCount((count) => count + 1)}
        >
          Refresh
        </button>
      </header>
      <AnalyticsDashboard refreshToken={refreshCount} />
      <div>
  <label htmlFor="status-filter">Filter by status: </label>

  <select
    id="status-filter"
    value={statusFilter}
    onChange={(event) => {
      setStatusFilter(event.target.value as OrderStatus | '')
      setPage(0)
      setData(null)
    }}
  >
    <option value="">All statuses</option>
    <option value="CREATED">Created</option>
    <option value="PROCESSING">Processing</option>
    <option value="SHIPPED">Shipped</option>
    <option value="DELIVERED">Delivered</option>
    <option value="CANCELLED">Cancelled</option>
  </select>
</div>
       <CreateOrderForm
        onCreated={() => {
          setPage(0)
          setRefreshCount((count) => count + 1)
        }}
      />

      {loading && <p role="status">Loading orders…</p>}


   

      {error && <p role="alert">{error}</p>}

      {!loading && !error && currentData && (
        <>
          <p>Total orders: {currentData.totalElements}</p>

          {currentData.content.length === 0 ? (
            <p>No orders on this page.</p>
          ) : (
            <OrderTable
  orders={currentData.content}
  onUpdated={() => {
    setRefreshCount((count) => count + 1)
  }}
/>
          )}

          <nav className="pagination" aria-label="Order pages">
            <button
              disabled={page === 0}
              onClick={() => setPage((current) => current - 1)}
            >
              Previous
            </button>

            <span>
              {currentData.totalPages === 0
                ? 'No pages'
                : `Page ${page + 1} · ${currentData.totalPages} total`}
            </span>

            <button
              disabled={page + 1 >= currentData.totalPages}
              onClick={() => setPage((current) => current + 1)}
            >
              Next
            </button>
          </nav>
        </>
      )}
    </main>
  )
}