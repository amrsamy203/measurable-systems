package com.amrsamy.mep.thermiq.web.form;

import com.amrsamy.mep.common.validation.EgyptianPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ContactForm {

    public enum Topic {
        GENERAL("General question"),
        COMMERCIAL("Maintenance contract for a business"),
        FEEDBACK("Feedback on a visit");

        private final String label;

        Topic(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @NotBlank(message = "Please tell us your name")
    @Size(max = 80)
    private String fullName;

    @NotBlank(message = "Please add a phone number")
    @EgyptianPhone
    private String phone;

    @Email(message = "Enter a valid email address")
    @Size(max = 120)
    private String email;

    @NotNull(message = "Choose a topic")
    private Topic topic;

    @NotBlank(message = "Please write a short message")
    @Size(min = 10, max = 1500, message = "Please write between 10 and 1500 characters")
    private String message;

    /** Honeypot: hidden from people, filled in by naive bots. */
    private String website;

    public boolean isLikelySpam() {
        return website != null && !website.isBlank();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Topic getTopic() { return topic; }
    public void setTopic(Topic topic) { this.topic = topic; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
