package net.geant.coco.agent.portal.controllers;

import java.util.List;

import net.geant.coco.agent.portal.dao.Offer;
import net.geant.coco.agent.portal.dao.OfferDAO;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;

public class App {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                "net/geant/coco/agent/portal/beans/beans.xml");
        OfferDAO offersDao = (OfferDAO) context.getBean("offerDAO");
        System.out.println("hello world.");
        try {
            List<Offer> offers = offersDao.getOffers();
            
            for (Offer offer: offers) {
                System.out.println(offer);
            }
        } catch(DataAccessException ex) {
            System.out.println("error.");
        }
        ((ClassPathXmlApplicationContext) context).close();
    }

}
