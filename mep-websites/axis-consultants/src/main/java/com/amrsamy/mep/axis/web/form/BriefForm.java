package com.amrsamy.mep.axis.web.form;

import com.amrsamy.mep.axis.model.Discipline;
import com.amrsamy.mep.axis.model.Sector;
import com.amrsamy.mep.axis.model.Stage;
import com.amrsamy.mep.common.validation.EgyptianPhone;
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

/** A project brief: who you are, what you are building and where you need us to start. */
public class BriefForm {

    public enum ClientRole {
        DEVELOPER("Developer / owner"),
        ARCHITECT("Architect / lead consultant"),
        CONTRACTOR("Contractor (design & build)"),
        FACILITY("Facility / asset manager");

        private final String label;

        ClientRole(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    @NotBlank(message = "Please tell us your name")
    @Size(max = 80)
    private String fullName;

    @NotBlank(message = "Which organisation are you with?")
    @Size(max = 120)
    private String organisation;

    @NotNull(message = "Choose the option that describes you best")
    private ClientRole role;

    @NotBlank(message = "We need an email to reply to")
    @Email(message = "Enter a valid email address")
    private String email;

    @EgyptianPhone
    private String phone;

    @NotBlank(message = "Give the project a working name")
    @Size(max = 120)
    private String projectName;

    @NotNull(message = "Choose a sector")
    private Sector sector;

    @NotNull(message = "Where should we start?")
    private Stage startStage;

    @NotEmpty(message = "Select at least one discipline")
    private List<Discipline> disciplines = new ArrayList<>();

    @Min(value = 100, message = "Minimum 100 m²")
    @Max(value = 2_000_000, message = "That looks too large — check the units")
    private Integer area;

    @NotBlank(message = "A few lines about the project help us prepare")
    @Size(min = 30, max = 3000, message = "Please write between 30 and 3000 characters")
    private String description;

    @AssertTrue(message = "Please confirm so we can reply to your brief")
    private boolean consent;

    /** Honeypot: hidden from people, filled in by naive bots. */
    private String website;

    public boolean isLikelySpam() {
        return website != null && !website.isBlank();
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getOrganisation() { return organisation; }
    public void setOrganisation(String organisation) { this.organisation = organisation; }
    public ClientRole getRole() { return role; }
    public void setRole(ClientRole role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    public Sector getSector() { return sector; }
    public void setSector(Sector sector) { this.sector = sector; }
    public Stage getStartStage() { return startStage; }
    public void setStartStage(Stage startStage) { this.startStage = startStage; }
    public List<Discipline> getDisciplines() { return disciplines; }
    public void setDisciplines(List<Discipline> disciplines) { this.disciplines = disciplines; }
    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isConsent() { return consent; }
    public void setConsent(boolean consent) { this.consent = consent; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
