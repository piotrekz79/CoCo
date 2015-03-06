package net.geant.coco.agent.portal.controllers;

import java.util.List;

import javax.validation.Valid;

import net.geant.coco.agent.portal.dao.NetworkLink;
import net.geant.coco.agent.portal.dao.NetworkSite;
import net.geant.coco.agent.portal.dao.Offer;
import net.geant.coco.agent.portal.dao.NetworkSwitch;
import net.geant.coco.agent.portal.dao.Vpn;
import net.geant.coco.agent.portal.service.NetworkLinksService;
import net.geant.coco.agent.portal.service.NetworkSitesService;
import net.geant.coco.agent.portal.service.OffersService;
import net.geant.coco.agent.portal.service.NetworkSwitchesService;
import net.geant.coco.agent.portal.service.VpnsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PortalController {

    private OffersService offersService;
    private NetworkSwitchesService networkSwitchesService;
    private NetworkLinksService networkLinksService;
    private NetworkSitesService networkSitesService;
    private VpnsService vpnsService;

    @Autowired
    public void setOffersService(OffersService offersService) {
        this.offersService = offersService;
    }
    
    @Autowired
    public void setNetworkSwitchService(NetworkSwitchesService networkSwitchesService) {
        this.networkSwitchesService = networkSwitchesService;
    }

    @Autowired
    public void setNetworkLinkService(NetworkLinksService networkLinksService) {
        this.networkLinksService = networkLinksService;
    }
    
    @Autowired
    public void setNetworkSitesService(NetworkSitesService networkSitesService) {
        this.networkSitesService = networkSitesService;
    }
    
    @Autowired
    public void setVpnsService(VpnsService vpnsService) {
        this.vpnsService = vpnsService;
    }
    
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String showTest(Model model, @RequestParam("id") String id) {
        System.out.println("Id is: " + id);
        return "portal";
    }

    /*
    @ExceptionHandler(DataAccessException.class)
    public String handleDatabaseException(DataAccessException ex) {
        return "error";
    }
    */
    
    @RequestMapping("/offers")
    public String showPortal(Model model) {

        //offersService.throwTestException();
        
        List<Offer> offers = offersService.getCurrent();

        model.addAttribute("offers", offers);
        return "offers";
    }
    
    @RequestMapping("/")
    public String showCoCoPortal(Model model) {

        //offersService.throwTestException();
        
        List<NetworkSwitch> networkSwitches = networkSwitchesService.getNetworkSwitches();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();
        List<NetworkSite> networkSites = networkSitesService.getNetworkSites();
        List<Vpn> vpns = vpnsService.getVpns();

        //model.addAttribute("switchNodes", switchNodes);
        //return "switchNodes";
        
        model.addAttribute("switches", networkSwitches);
        model.addAttribute("links", networkLinks);
        model.addAttribute("sites", networkSites);
        model.addAttribute("vpns", vpns);
        return "portal";
    }
    
    @RequestMapping("/addsite")
    public String addSite(Model model) {

        model.addAttribute("site", new NetworkSite());
        return "addsite";
    }
    
    @RequestMapping("/vpns")
    public String manageVpns(Model model) {

        List<Vpn> vpns = vpnsService.getVpns();
        
        model.addAttribute("vpns", vpns);
        return "vpns";
    }


    @RequestMapping("/createoffer")
    public String createOffer(Model model) {

        model.addAttribute("offer", new Offer());
        return "createoffer";
    }

    @RequestMapping(value = "/docreate", method = RequestMethod.POST)
    public String doCreate(Model model, @Valid Offer offer, BindingResult result) {
        if (result.hasErrors()) {
            return "createoffer";
        }
        //offersService.create(offer);
        offersService.throwTestException();
        return "offercreated";
    }
}
