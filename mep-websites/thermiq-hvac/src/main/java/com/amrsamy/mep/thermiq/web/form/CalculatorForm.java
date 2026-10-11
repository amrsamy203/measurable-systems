package com.amrsamy.mep.thermiq.web.form;

import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator.RoomType;
import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator.SunExposure;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CalculatorForm {

    @NotNull(message = "Enter the room area")
    @Min(value = 5, message = "Rooms start at 5 m²")
    @Max(value = 200, message = "Above 200 m² we size on site — book a free survey")
    private Integer area;

    @NotNull(message = "Choose the room type")
    private RoomType room = RoomType.LIVING;

    @NotNull(message = "Choose the sun exposure")
    private SunExposure sun = SunExposure.NORMAL;

    @NotNull(message = "How many people usually use the room?")
    @Min(value = 1, message = "At least one person")
    @Max(value = 30, message = "Up to 30 people")
    private Integer occupants = 2;

    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }
    public RoomType getRoom() { return room; }
    public void setRoom(RoomType room) { this.room = room; }
    public SunExposure getSun() { return sun; }
    public void setSun(SunExposure sun) { this.sun = sun; }
    public Integer getOccupants() { return occupants; }
    public void setOccupants(Integer occupants) { this.occupants = occupants; }
}
