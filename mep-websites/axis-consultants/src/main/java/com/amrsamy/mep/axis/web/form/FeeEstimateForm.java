package com.amrsamy.mep.axis.web.form;

import com.amrsamy.mep.axis.model.Complexity;
import com.amrsamy.mep.axis.model.Discipline;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class FeeEstimateForm {

    @NotNull(message = "Enter the gross built-up area")
    @Min(value = 500, message = "The estimator starts at 500 m²")
    @Max(value = 500_000, message = "Above 500,000 m² we price individually — send us a brief")
    private Integer area;

    @NotNull(message = "Choose the building complexity")
    private Complexity complexity;

    @NotEmpty(message = "Select at least one discipline")
    private List<Discipline> disciplines = new ArrayList<>();

    private boolean bim;

    private boolean supervision;

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }
    public Complexity getComplexity() { return complexity; }
    public void setComplexity(Complexity complexity) { this.complexity = complexity; }
    public List<Discipline> getDisciplines() { return disciplines; }
    public void setDisciplines(List<Discipline> disciplines) { this.disciplines = disciplines; }
    public boolean isBim() { return bim; }
    public void setBim(boolean bim) { this.bim = bim; }
    public boolean isSupervision() { return supervision; }
    public void setSupervision(boolean supervision) { this.supervision = supervision; }
}
