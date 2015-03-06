package net.geant.coco.agent.portal.service;

import java.util.List;

import net.geant.coco.agent.portal.dao.Offer;
import net.geant.coco.agent.portal.dao.OfferDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("offersService")
public class OffersService {

    private OfferDAO offerDao;

    @Autowired
    public void setOfferDao(OfferDAO offerDao) {
        this.offerDao = offerDao;
    }

    public List<Offer> getCurrent() {
        return offerDao.getOffers();
    }

    public void create(Offer offer) {
       offerDao.create(offer);
    }

    public void throwTestException() {
       offerDao.getOffers();
    }
}
