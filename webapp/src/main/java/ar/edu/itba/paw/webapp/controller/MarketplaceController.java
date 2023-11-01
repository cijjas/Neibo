package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    private final SessionUtils sessionUtils;
    private final PostService ps;
    private final UserService us;
    private final NeighborhoodService nhs;
    private final CommentService cs;
    private final TagService ts;
    private final ChannelService chs;
    private final SubscriptionService ss;
    private final CategorizationService cas;
    private final ImageService is;
    private final AmenityService as;
    private final EventService es;
    private final ResourceService res;
    private final ContactService cos;
    private final ShiftService shs;
    private final AvailabilityService avs;

    // ------------------------------------- INFORMATION --------------------------------------

    @Autowired
    public MarketplaceController(SessionUtils sessionUtils,
                           final PostService ps,
                           final UserService us,
                           final NeighborhoodService nhs,
                           final CommentService cs,
                           final TagService ts,
                           final ChannelService chs,
                           final SubscriptionService ss,
                           final CategorizationService cas,
                           final ImageService is,
                           final AmenityService as,
                           final EventService es,
                           final ResourceService res,
                           final ContactService cos,
                           final ShiftService shs,
                           final AvailabilityService avs
    ) {
        this.sessionUtils = sessionUtils;
        this.is = is;
        this.ps = ps;
        this.us = us;
        this.nhs = nhs;
        this.cs = cs;
        this.ts = ts;
        this.chs = chs;
        this.ss = ss;
        this.cas = cas;
        this.as = as;
        this.es = es;
        this.res = res;
        this.cos = cos;
        this.shs = shs;
        this.avs = avs;
    }

    // ------------------------------------- Market --------------------------------------

    @RequestMapping(value = {"/products", "/"}, method = RequestMethod.GET)
    public ModelAndView marketplaceProducts(

    ) {
        LOGGER.info("User arriving at '/marketplace'");

        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
        mav.addObject("channel", "Marketplace");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/my-purchases" , method = RequestMethod.GET)
    public ModelAndView myPurchases(

    ) {
        LOGGER.info("User arriving at '/marketplace/my-purchases'");

        ModelAndView mav = new ModelAndView("marketplace/views/myPurchases");
        mav.addObject("channel", "MyPurchases");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }
    @RequestMapping(value = "/my-sales", method = RequestMethod.GET)
    public ModelAndView mySales(

    ) {
        LOGGER.info("User arriving at '/marketplace/my-sales'");

        ModelAndView mav = new ModelAndView("marketplace/views/mySales");
        mav.addObject("channel", "MySales");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }
    @RequestMapping(value = "/my-listings", method = RequestMethod.GET)
    public ModelAndView myListings(

    ) {
        LOGGER.info("User arriving at '/marketplace/my-listings'");

        ModelAndView mav = new ModelAndView("marketplace/views/myListings");
        mav.addObject("channel", "MyListings");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/create-listing", method = RequestMethod.GET)
    public ModelAndView createListing(

    ) {
        LOGGER.info("User arriving at '/marketplace/create-publishing'");

        ModelAndView mav = new ModelAndView("marketplace/views/sell");
        mav.addObject("channel", "Sell");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/create-listing", method = RequestMethod.POST)
    public ModelAndView crateListing(

    ) {
        LOGGER.info("User arriving at '/marketplace/create-publishing'");

        ModelAndView mav = new ModelAndView("marketplace/views/sell");
        mav.addObject("channel", "Sell");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }


}
