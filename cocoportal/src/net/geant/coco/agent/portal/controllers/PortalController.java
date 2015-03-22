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
import net.geant.coco.agent.portal.utils.Pce;

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
    private Pce pce;

    @Autowired
    public void setOffersService(OffersService offersService) {
        this.offersService = offersService;
    }

    @Autowired
    public void setNetworkSwitchService(
            NetworkSwitchesService networkSwitchesService) {
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
     * @ExceptionHandler(DataAccessException.class) public String
     * handleDatabaseException(DataAccessException ex) { return "error"; }
     */

    @RequestMapping("/offers")
    public String showPortal(Model model) {

        // offersService.throwTestException();

        List<Offer> offers = offersService.getCurrent();

        model.addAttribute("offers", offers);
        return "offers";
    }

    @RequestMapping("/")
    public String showCoCoPortal(Model model) {

        // offersService.throwTestException();

        List<NetworkSwitch> networkSwitches = networkSwitchesService
                .getNetworkSwitches();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();
        List<NetworkSite> networkSites = networkSitesService.getNetworkSites();
        List<Vpn> vpns = vpnsService.getVpns();

        pce = new Pce(networkSwitches, networkSites);
        pce.setupCoreForwarding();
        
        // model.addAttribute("switchNodes", switchNodes);
        // return "switchNodes";

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
    public String manageVpns(@RequestParam("vpn") String vpnName, Model model) {

        List<Vpn> vpns = vpnsService.getVpns();
        List<NetworkSite> networkSites = networkSitesService
                .getNetworkSites(vpnName);
        List<NetworkSite> freeSites = networkSitesService
                .getNetworkSites("all");

        System.out.println("vpns: " + vpnName);
        model.addAttribute("vpns", vpns);
        model.addAttribute("vpnname", vpnName);
        model.addAttribute("sites", networkSites);
        model.addAttribute("freesites", freeSites);
        return "vpns";
    }

    @RequestMapping("/updatevpn")
    public String updateVpn(
            @RequestParam(value = "vpn", defaultValue = "") String vpnName,
            @RequestParam(value = "deletesite", defaultValue = "") String deleteSiteName,
            @RequestParam(value = "addsite", defaultValue = "") String addSiteName,
            @RequestParam(value = "showvpn", defaultValue = "") String showVpn,
            @RequestParam(value = "newvpn", defaultValue = "") String newVpn,
            @RequestParam(value = "done", defaultValue = "") String done,
            Model model) {
        List<Vpn> vpns = vpnsService.getVpns();
        List<NetworkSite> networkSites;
        List<NetworkSite> freeSites = networkSitesService
                .getNetworkSites("all");
        List<NetworkSite> vpnSites = networkSitesService
        .getNetworkSites("vpn");
        List<NetworkSwitch> networkSwitches = networkSwitchesService
                .getNetworkSwitches();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();

        System.out.println("updatevpn vpn: " + vpnName);
        System.out.println("updatevpn add site: " + addSiteName);
        System.out.println("updatevpn delete site: " + deleteSiteName);
        
        //System.out.println(networkSitesService.getNetworkSite("site1"));
        
        //pce.addSiteToVpn(foo, vpnSites);

        model.addAttribute("switches", networkSwitches);
        model.addAttribute("links", networkLinks);
        model.addAttribute("freesites", freeSites);
        model.addAttribute("vpnname", vpnName);
        model.addAttribute("vpns", vpns);
        model.addAttribute("vpnname", vpnName);

        if (!addSiteName.equals("")) {

        }

        // show another VPN
        if (!showVpn.equals("") || (!done.equals(""))) {
            networkSites = networkSitesService.getNetworkSites();
            model.addAttribute("sites", networkSites);
            return "portal";
        }

        // manage VPN
        networkSites = networkSitesService.getNetworkSites(vpnName);
        model.addAttribute("sites", networkSites);
        return "updatevpn";
    }

    @RequestMapping("/doupdatevpn")
    public String doUpdateVpn(
            @RequestParam(value = "vpn", defaultValue = "") String vpnName,
            @RequestParam(value = "deletesite", defaultValue = "") String deleteSiteName,
            @RequestParam(value = "addsite", defaultValue = "") String addSiteName,
            @RequestParam(value = "showvpn", defaultValue = "") String showVpn,
            @RequestParam(value = "newvpn", defaultValue = "") String newVpn,
            @RequestParam(value = "done", defaultValue = "") String done,
            Model model) {

        List<NetworkSite> networkSites;
        List<Vpn> vpns = vpnsService.getVpns();

        List<NetworkSwitch> networkSwitches = networkSwitchesService
                .getNetworkSwitches();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();

        System.out.println("doupdatevpn vpn: " + vpnName);
        System.out.println("doupdatevpn add site: " + addSiteName);
        System.out.println("doupdatevpn delete site: " + deleteSiteName);
        model.addAttribute("switches", networkSwitches);
        model.addAttribute("links", networkLinks);
        model.addAttribute("vpnname", vpnName);
        model.addAttribute("vpns", vpns);
        model.addAttribute("vpnname", vpnName);

        if (done.equals("done")) {
            networkSites = networkSitesService.getNetworkSites();
            model.addAttribute("sites", networkSites);
            return "portal";
        }

        if (!addSiteName.equals("")) {
            vpnsService.addSite(vpnName, addSiteName);
            // find site object
            for (NetworkSite networkSite: networkSitesService.getNetworkSites()) {
                if (networkSite.getName().equals(addSiteName)) {
                    Vpn vpn = vpnsService.getVpn(vpnName);
                    System.out.println("MPLS labl voor " + vpnName + " is " + vpn.getMplsLabel());
                    pce.addSiteToVpn(networkSite, networkSitesService.getNetworkSites(vpnName));
                }
            }
        }

        if (!deleteSiteName.equals("")) {
            vpnsService.deleteSite(deleteSiteName);
        }

        networkSites = networkSitesService.getNetworkSites(vpnName);
        List<NetworkSite> freeSites = networkSitesService
                .getNetworkSites("all");
        model.addAttribute("sites", networkSites);
        model.addAttribute("freesites", freeSites);
        
        return "updatevpn";
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
        // offersService.create(offer);
        offersService.throwTestException();
        return "offercreated";
    }
}
