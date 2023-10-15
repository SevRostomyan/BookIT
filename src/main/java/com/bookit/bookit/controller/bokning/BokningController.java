package com.bookit.bookit.controller.bokning;

import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.service.kund.KundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bokning")
public class BokningController {

    private final KundService kundService;

    public BokningController(KundService kundService) {
        this.kundService = kundService;
    }

    //OBS: Servicemetoden för denna finns i KundService classen då det avser kundens bokning
    @PostMapping("/bookCleaning")
    public ResponseEntity<String> bookCleaning(@RequestBody CleaningBookingRequest request) {
        return ResponseEntity.ok(kundService.bookCleaning(request));
    }

}
