package org.biegger.covidtestform.controller;

import org.biegger.covidtestform.service.FallbackDataService.FallbackDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DataFallbackController {

    private FallbackDataService fallbackDataService;

    @Autowired
    public DataFallbackController(FallbackDataService fallbackDataService) {
        this.fallbackDataService = fallbackDataService;
    }


    @RequestMapping(value = "/admin/fallback")
    public String getFallbackPage(Model model){
        model.addAttribute("failedemailtransactions", fallbackDataService.getAllFallbackRegistrations());
        return "failedEmail";
    }

    @RequestMapping(value = "/admin/fallback/deleteAll")
    public String deleteAll(Model model){
        fallbackDataService.deleteAll();
        model.addAttribute("failedemailtransactions", fallbackDataService.getAllFallbackRegistrations());
        return "failedEmail";
    }
}
