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
	
	public void addVpn(String prefix, String rd, int tag)
	{
		try {
		      TTransport transport;
		      transport = new TSocket(this.ipAddress, this.port);
		      transport.open();
		      

		      TProtocol protocol = new  TBinaryProtocol(transport);
		      BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

		      client.pushRoute(prefix, "", rd, tag);

		      transport.close();
		    } catch (TException x) {
		      x.printStackTrace();
		    } 
		
	}
	
	public void delVpn(String prefix,String rd) {

		try {
			TTransport transport;
			transport = new TSocket(this.ipAddress, this.port);
			transport.open();

			TProtocol protocol = new TBinaryProtocol(transport);
			BgpConfigurator.Client client = new BgpConfigurator.Client(protocol);

			client.withdrawRoute(prefix, rd);

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
				
				retRoutes.add(new BgpRouteEntry(update.prefix + "/" + update.prefixlen, update.rd, update.nexthop, update.label));

				
			}
			

			transport.close();
			return retRoutes;
		} catch (TException x) {
			x.printStackTrace();
		}
		return null;
	}
	
	
}
