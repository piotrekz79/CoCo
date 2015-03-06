package net.geant.coco.agent.portal.service;

import java.util.List;

import net.geant.coco.agent.portal.dao.NetworkLink;
import net.geant.coco.agent.portal.dao.NetworkLinkDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("networkLinksService")
public class NetworkLinksService {
    private NetworkLinkDao networkLinkDao;

    @Autowired
    public void setNetworkLinkDao(NetworkLinkDao networkLinkDao) {
        this.networkLinkDao = networkLinkDao;
    }

    public List<NetworkLink> getNetworkLinks() {
        return networkLinkDao.getNetworkLinks();
    }
}
