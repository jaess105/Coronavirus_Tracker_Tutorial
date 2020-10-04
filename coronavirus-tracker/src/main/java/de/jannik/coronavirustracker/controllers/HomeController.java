package de.jannik.coronavirustracker.controllers;

import de.jannik.coronavirustracker.models.LocationStats;
import de.jannik.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model){
        final List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        Integer totalReportedCases=allStats.stream()
                .mapToInt(stat -> stat.getLatestTotalCases())
                .sum();
        Integer totalNewCases=allStats.stream()
                .mapToInt(stat -> stat.getDivFromPreviousDate())
                .sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        return "home";
    }
}
