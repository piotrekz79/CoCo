package net.geant.coco.agent.portal.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import net.geant.coco.agent.portal.dao.NetworkLink;
import net.geant.coco.agent.portal.dao.NetworkLinkDao;
import net.geant.coco.agent.portal.dao.NetworkSite;
import net.geant.coco.agent.portal.dao.NetworkSiteDao;
import net.geant.coco.agent.portal.dao.NetworkSwitch;
import net.geant.coco.agent.portal.dao.NetworkSwitchDao;
import net.geant.coco.agent.portal.dao.Vpn;
import net.geant.coco.agent.portal.dao.VpnDao;
import net.geant.coco.agent.portal.service.NetworkLinksService;
import net.geant.coco.agent.portal.service.NetworkSitesService;
import net.geant.coco.agent.portal.service.NetworkSwitchesService;
import net.geant.coco.agent.portal.service.VpnsService;

@Component
public class TestApp {
	
	private static NetworkSwitchesService networkSwitchesService;
    private static NetworkLinksService networkLinksService;
    private static NetworkSitesService networkSitesService;
    private static VpnsService vpnsService;
    private static Pce pce;
	private static long testTime;
	
	private static final String lastFlowFilename = "lastFlowNumber.txt";
	    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.print("Test");
		
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("coco");
		dataSource.setPassword("cocorules!");
		dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/CoCoDB");
		dataSource.setMaxActive(10);
		dataSource.setMaxIdle(5);
		dataSource.setInitialSize(5);
		dataSource.setValidationQuery("SELECT 1");
		
		NetworkSwitchDao networkSwitchDao = new NetworkSwitchDao();
		networkSwitchDao.setDataSource(dataSource);
		networkSwitchesService = new NetworkSwitchesService();
		networkSwitchesService.setNetworkSwitchDao(networkSwitchDao);
		
		NetworkLinkDao networkLinkDao = new NetworkLinkDao();
		networkLinkDao.setDataSource(dataSource);
		networkLinksService = new NetworkLinksService();
		networkLinksService.setNetworkLinkDao(networkLinkDao);
		
		NetworkSiteDao networkSiteDao = new NetworkSiteDao();
		networkSiteDao.setDataSource(dataSource);
		networkSitesService = new NetworkSitesService();
		networkSitesService.setNetworkSiteDao(networkSiteDao);
		
		VpnDao vpnsDao = new VpnDao();
		vpnsDao.setDataSource(dataSource);
		vpnsService = new VpnsService();
		vpnsService.setVpnDao(vpnsDao);
		
		List<NetworkSwitch> networkSwitches = networkSwitchesService.getNetworkSwitches();
        List<NetworkLink> networkLinks = networkLinksService.getNetworkLinks();
        List<NetworkSite> networkSites = networkSitesService.getNetworkSites();
        List<Vpn> vpns = vpnsService.getVpns();

        
        int lastFlowNumber = 1;
        try {
			Scanner in = new Scanner(new FileReader(lastFlowFilename));
			lastFlowNumber = in.nextInt();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        //pce = new Pce(networkSwitches, networkSites);
        pce = new Pce(networkSwitches, networkSites, lastFlowNumber);
        //Topology topo = pce.getTopology();
        
        //List<CoCoLink> pathList = topo.calculatePath("h1", "h5");
        //pathList = topo.calculatePath("h5", "h1");
        
//        pathList = topo.calculatePath("h1", "h5");
//        
//        pathList = topo.calculatePath("h5", "h1");
        
        pce.setupCoreForwarding();
        
        
        // real thing
        List<String> argumentList = new ArrayList<String>(Arrays.asList(args));
        
        if (argumentList.size() <= 1) {
        	System.out.println("Usage: pass <experimentTime>, <list of sites to connect to vpn> as arguments.");
        }
        testTime = Long.parseLong(argumentList.remove(0), 10);
        
        List<String> siteNamesToAddToVpn = new ArrayList<String>();
        
        System.out.println("Sites added to VPN:");
        for (String s: argumentList) {
        	siteNamesToAddToVpn.add(s);
            System.out.println(s);
        }
       
        
        String vpnName = "vpn1";
        
//        for (String site : siteNamesToAddToVpn) {
//        	doUpdateVpn(vpnName, "", site);
//        }
        
        
        for (String site : siteNamesToAddToVpn) {
        	doUpdateVpn(vpnName, site, "");
        }
        
        System.out.println("Press any key...");
        try {
			System.in.read();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
//        try {
//			Thread.sleep(testTime);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        for (String site : siteNamesToAddToVpn) {
        	doUpdateVpn(vpnName, "", site);
        }
        
        
		try {
			PrintWriter writer = new PrintWriter(lastFlowFilename, "UTF-8");
	        writer.println(Integer.toString(pce.getFlowId()));
	        writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        
        // test setup
        
//        String vpnName = "vpn1";
//        String site1 = "h1";
//        String site2 = "h5";
//        
//        doUpdateVpn(vpnName, site1, "");
//        doUpdateVpn(vpnName, site2, "");
//        
//        try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
//        doUpdateVpn(vpnName, "", site1);
//        doUpdateVpn(vpnName, "", site2);
		
	}
	
	public static void doUpdateVpn(String vpnName, String addSiteName, String deleteSiteName) {
        System.out.println("doupdatevpn vpn: " + vpnName);
        System.out.println("doupdatevpn add site: " + addSiteName);
        System.out.println("doupdatevpn delete site: " + deleteSiteName);
       

        if (!addSiteName.equals("")) {
            vpnsService.addSite(vpnName, addSiteName);
            // find site object
            for (NetworkSite networkSite: networkSitesService.getNetworkSites()) {
                if (networkSite.getName().equals(addSiteName)) {
                    Vpn vpn = vpnsService.getVpn(vpnName);
                    System.out.println("MPLS label for " + vpnName + " is " + vpn.getMplsLabel());
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
                    System.out.println("MPLS label for " + vpnName + " is " + vpn.getMplsLabel());
                    pce.deleteSiteFromVpn(networkSite, vpn.getMplsLabel(), networkSitesService.getNetworkSites(vpnName));
                }
            }
        }
        
	}

}
