package com.amrsamy.mep.meridian.web.form;

import com.amrsamy.mep.common.validation.EgyptianPhone;
import com.amrsamy.mep.meridian.model.Sector;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/** Mutable bean so Thymeleaf's th:field can bind and re-render it after validation errors. */
public class QuoteRequestForm {

    public enum Stage {
        DESIGN("Concept / design"),
        TENDER("Tender / BOQ pricing"),
        CONSTRUCTION("Under construction"),
        EXISTING("Existing building upgrade");

        private final String label;

        Stage(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @NotBlank(message = "Please tell us your name")
    @Size(max = 80)
    private String fullName;

    @Size(max = 120)
    private String company;

    @NotBlank(message = "We need an email to send the proposal")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Please add a phone number")
    @EgyptianPhone
    private String phone;

    @NotNull(message = "Choose the project sector")
    private Sector sector;

    @NotNull(message = "Choose the project stage")
    private Stage stage;

    @NotEmpty(message = "Select at least one discipline")
    private List<String> disciplines = new ArrayList<>();

    @NotBlank(message = "Where is the project?")
    @Size(max = 120)
    private String location;

    @Min(value = 100, message = "Minimum 100 m²")
    @Max(value = 2_000_000, message = "That looks too large — check the units")
    private Integer builtUpArea;

    @NotBlank(message = "A short description helps us price accurately")
    @Size(min = 20, max = 2000, message = "Please write between 20 and 2000 characters")
    private String message;

    @AssertTrue(message = "Please agree so we can contact you about this request")
    private boolean consent;

    /** Honeypot: hidden from people, filled in by naive bots. */
    private String website;

    public boolean isLikelySpam() {
        return website != null && !website.isBlank();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
    public Stage getStage() { return stage; }
    public void setStage(Stage stage) { this.stage = stage; }
    public List<String> getDisciplines() { return disciplines; }
    public void setDisciplines(List<String> disciplines) { this.disciplines = disciplines; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getBuiltUpArea() { return builtUpArea; }
    public void setBuiltUpArea(Integer builtUpArea) { this.builtUpArea = builtUpArea; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isConsent() { return consent; }
    public void setConsent(boolean consent) { this.consent = consent; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
