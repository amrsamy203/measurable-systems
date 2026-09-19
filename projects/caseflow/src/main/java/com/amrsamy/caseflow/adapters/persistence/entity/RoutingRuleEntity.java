package com.amrsamy.caseflow.adapters.persistence.entity;

import com.amrsamy.caseflow.domain.model.Priority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "routing_rules")
public class RoutingRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private Priority matchPriority;

    @Column(length = 64)
    private String matchSkill;

    @Column(nullable = false, length = 80)
    private String targetQueue;

    @Column(nullable = false)
    private int priorityWeight = 0;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Priority getMatchPriority() {
        return matchPriority;
    }

    public void setMatchPriority(Priority matchPriority) {
        this.matchPriority = matchPriority;
    }

    public String getMatchSkill() {
        return matchSkill;
    }

    public void setMatchSkill(String matchSkill) {
        this.matchSkill = matchSkill;
    }

    public String getTargetQueue() {
        return targetQueue;
    }

    public void setTargetQueue(String targetQueue) {
        this.targetQueue = targetQueue;
    }

    public int getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(int priorityWeight) {
        this.priorityWeight = priorityWeight;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
