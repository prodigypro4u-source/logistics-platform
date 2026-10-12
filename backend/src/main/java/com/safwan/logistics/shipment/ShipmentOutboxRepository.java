package com.safwan.logistics.shipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShipmentOutboxRepository
        extends JpaRepository<ShipmentOutboxEvent, String> {

    List<ShipmentOutboxEvent>
    findTop50ByPublishedFalseOrderByEventTimeAscEventIdAsc();

    @Modifying
    @Transactional
    @Query("""
            UPDATE ShipmentOutboxEvent event
            SET event.published = true
            WHERE event.eventId = :eventId
              AND event.published = false
            """)
    int markPublished(@Param("eventId") String eventId);
}