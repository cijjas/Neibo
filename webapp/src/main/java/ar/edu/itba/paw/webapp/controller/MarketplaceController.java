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

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    public ModelAndView adminInformation() {
        LOGGER.info("User arriving at '/admin/information'");

        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
        mav.addObject("channel", "Marketplace");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }
}
