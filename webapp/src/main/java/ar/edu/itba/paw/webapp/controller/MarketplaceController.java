package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.MainEntities.Product;
import ar.edu.itba.paw.models.MainEntities.User;
import ar.edu.itba.paw.webapp.form.ListingForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.ReplyForm;
import ar.edu.itba.paw.webapp.form.RequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    public MarketplaceController(SessionUtils sessionUtils,
                           final PostService ps,
                           final UserService us,
                           final NeighborhoodService nhs,
                           final CommentService cs,
                           final TagService ts,
                           final ChannelService chs,
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

    // --------------------------------------------------- MARKET ------------------------------------------------------


    @RequestMapping(value = {"/products/{department}"}, method = RequestMethod.GET)
    public ModelAndView marketplaceProducts(
            @PathVariable(value = "department") String department,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size
    ) {
        if(department == null || department.isEmpty()){
            department = "all";
        }
        LOGGER.info("User arriving at '/marketplace'");
        List<Product> productList = prs.getProductsByCriteria(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), Department.fromURLString(department) , page,size);
        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
        mav.addObject("productList", productList);
        mav.addObject("channel", "Marketplace");
        mav.addObject("departmentList", Department.getDepartmentsWithUrls());
        mav.addObject("departmentName", Objects.requireNonNull(Department.fromURLString(department)).name());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsTotalPages(sessionUtils.getLoggedUser().getNeighborhood().getNeighborhoodId(), size, Department.fromURLString(department)));
        mav.addObject("contextPath", "/marketplace/products/" + department);
        return mav;
    }
    @RequestMapping(value = { "/"}, method = RequestMethod.GET)
    public String redirectToMarketplace() {
        return "redirect:/marketplace/products/all";
    }

    @RequestMapping(value = "/my-purchases" , method = RequestMethod.GET)
    public ModelAndView myPurchases(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/my-purchases'");

        List<Product> products = prs.getProductsBought(sessionUtils.getLoggedUser().getUserId(), page, size);

        ModelAndView mav = new ModelAndView("marketplace/views/myPurchases");
        mav.addObject("channel", "MyPurchases");
        mav.addObject("products", products);
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(sessionUtils.getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/my-purchases");
        return mav;
    }
    @RequestMapping(value = "/my-sales", method = RequestMethod.GET)
    public ModelAndView mySales(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/my-sales'");

        ModelAndView mav = new ModelAndView("marketplace/views/mySales");
        mav.addObject("products", prs.getProductsSold(sessionUtils.getLoggedUser().getUserId(), page, size));
        mav.addObject("channel", "MySales");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsSoldTotalPages(sessionUtils.getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/my-sales");
        return mav;
    }



    @RequestMapping(value = "/my-requests/{productId:\\d+}", method = RequestMethod.GET)
    public ModelAndView saleRequests(
            @PathVariable(value = "productId") int productId
    ) {
        LOGGER.info("User arriving at '/marketplace/my-requests/{}'", productId);

        ModelAndView mav = new ModelAndView("marketplace/views/saleRequests");
        mav.addObject("requests", prs.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")).getRequesters());
        mav.addObject("product", prs.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")));
        mav.addObject("channel", "MySales");  // this is wrong
        return mav;
    }

    @RequestMapping(value = "/requested-listings" , method = RequestMethod.GET)
    public ModelAndView requestedListings(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at /requested-listings");

        ModelAndView mav = new ModelAndView("marketplace/views/requestedListings");
        mav.addObject("channel", "MyRequested");
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(sessionUtils.getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/my-purchases");
        return mav;
    }


    @RequestMapping(value = "/mark-as-bought", method = RequestMethod.POST)
    public ModelAndView markAsBought(
            @RequestParam(value = "buyerId") int buyerId,
            @RequestParam(value = "productId") int productId

    ) {
        LOGGER.info("User arriving at '/marketplace/mark-as-bought'");
        /*prs.markAsBought(buyerId, productId, units!); ahora falta units que se compraron*/
        return new ModelAndView("redirect:/marketplace/my-sales");
    }

    @RequestMapping(value = "/my-listings", method = RequestMethod.GET)
    public ModelAndView myListings(
        @RequestParam (value = "page", required = false, defaultValue = "1") int page,
        @RequestParam (value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/my-listings'");

        ModelAndView mav = new ModelAndView("marketplace/views/myListings");

        mav.addObject("myProductList", prs.getProductsSelling(sessionUtils.getLoggedUser().getUserId(), page, size));
        mav.addObject("channel", "MyListings");
        mav.addObject("loggedUser", sessionUtils.getLoggedUser());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsSellingTotalPages(sessionUtils.getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/my-listings");
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
    public ModelAndView createListing(
        @Valid @ModelAttribute("listingForm") ListingForm listingForm,
        final BindingResult bindingResult
    ) {
        LOGGER.info("User arriving at '/marketplace/create-publishing' POST");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'listingForm'");
            return createListingForm(listingForm);
        }
        User user = sessionUtils.getLoggedUser();
/*
        prs.createProduct(user.getUserId(), listingForm.getTitle(), listingForm.getDescription(), listingForm.getPrice(), listingForm.getUsed(), listingForm.getDepartmentId() , listingForm.getImageFiles());
        ahora tienen que venir las units tambn
*/
        return new ModelAndView("redirect:/marketplace/my-listings");
    }


    @RequestMapping(value = "/products/{department}/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView product(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("replyForm") ReplyForm replyForm,
            @RequestParam(value = "requestError", required = false, defaultValue = "false") Boolean requestError
    ) {
        LOGGER.info("User arriving at '/products/"+ productId +"' ");
        ModelAndView mav = new ModelAndView("marketplace/views/product");
        mav.addObject("requestError", requestError);
        mav.addObject("questions", inqs.getInquiriesByProduct(productId));
        mav.addObject("product", prs.findProductById(productId).orElseThrow(() -> new NotFoundException("Product not found")));
        return mav;
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}/request", method = RequestMethod.POST)
    public ModelAndView buyProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("requestForm") RequestForm requestForm,
            final BindingResult bindingResult,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("replyForm") ReplyForm replyForm
    ) {
        LOGGER.info("User requesting product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'requestForm'");
            return product(productId, department, requestForm, new QuestionForm(), new ReplyForm(), true);
        }
        rqs.createRequest(sessionUtils.getLoggedUser().getUserId(), productId, requestForm.getRequestMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}/ask", method = RequestMethod.POST)
    public ModelAndView askProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("questionForm") QuestionForm questionForm,
            final BindingResult bindingResult,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("replyForm") ReplyForm replyForm
    ) {
        LOGGER.info("User asking on product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'questionForm'");
            return product(productId,department,  new RequestForm(), questionForm, new ReplyForm(),false);
        }
        inqs.createInquiry(sessionUtils.getLoggedUser().getUserId(), productId, questionForm.getQuestionMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}/reply", method = RequestMethod.POST)
    public ModelAndView replyInquiry(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("replyForm") ReplyForm replyForm,
            final BindingResult bindingResult,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("requestForm") RequestForm requestForm
    ) {
        LOGGER.info("User replying inquiry in product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'replyForm'");
            return product(productId, department, new RequestForm(), new QuestionForm(), replyForm,false);
        }
        inqs.replyInquiry(Long.parseLong(replyForm.getInquiryId()), replyForm.getReplyMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }
}
