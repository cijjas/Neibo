package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.webapp.form.ListingForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketplaceController.class);
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
    private final ProductService prs;
    private final RequestService rqs;
    private final InquiryService inqs;

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
                           final AvailabilityService avs,
                            final ProductService prs,
                            final RequestService rqs,
                            final InquiryService inqs
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
        this.prs = prs;
        this.rqs = rqs;
        this.inqs = inqs;
    }

    // ------------------------------------- Market --------------------------------------

    @RequestMapping(value = {"/products", "/"}, method = RequestMethod.GET)
    public ModelAndView marketplaceProducts(
        @RequestParam(value = "department", required = false) Integer department
    ) {
        LOGGER.info("User arriving at '/marketplace'");
        List<Product> productList = prs.getProductsByCriteria(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), Department.NONE , 1,10);
        System.out.println(productList.size());
        System.out.println(productList);
        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
        mav.addObject("productList", productList);
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
    public ModelAndView createListingForm(
        @ModelAttribute("listingForm") ListingForm listingForm
    ) {
        LOGGER.info("User arriving at '/marketplace/create-publishing'");


        ModelAndView mav = new ModelAndView("marketplace/views/sell");
        mav.addObject("channel", "Sell");
        mav.addObject("departmentList", Department.getDepartments());
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/create-listing", method = RequestMethod.POST)
    public ModelAndView crateListing(
        @Valid @ModelAttribute("listingForm") ListingForm listingForm,
        final BindingResult bindingResult
    ) {
        LOGGER.info("User arriving at '/marketplace/create-publishing'");
        if(bindingResult.hasErrors()){
            LOGGER.info("Error in form");
            return createListingForm(listingForm);
        }
        User user = sessionUtils.getLoggedUser();
        prs.createProduct(user.getUserId(), listingForm.getTitle(), listingForm.getDescription(), listingForm.getPrice(), true, Department.ELECTRONICS.getId() , null, null, null);
        ListingForm newListingForm = new ListingForm();
        return createListingForm(newListingForm);
    }


}
