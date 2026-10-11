package com.amrsamy.mep.common.inquiry;

/**
 * Delivers inquiries to the business. The default implementation logs them; a production
 * deployment would register an email or CRM implementation as a bean to replace it.
 */
public interface InquiryNotifier {

    void send(Inquiry inquiry);
}
