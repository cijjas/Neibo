package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.webapp.form.ListingForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.RequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
        @RequestParam(value = "department", required = false, defaultValue = "0") Integer department
    ) {
        LOGGER.info("User arriving at '/marketplace'");
        List<Product> productList = prs.getProductsByCriteria(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), Department.fromId(department) , 1,40);
        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
        mav.addObject("productList", productList);
        mav.addObject("channel", "Marketplace");
        mav.addObject("departmentList", Department.getDepartments());
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }

    @RequestMapping(value = "/my-purchases" , method = RequestMethod.GET)
    public ModelAndView myPurchases(

    ) {
        LOGGER.info("User arriving at '/marketplace/my-purchases'");

        List<Product> products = prs.getProductsBought(sessionUtils.getLoggedUser().getUserId(), 1, 10);

        ModelAndView mav = new ModelAndView("marketplace/views/myPurchases");
        mav.addObject("channel", "MyPurchases");
        mav.addObject("products", products);
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }
    @RequestMapping(value = "/my-sales", method = RequestMethod.GET)
    public ModelAndView mySales(

    ) {
        LOGGER.info("User arriving at '/marketplace/my-sales'");

        ModelAndView mav = new ModelAndView("marketplace/views/mySales");
        mav.addObject("products", prs.getProductsSold(sessionUtils.getLoggedUser().getUserId(), 1, 10));
        mav.addObject("channel", "MySales");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        return mav;
    }
    @RequestMapping(value = "/my-listings", method = RequestMethod.GET)
    public ModelAndView myListings(

    ) {
        LOGGER.info("User arriving at '/marketplace/my-listings'");

        ModelAndView mav = new ModelAndView("marketplace/views/myListings");

        mav.addObject("myProductList", prs.getProductsSelling(sessionUtils.getLoggedUser().getUserId(), 1, 10));
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
        LOGGER.info("User arriving at '/marketplace/create-publishing' POST");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'listingForm'");
            return createListingForm(listingForm);
        }
        User user = sessionUtils.getLoggedUser();
        prs.createProduct(user.getUserId(), listingForm.getTitle(), listingForm.getDescription(), listingForm.getPrice(), listingForm.getUsed(), listingForm.getDepartmentId() , listingForm.getImageFiles());
        return new ModelAndView("redirect:/marketplace/my-listings");
    }


    @RequestMapping(value = "/products/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView product(
            @PathVariable(value = "id") Long productId,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @RequestParam(value = "requestError", required = false, defaultValue = "false") Boolean requestError
    ) {
        LOGGER.info("User arriving at '/products/"+ productId +"' ");
        ModelAndView mav = new ModelAndView("marketplace/views/product");
        mav.addObject("requestError", requestError);
        mav.addObject("questions", inqs.getInquiriesByProduct(productId));
        mav.addObject("product", prs.findProductById(productId).orElseThrow(() -> new NotFoundException("Product not found")));
        return mav;
    }

    @RequestMapping(value = "/products/{id:\\d+}/request", method = RequestMethod.POST)
    public ModelAndView buyProduct(
            @PathVariable(value = "id") Long productId,
            @Valid @ModelAttribute("requestForm") RequestForm requestForm,
            final BindingResult bindingResult,
            @ModelAttribute("questionForm") QuestionForm questionForm
    ) {
        LOGGER.info("User requesting product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'requestForm'");
            return product(productId, requestForm, new QuestionForm(), true);
        }
        rqs.createRequest(sessionUtils.getLoggedUser().getUserId(), productId, requestForm.getRequestMessage());
        ModelAndView mav = new ModelAndView("marketplace/views/product");
        mav.addObject("product", prs.findProductById(productId).orElseThrow(() -> new NotFoundException("Product not found")));
        return mav;
    }

    @RequestMapping(value = "/products/{id:\\d+}/ask", method = RequestMethod.POST)
    public ModelAndView askProduct(
            @PathVariable(value = "id") Long productId,
            @Valid @ModelAttribute("questionForm") QuestionForm questionForm,
            final BindingResult bindingResult,
            @ModelAttribute("requestForm") RequestForm requestForm
    ) {
        LOGGER.info("User asking on product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'questionForm'");
            return product(productId, new RequestForm(), questionForm, true);
        }
//        System.out.println("creating question" + questionForm.getQuestionMessage());
        inqs.createInquiry(sessionUtils.getLoggedUser().getUserId(), productId, questionForm.getQuestionMessage());
        return new ModelAndView("redirect:/products/" + productId);
//        ModelAndView mav = new ModelAndView("marketplace/views/product");
//        mav.addObject("product", prs.findProductById(productId).orElseThrow(() -> new NotFoundException("Product not found")));
//        return mav;
    }
}
