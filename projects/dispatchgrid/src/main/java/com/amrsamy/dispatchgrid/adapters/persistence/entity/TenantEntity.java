package com.amrsamy.dispatchgrid.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants")
public class TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false)
    private long dailyQuota = 100_000;

    @Column(nullable = false)
    private long usedToday = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(long dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public long getUsedToday() {
        return usedToday;
    }

    public void setUsedToday(long usedToday) {
        this.usedToday = usedToday;
    }
}
