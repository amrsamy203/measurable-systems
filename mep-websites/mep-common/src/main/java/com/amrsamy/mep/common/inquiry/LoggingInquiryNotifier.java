package com.amrsamy.mep.common.inquiry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingInquiryNotifier implements InquiryNotifier {

    private static final Logger log = LoggerFactory.getLogger(LoggingInquiryNotifier.class);

    @Override
    public void send(Inquiry inquiry) {
        log.info("New {} inquiry {} from {} ({} fields)",
                inquiry.type(), inquiry.reference(), inquiry.name(), inquiry.details().size());
    }
}
