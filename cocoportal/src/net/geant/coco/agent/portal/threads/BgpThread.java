package net.geant.coco.agent.portal.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.geant.coco.agent.portal.bgp.BgpRouteEntry;
import net.geant.coco.agent.portal.bgp.BgpRouter;
import net.geant.coco.agent.portal.service.NetworkLinksService;
import net.geant.coco.agent.portal.service.NetworkSitesService;
import net.geant.coco.agent.portal.service.NetworkSwitchesService;

@Slf4j
public class BgpThread implements Runnable {

    private NetworkSwitchesService networkSwitchesService;
    private NetworkLinksService networkLinksService;
    private NetworkSitesService networkSitesService;
    private BgpRouter bgpRouter;

    private static final int SLEEP_INTERVAL = 2000;
    
    private Map<String, BgpRouteEntry> prefixToBgpRouteEntry = new HashMap<String, BgpRouteEntry>();
    
	public BgpThread(NetworkSwitchesService networkSwitchesService, NetworkLinksService networkLinksService, NetworkSitesService networkSitesService, BgpRouter bgpRouter) {
		this.networkSwitchesService = networkSwitchesService;
		this.networkLinksService = networkLinksService;
		this.networkSitesService = networkSitesService;
		this.bgpRouter = bgpRouter;

	}

	public void run() {
		
		// getting initial state
		prefixToBgpRouteEntry = getPrefixToBgpRouteEntryMap();
		
		try {
			Thread.sleep(SLEEP_INTERVAL);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while (true) {
			
			Map<String, BgpRouteEntry> newPrefixToBgpRouteEntry = new HashMap<String, BgpRouteEntry>();
		    
			List<BgpRouteEntry> bgpRouteEntryListNew = getNewRouteEntries(this.prefixToBgpRouteEntry, newPrefixToBgpRouteEntry);
			
			if (!bgpRouteEntryListNew.isEmpty()) {
				
				for (BgpRouteEntry bgpRouteEntry : bgpRouteEntryListNew) {
					String prefix = bgpRouteEntry.getPrefix();
					int vlanId = bgpRouteEntry.getRouteTarget();
					String neighborIp = bgpRouteEntry.getNexthop();
					
					addVirtualSiteExternal(prefix, vlanId, neighborIp);
				}
				
				this.prefixToBgpRouteEntry = newPrefixToBgpRouteEntry;
			}
			
	
	    	try {
				Thread.sleep(SLEEP_INTERVAL);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void addVirtualSiteExternal(String prefix, int vlanId, String neighborIp) {
		networkSitesService.insertNetworkSite(prefix, vlanId, neighborIp);
	}

	private Map<String, BgpRouteEntry> getPrefixToBgpRouteEntryMap() {
	    Map<String, BgpRouteEntry> localPrefixToBgpRouteEntry = new HashMap<String, BgpRouteEntry>();
	    
		List<BgpRouteEntry> list = bgpRouter.getVpns();
    	Iterator<BgpRouteEntry> it = list.iterator();
    	
    	while(it.hasNext())
    	{
    		BgpRouteEntry routeEntry = it.next();
    		
    		log.debug(routeEntry.toString());
    		
    		String prefix = routeEntry.getPrefix();
    		localPrefixToBgpRouteEntry.put(prefix, routeEntry);
    	}	    	
		
		return localPrefixToBgpRouteEntry;
	}
	
	private List<BgpRouteEntry> getNewRouteEntries(Map<String, BgpRouteEntry> oldPrefixToBgpRouteEntry, Map<String, BgpRouteEntry> newPrefixToBgpRouteEntry) {
		List<BgpRouteEntry> bgpRouteEntryList = new ArrayList<BgpRouteEntry>();
		
		Set<String> oldPrefixes = oldPrefixToBgpRouteEntry.keySet();
		Set<String> newPrefixes = newPrefixToBgpRouteEntry.keySet();
		
		newPrefixes.removeAll(oldPrefixes);
		
		for (String prefix : newPrefixes) {
			BgpRouteEntry newBgpRouteEntry = newPrefixToBgpRouteEntry.get(prefix);
			bgpRouteEntryList.add(newBgpRouteEntry);
		}
		
		return bgpRouteEntryList;
	}
}