package net.geant.coco.agent.portal.bgp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class BgpRouter {

	private String ipAddress;
	private int port;
		
	public BgpRouter(String ipAddress, int port) {
		super();
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public void addPeer(String ipAddress, int asNumber)
	{
		
		try {
		      TTransport transport;
		      transport = new TSocket(this.ipAddress, this.port);
		      transport.open();

		      TProtocol protocol = new  TBinaryProtocol(transport);
		      BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

		      client.createPeer(ipAddress, asNumber);

		      transport.close();
		    } catch (TException x) {
		      x.printStackTrace();
		    } 
		
	}
	
	public void addVpn(int aclNum, int routeMapNum, int seqNum, String prefix, String wildcard, String neighborIpAddress, int vpnNum)
	{
		try {
		      TTransport transport;
		      transport = new TSocket(this.ipAddress, this.port);
		      transport.open();
		      

		      TProtocol protocol = new  TBinaryProtocol(transport);
		      BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

		      // aclNum and seqNum are the same (per site)
		      // routeMapNum are per neighbor
		      client.pushRoute(aclNum, routeMapNum, seqNum, prefix, wildcard, neighborIpAddress, vpnNum);

		      transport.close();
		    } catch (TException x) {
		      x.printStackTrace();
		    } 
		
	}
	
	public void delVpn(int aclNum, int routeMapNum, int seqNum, String prefix, String neighborIpAddress) {

		try {
			TTransport transport;
			transport = new TSocket(this.ipAddress, this.port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

			client.withdrawRoute(aclNum, routeMapNum, seqNum, prefix, neighborIpAddress);
			
			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		}

	}
	
	public List<BgpRouteEntry> getVpns()
	{
		try {
			TTransport transport;
			transport = new TSocket(this.ipAddress, this.port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

			Routes routes = client.getRoutes(1, 1);
			List<Update> updates = routes.updates;

			List<BgpRouteEntry> retRoutes = new ArrayList<BgpRouteEntry>();
			
			Iterator<Update> it = updates.iterator();
			while(it.hasNext())
			{
				Update update = it.next();
				String prefixWithLength = update.prefix + "/" + update.prefixlen;
				String routeTarget = client.getRouteTarget(prefixWithLength);
				retRoutes.add(new BgpRouteEntry(update.prefix + "/" + update.prefixlen, update.rd, update.nexthop, update.label, routeTarget));

				
			}
			

			transport.close();
			return retRoutes;
		} catch (TException x) {
			x.printStackTrace();
		}
		return null;
	}
	
	public String getRouteTarget(String prefix)
	{
		String routeTarget = "";
		try {
		      TTransport transport;
		      transport = new TSocket(this.ipAddress, this.port);
		      transport.open();
		      

		      TProtocol protocol = new  TBinaryProtocol(transport);
		      BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

		      routeTarget = client.getRouteTarget(prefix);

		      transport.close();
		    } catch (TException x) {
		      x.printStackTrace();
		    } 
		
		return routeTarget;
		
	}
	
	
}
