package com.amrsamy.mep.thermiq.web.form;

import com.amrsamy.mep.common.validation.EgyptianPhone;
import com.amrsamy.mep.thermiq.model.ServiceArea;
import com.amrsamy.mep.thermiq.model.ServiceType;
import com.amrsamy.mep.thermiq.model.TimeSlot;
import com.amrsamy.mep.thermiq.service.BookingRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Shape checks only; date and slot availability are business rules enforced in BookingService. */
public class BookingForm {

    @NotNull(message = "What do you need us for?")
    private ServiceType service;

    @NotNull(message = "How many AC units?")
    @Min(value = 1, message = "At least one unit")
    @Max(value = 10, message = "For more than 10 units, call us for a site survey")
    private Integer units = 1;

    @NotNull(message = "Choose your area")
    private ServiceArea area;

    @NotBlank(message = "We need the address for the technician")
    @Size(max = 200)
    private String address;

    @NotNull(message = "Pick a date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    @NotNull(message = "Pick a time slot")
    private TimeSlot slot;

    @NotBlank(message = "Please tell us your name")
    @Size(max = 80)
    private String fullName;

    @NotBlank(message = "A mobile number lets the technician call when on the way")
    @EgyptianPhone(mobileOnly = true, message = "Enter an Egyptian mobile number, e.g. 010 1234 5678")
    private String phone;

    @Email(message = "Enter a valid email address")
    @Size(max = 120)
    private String email;

    @Size(max = 500, message = "Please keep notes under 500 characters")
    private String notes;

    /** Honeypot: hidden from people, filled in by naive bots. */
    private String website;

    public boolean isLikelySpam() {
        return website != null && !website.isBlank();
    }

    public BookingRequest toRequest() {
        return new BookingRequest(service, units, area, address, date, slot, fullName, phone, email, notes);
    }

    public ServiceType getService() { return service; }
    public void setService(ServiceType service) { this.service = service; }
    public Integer getUnits() { return units; }
    public void setUnits(Integer units) { this.units = units; }
    public ServiceArea getArea() { return area; }
    public void setArea(ServiceArea area) { this.area = area; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public TimeSlot getSlot() { return slot; }
    public void setSlot(TimeSlot slot) { this.slot = slot; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
