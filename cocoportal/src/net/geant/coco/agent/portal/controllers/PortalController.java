package net.geant.coco.agent.portal.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import net.geant.coco.agent.portal.dao.NetworkElement;
import net.geant.coco.agent.portal.dao.NetworkInterface;
import net.geant.coco.agent.portal.dao.NetworkLink;
import net.geant.coco.agent.portal.dao.NetworkSite;
import net.geant.coco.agent.portal.dao.NetworkSwitch;
import net.geant.coco.agent.portal.dao.Vpn;
import net.geant.coco.agent.portal.rest.RestVpnURIConstants;
import net.geant.coco.agent.portal.rest.Shop;
import net.geant.coco.agent.portal.rest.Student;
import net.geant.coco.agent.portal.rest.RestVpn;
import net.geant.coco.agent.portal.service.NetworkLinksService;
import net.geant.coco.agent.portal.service.NetworkSitesService;
import net.geant.coco.agent.portal.service.NetworkSwitchesService;
import net.geant.coco.agent.portal.service.TopologyService;
import net.geant.coco.agent.portal.service.VpnsService;
import net.geant.coco.agent.portal.utils.Pce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PortalController {

    private NetworkSwitchesService networkSwitchesService;
    private NetworkLinksService networkLinksService;
    private NetworkSitesService networkSitesService;
    private VpnsService vpnsService;
    private TopologyService topologyService;
    private Pce pce;

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
    
    @Autowired
    public void setTopologyService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String showTest(Model model, @RequestParam("id") String id) {
        log.info("Id is: " + id);
        return "portal";
    }

    /*
     * @ExceptionHandler(DataAccessException.class) public String
     * handleDatabaseException(DataAccessException ex) { return "error"; }
     */

    @RequestMapping("/")
    public String showCoCoPortal(Model model) {

        // offersService.throwTestException();

        List<NetworkSwitch> networkSwitches = networkSwitchesService.getNetworkSwitches();
        List<NetworkSwitch> networkSwitchesWithEnni = networkSwitchesService.getNetworkSwitchesWithNni();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();
        List<NetworkSite> networkSites = networkSitesService.getNetworkSites();
        List<Vpn> vpns = vpnsService.getVpns();

        model.addAttribute("switches", networkSwitches);
        model.addAttribute("links", networkLinks);
        model.addAttribute("sites", networkSites);
        model.addAttribute("vpns", vpns);
        
        setUpPce(networkSwitches, networkSites, networkSwitchesWithEnni);
        return "portal";

    }

    public void setUpPce(List<NetworkSwitch> networkSwitches, List<NetworkSite> networkSites, List<NetworkSwitch> networkSwitchesWithEnni) {

        class SetupThread implements Runnable {

        	List<NetworkSwitch> networkSwitches;
        	List<NetworkSite> networkSites;
        	List<NetworkSwitch> networkSwitchesWithEnni;
        	
        	   public SetupThread(List<NetworkSwitch> networkSwitches, List<NetworkSite> networkSites, List<NetworkSwitch> networkSwitchesWithEnni) {
        	       this.networkSwitches = networkSwitches;
        	       this.networkSites = networkSites;
        	       this.networkSwitchesWithEnni = networkSwitchesWithEnni;
        	   }

        	   public void run() {
        	    	pce = new Pce(networkSwitches, networkSites, networkSwitchesWithEnni);
        	        pce.setupCoreForwarding();
        	   }
        	}
        
        Runnable setupThreadRunnable = new SetupThread(networkSwitches, networkSites, networkSwitchesWithEnni);
        log.debug("Starting core provisioning thread");
        new Thread(setupThreadRunnable).start();
        log.debug("Started core provisioning thread");
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

        log.info("vpns: " + vpnName);
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
            @RequestParam(value = "addswitch", defaultValue = "") String addSwitch,
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

        log.info("updatevpn vpn: " + vpnName);
        log.info("updatevpn add site: " + addSiteName);
        log.info("updatevpn delete site: " + deleteSiteName);
        
        List<NetworkSwitch> networkSwitchesWithNni = networkSwitchesService.getNetworkSwitchesWithNni();

        //System.out.println(networkSitesService.getNetworkSite("site1"));
        
        //pce.addSiteToVpn(foo, vpnSites);

        model.addAttribute("switches", networkSwitches);
        model.addAttribute("links", networkLinks);
        model.addAttribute("freesites", freeSites);
        model.addAttribute("vpnname", vpnName);
        model.addAttribute("vpns", vpns);
        model.addAttribute("vpnname", vpnName);
        model.addAttribute("ext_switches", networkSwitchesWithNni);
        
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
            @RequestParam(value = "addswitch", defaultValue = "") String addSwitch,
            @RequestParam(value = "done", defaultValue = "") String done,
            Model model) {

        List<NetworkSite> networkSites;
        List<Vpn> vpns = vpnsService.getVpns();

        List<NetworkSwitch> networkSwitches = networkSwitchesService
                .getNetworkSwitches();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();

        log.info("doupdatevpn vpn: " + vpnName);
        log.info("doupdatevpn add site: " + addSiteName);
        log.info("doupdatevpn delete site: " + deleteSiteName);
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
                    log.info("MPLS label for " + vpnName + " is " + vpn.getMplsLabel());
                    pce.addSiteToVpn(networkSite, vpn.getMplsLabel(), networkSitesService.getNetworkSites(vpnName));
                }
            }
        }

        if (!deleteSiteName.equals("")) {
            vpnsService.deleteSite(deleteSiteName);
         // find site object
            for (NetworkSite networkSite: networkSitesService.getNetworkSites()) {
                if (networkSite.getName().equals(deleteSiteName)) {
                    Vpn vpn = vpnsService.getVpn(vpnName);
                    log.info("MPLS label for " + vpnName + " is " + vpn.getMplsLabel());
                    pce.deleteSiteFromVpn(networkSite, vpn.getMplsLabel(), networkSitesService.getNetworkSites(vpnName));
                }
            }
        }

        networkSites = networkSitesService.getNetworkSites(vpnName);
        List<NetworkSite> freeSites = networkSitesService
                .getNetworkSites("all");
        model.addAttribute("sites", networkSites);
        model.addAttribute("freesites", freeSites);
        
        return "updatevpn";
    }
    
    
    
    @RequestMapping(value="topology", method=RequestMethod.GET)  
    public @ResponseBody  
    List<NetworkInterface> getTopology() {  
     return topologyService.getNetworkInterfaces();
    }
    
    @RequestMapping(value="topology/vis", method=RequestMethod.GET)  
    public @ResponseBody  
    String getTopologyVis() {
    	StringBuilder visJson = new StringBuilder();
    	
    	Set<NetworkElement> nodeSet = new HashSet<NetworkElement>();
    	
    	List<NetworkInterface> networkInterfaces = topologyService.getNetworkInterfaces();
    	
    	for (NetworkInterface networkInterface : networkInterfaces) {
    		if (!nodeSet.contains(networkInterface.source)) {
    			nodeSet.add(networkInterface.source);
    		}
    		
    		if (!nodeSet.contains(networkInterface.neighbour)) {
    			nodeSet.add(networkInterface.neighbour);
    		}
		}
    	visJson.append("{\"nodes\" : [ ");
    	for (NetworkElement networkElement : nodeSet) {
			visJson.append("{\"id\": \"");
			visJson.append(networkElement.name);
			visJson.append("\", \"label\": \"");
			visJson.append(networkElement.name);
			visJson.append("\", \"group\": \"");
			visJson.append(networkElement.nodeType);
			visJson.append("\"}, ");
		}

    	visJson.deleteCharAt(visJson.lastIndexOf(","));
    	visJson.append("],");
    	visJson.append("\"edges\" : [");
    	
    	for (NetworkInterface networkInterface : networkInterfaces) {
    		visJson.append("{\"from\": \"");
    		visJson.append(networkInterface.source.name);
    		visJson.append("\", \"to\": \"");
    		visJson.append(networkInterface.neighbour.name);
    		visJson.append("\"}, ");
		}
    	visJson.deleteCharAt(visJson.lastIndexOf(","));
    	visJson.append("]}");
    	
    	return visJson.toString();
    } 
    
    @RequestMapping(value="student", method=RequestMethod.GET)  
    public @ResponseBody  
    Student getStudent() {  
     return new Student(23, "meghna", "Naidu", "meghna@gmail.com",  
       "8978767878");  
    }  
     
    @RequestMapping(value="studentlist", method=RequestMethod.GET)  
    public @ResponseBody  
    List<Student> getStudentList() {  
     List<Student> studentList = new ArrayList<Student>();  
     studentList.add(new Student(23, "Meghna", "Naidu", "meghna@gmail.com",  
       "8978767878"));  
     studentList.add(new Student(3, "Robert", "Parera", "robert@gmail.com",  
       "8978767878"));  
     studentList.add(new Student(93, "Andrew", "Strauss",  
       "andrew@gmail.com", "8978767878"));  
     studentList.add(new Student(239, "Eddy", "Knight", "knight@gmail.com",  
       "7978767878"));  
     
     return studentList;  
    }
    
    @RequestMapping(value = "{name}", method = RequestMethod.GET)
	public @ResponseBody
	Shop getShopInJSON(@PathVariable String name) {

		Shop shop = new Shop();
		shop.setName(name);
		shop.setStaffName(new String[] { "mkyong1", "mkyong2" });

		return shop;

	}
    
    
    //Map to store employees, ideally we should use database
    Map<Integer, RestVpn> vpnData = new HashMap<Integer, RestVpn>();
     
    @RequestMapping(value = RestVpnURIConstants.DUMMY_VPN, method = RequestMethod.GET)
    public @ResponseBody RestVpn getDummyVpn() {
    	log.info("Start getDummyVpn");
        RestVpn vpn = new RestVpn();
        vpn.setId(9999);
        vpn.setName("Dummy");
        //emp.setCreatedDate(new Date());
        vpnData.put(9999, vpn);
        return vpn;
    }
     
    @RequestMapping(value = RestVpnURIConstants.GET_VPN, method = RequestMethod.GET)
    public @ResponseBody RestVpn getVpn(@PathVariable("id") int vpnId) {
    	log.info("Start getVpn. ID="+vpnId);
         
        return vpnData.get(vpnId);
    }
    
    @RequestMapping(value = RestVpnURIConstants.UPDATE_VPN, method = RequestMethod.PUT)
    public @ResponseBody RestVpn updateVpn(@PathVariable("id") int vpnId, @RequestBody RestVpn vpn) {
    	log.info("Start updateVpn. ID="+vpnId);
    	
    	RestVpn vpn_current = vpnData.get(vpnId);
    	if (vpn_current != null ) {
    		vpn_current.setName(vpn.getName());
            vpnData.put(vpn.getId(), vpn_current);
    	}

        return vpnData.get(vpnId);
    }
     
    @RequestMapping(value = RestVpnURIConstants.GET_ALL_VPN, method = RequestMethod.GET)
    public @ResponseBody List<Vpn> getAllVpns() {
        log.info("Start getAllVpns.");
        
        /*
        List<vpns> this.vpnsService.getVpns();
        
        List<RestVpn> vpns = new ArrayList<RestVpn>();
        Set<Integer> vpnIdKeys = vpnData.keySet();
        for(Integer i : vpnIdKeys){
            vpns.add(vpnData.get(i));
        }
        */
        return vpnsService.getVpns();
    }
    
    @RequestMapping(value = RestVpnURIConstants.GET_ALL_SITES, method = RequestMethod.GET)
    public @ResponseBody List<NetworkSite> getAllSites(@PathVariable("id") int vpnID) {
        log.info("Start getall sites.");
        
        /*
        List<vpns> this.vpnsService.getVpns();
        
        List<RestVpn> vpns = new ArrayList<RestVpn>();
        Set<Integer> vpnIdKeys = vpnData.keySet();
        for(Integer i : vpnIdKeys){
            vpns.add(vpnData.get(i));
        }
        */
        Vpn vpn = vpnsService.getVpn(vpnID);
        
        return networkSitesService.getNetworkSites(vpn.getName());
    }
     
    @RequestMapping(value = RestVpnURIConstants.CREATE_VPN, method = RequestMethod.POST)
    public @ResponseBody RestVpn createVpn(@RequestBody RestVpn vpn) {
    	log.info("Start createVpn.");
        //emp.setCreatedDate(new Date());
        vpnData.put(vpn.getId(), vpn);
        return vpn;
    }
     
    @RequestMapping(value = RestVpnURIConstants.DELETE_VPN, method = RequestMethod.PUT)
    public @ResponseBody RestVpn deleteVpn(@PathVariable("id") int vpnId) {
    	log.info("Start deleteVpn.");
        RestVpn vpn = vpnData.get(vpnId);
        vpnData.remove(vpnId);
        return vpn;
    }
}
