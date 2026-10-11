package com.amrsamy.mep.thermiq.web;

import com.amrsamy.mep.thermiq.model.ServiceArea;
import com.amrsamy.mep.thermiq.model.ServiceType;
import com.amrsamy.mep.thermiq.model.TimeSlot;
import com.amrsamy.mep.thermiq.service.BookingRejectedException;
import com.amrsamy.mep.thermiq.service.BookingService;
import com.amrsamy.mep.thermiq.service.DayAvailability;
import com.amrsamy.mep.thermiq.web.form.BookingForm;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/book")
public class BookingController {

    private static final int DAYS_SHOWN = 6;

    private final BookingService bookings;

    public BookingController(BookingService bookings) {
        this.bookings = bookings;
    }

    @ModelAttribute("services")
    public ServiceType[] services() {
        return ServiceType.values();
    }

    @ModelAttribute("areas")
    public ServiceArea[] areas() {
        return ServiceArea.values();
    }

    @ModelAttribute("slots")
    public TimeSlot[] slots() {
        return TimeSlot.values();
    }

    @ModelAttribute("availability")
    public List<DayAvailability> availability() {
        return bookings.availability(DAYS_SHOWN);
    }

    /** Slot buttons and CTAs elsewhere link here with {@code ?service=&date=&slot=&units=}. */
    @GetMapping
    public String form(@RequestParam(required = false) String service,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(required = false) TimeSlot slot,
                       @RequestParam(required = false) Integer units,
                       Model model) {
        BookingForm form = new BookingForm();
        ServiceType.fromSlug(service).ifPresent(form::setService);
        form.setDate(date);
        form.setSlot(slot);
        if (units != null && units >= 1 && units <= 10) {
            form.setUnits(units);
        }
        model.addAttribute("booking", form);
        return "book";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("booking") BookingForm form, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "book";
        }
        if (form.isLikelySpam()) {
            return "redirect:/book/confirmed";
        }
        try {
            redirect.addFlashAttribute("confirmation", BookingConfirmation.of(bookings.book(form.toRequest())));
        } catch (BookingRejectedException e) {
            result.rejectValue(e.getField(), "booking", e.getMessage());
            return "book";
        }
        return "redirect:/book/confirmed";
    }

    @GetMapping("/confirmed")
    public String confirmed(Model model) {
        return model.containsAttribute("confirmation") ? "book-confirmed" : "redirect:/book";
    }
}
