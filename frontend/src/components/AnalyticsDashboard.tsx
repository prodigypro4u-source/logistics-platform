import { useEffect, useState } from 'react'
import './AnalyticsDashboard.css'

interface StatusCount {
  status: string
  eventCount: number
}

interface AnalyticsResponse {
  jobId: string
  counts: StatusCount[]
}

interface AnalyticsDashboardProps {
  refreshToken?: number
}

export default function AnalyticsDashboard({
  refreshToken = 0,
}: AnalyticsDashboardProps) {
  const [data, setData] = useState<AnalyticsResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null)

  useEffect(() => {
    let active = true
    let pollTimer: number | undefined
    let requestController: AbortController | null = null

    async function loadAnalytics() {
      const controller = new AbortController()
      requestController = controller

      const timeout = window.setTimeout(() => controller.abort(), 10000)

      try {
        const response = await fetch(
          '/analytics-api/analytics/spark/events',
          { signal: controller.signal },
        )

        if (!response.ok) {
          throw new Error(
            `Could not load analytics (HTTP ${response.status})`,
          )
        }

        const result: AnalyticsResponse = await response.json()

        if (active) {
          setData(result)
          setError(null)
          setLastUpdated(new Date())
        }
      } catch (cause) {
        if (active) {
          setError(
            controller.signal.aborted
              ? 'Analytics request timed out.'
              : cause instanceof Error
                ? cause.message
                : 'Could not load analytics.',
          )
        }
      } finally {
        window.clearTimeout(timeout)

        if (active) {
          pollTimer = window.setTimeout(() => {
            void loadAnalytics()
          }, 5000)
        }
      }
    }

    void loadAnalytics()

    return () => {
      active = false
      window.clearTimeout(pollTimer)
      requestController?.abort()
    }
  }, [refreshToken])

  const totalEvents =
    data?.counts.reduce((total, item) => total + item.eventCount, 0) ?? 0

  return (
    <section className="analytics-panel" aria-labelledby="analytics-title">
      <div className="analytics-heading">
        <div>
          <h2 id="analytics-title">Shipment event analytics</h2>
          <p>
            Historical event counts, including test events.
            These are not current shipment totals.
          </p>
        </div>

        <span className="analytics-updated">
          {lastUpdated
            ? `Last fetched ${lastUpdated.toLocaleTimeString()}`
            : 'Waiting for analytics'}
        </span>
      </div>

      {!data && !error && (
        <p role="status">Loading analytics…</p>
      )}

      {error && (
        <p className="analytics-error" role="alert">
          {error}
          {data && ' Showing the last fetched results.'}
          {' Retrying automatically.'}
        </p>
      )}

      {data && (
        <div className="analytics-grid">
          <div className="analytics-card analytics-total">
            <span>Total recorded events</span>
            <strong>{totalEvents.toLocaleString()}</strong>
          </div>

          {data.counts.map((item) => (
            <div className="analytics-card" key={item.status}>
              <span>{item.status.replace(/_/g, ' ')} events</span>
              <strong>{item.eventCount.toLocaleString()}</strong>
            </div>
          ))}
        </div>
      )}

      {data && data.counts.length === 0 && (
        <p>No analytics results recorded yet.</p>
      )}
    </section>
  )
}