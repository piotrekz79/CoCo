package net.geant.coco.agent.portal.controllers;

import java.util.List;

import net.geant.coco.agent.portal.dao.NetworkSwitch;
import net.geant.coco.agent.portal.service.NetworkSwitchesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VpnsController {
    private NetworkSwitchesService switchNodesService;

    /*
    @Autowired
    public void setSwitchNodeService(SwitchNodesService switchNodesService) {
        this.switchNodesService = switchNodesService;
    }
    */
    
    @RequestMapping("/vpnsfoo")
    public String showCoCoPortal(Model model) {

        //offersService.throwTestException();
        
        List<NetworkSwitch> switchNodes = switchNodesService.getNetworkSwitches();

        model.addAttribute("vpns", switchNodes);
        return "vpnsfoo";
    }
}
