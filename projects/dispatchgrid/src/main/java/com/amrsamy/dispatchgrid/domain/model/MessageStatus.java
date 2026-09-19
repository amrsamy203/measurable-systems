package com.amrsamy.dispatchgrid.domain.model;

public enum MessageStatus {
    PENDING,
    QUEUED,
    SENDING,
    SENT,
    DELIVERED,
    FAILED,
    FAILED_DLQ
}
