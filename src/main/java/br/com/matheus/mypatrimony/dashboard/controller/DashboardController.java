package br.com.matheus.mypatrimony.dashboard.controller;

import br.com.matheus.mypatrimony.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService service;

    @GetMapping("/info")
    public ResponseEntity getInfo(){
        return service.getDashboardInfo();
    }

}



