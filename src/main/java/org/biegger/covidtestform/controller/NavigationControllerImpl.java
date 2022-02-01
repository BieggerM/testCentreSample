package org.biegger.covidtestform.controller;

import org.biegger.covidtestform.Data.Registration;
import org.biegger.covidtestform.service.RegistrationService.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class NavigationControllerImpl {

    private RegistrationService registrationService;

    @Autowired
    public NavigationControllerImpl(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @RequestMapping(value = "dsgvo")
    public String getDsgvoPage(){
        return "dsgvo";
    }

    @RequestMapping(value = "impressum")
    public String getImpressum(){
        return "impressum";
    }

    @RequestMapping("")
    public String returnPage(Model model){
        model.addAttribute("registration", new Registration());
        return "form";
    }

    @RequestMapping(value = "/admin")
    public String getAdminPage(Model model){
        model.addAttribute("registrations", registrationService.getAllRegistrations());
        return "administration";
    }

}
