CREATE TABLE shipment_event_outbox (
    event_id VARCHAR(255) PRIMARY KEY,
    shipment_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    location TEXT NOT NULL,
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_shipment_outbox_pending
    ON shipment_event_outbox (event_time, event_id)
    WHERE published = FALSE;