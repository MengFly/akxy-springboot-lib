package com.akxy.autocheck.controller;

import com.akxy.autocheck.autocheck.AutoCheckAspectSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true")
@RequestMapping("/autoCheck")
public class AliveController {

    @Autowired
    AutoCheckAspectSupport autoCheckAspectSupport;

    @GetMapping("/alive")
    @ResponseBody
    public boolean isAlive() {
        return true;
    }

    @GetMapping("health")
    @ResponseBody
    public List<AutoCheckAspectSupport.HealthItem> health() {
        return autoCheckAspectSupport.health();
    }

}
