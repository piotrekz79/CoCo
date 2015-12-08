package net.geant.coco.agent.portal.threads;

import java.util.Iterator;
import java.util.List;

import net.geant.coco.agent.portal.bgp.BgpRouteEntry;
import net.geant.coco.agent.portal.bgp.BgpRouter;
import net.geant.coco.agent.portal.service.NetworkLinksService;
import net.geant.coco.agent.portal.service.NetworkSitesService;
import net.geant.coco.agent.portal.service.NetworkSwitchesService;

public class BgpThread implements Runnable {

    private NetworkSwitchesService networkSwitchesService;
    private NetworkLinksService networkLinksService;
    private NetworkSitesService networkSitesService;
    private BgpRouter bgpRouter;
    private List<BgpRouteEntry> bgpRouteEntryList;
    
	public BgpThread(NetworkSwitchesService networkSwitchesService, NetworkLinksService networkLinksService, NetworkSitesService networkSitesService, BgpRouter bgpRouter) {
		this.networkSwitchesService = networkSwitchesService;
		this.networkLinksService = networkLinksService;
		this.networkSitesService = networkSitesService;
		this.bgpRouter = bgpRouter;

	}

	public void run() {
		while (true) {
			List<BgpRouteEntry> list = bgpRouter.getVpns();
	    	
	    	Iterator<BgpRouteEntry> it = list.iterator();
	    	
	    	while(it.hasNext())
	    	{
	    		BgpRouteEntry routeEntry = it.next();
	    		System.out.println(routeEntry);
	    		String prefix = routeEntry.getPrefix();
	    		String routeTarget = bgpRouter.getRouteTarget(prefix);
	    		System.out.println("RT:" + routeTarget);
	    	}
	    	String routeTarget = bgpRouter.getRouteTarget("10.0.0.1/24");
	    	System.out.println("RT:" + routeTarget);
	    	
	    	try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}