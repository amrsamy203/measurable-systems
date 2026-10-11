package com.amrsamy.mep.thermiq.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "booking", indexes = @Index(name = "ix_booking_date_slot", columnList = "visit_date, slot"))
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Assigned from the id right after the first insert, e.g. TQ-2026-000042. */
    @Column(unique = true, length = 20)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceType service;

    @Column(nullable = false)
    private int units;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceArea area;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(name = "visit_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private TimeSlot slot;

    @Column(nullable = false, length = 80)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private Instant createdAt;

    protected Booking() {}

    public Booking(ServiceType service, int units, ServiceArea area, String address, LocalDate date, TimeSlot slot,
                   String customerName, String phone, String email, String notes, Instant createdAt) {
        this.service = service;
        this.units = units;
        this.area = area;
        this.address = address;
        this.date = date;
        this.slot = slot;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public void assignReference(String reference) {
        if (this.reference != null) {
            throw new IllegalStateException("Reference already assigned");
        }
        this.reference = reference;
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public ServiceType getService() { return service; }
    public int getUnits() { return units; }
    public ServiceArea getArea() { return area; }
    public String getAddress() { return address; }
    public LocalDate getDate() { return date; }
    public TimeSlot getSlot() { return slot; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
}
