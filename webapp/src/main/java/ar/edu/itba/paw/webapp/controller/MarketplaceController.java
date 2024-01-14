package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Department;
import ar.edu.itba.paw.interfaces.exceptions.NotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Entities.Product;
import ar.edu.itba.paw.models.Entities.User;
import ar.edu.itba.paw.webapp.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/marketplace")
public class MarketplaceController extends GlobalControllerAdvice{
    private static final Logger LOGGER = LoggerFactory.getLogger(MarketplaceController.class);
    private final ProductService prs;
    private final RequestService rqs;
    private final InquiryService inqs;
    private final PurchaseService prchs;
    private final UserService us;


    @Autowired
    public MarketplaceController(final UserService us,
                            final ProductService prs,
                            final RequestService rqs,
                            final InquiryService inqs,
                            final PurchaseService prchs) {
        super(us);
        this.us = us;
        this.prs = prs;
        this.rqs = rqs;
        this.inqs = inqs;
        this.prchs = prchs;

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
//        List<Product> productList = prs.getProductsByCriteria(getLoggedUser().getNeighborhood().getNeighborhoodId(), Department.fromURLString(department) , page,size);
        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
//        mav.addObject("productList", productList);
        mav.addObject("channel", "Marketplace");
        mav.addObject("departmentList", Department.getDepartmentsWithUrls());
        mav.addObject("departmentName", Objects.requireNonNull(Department.fromURLString(department)).name());
        mav.addObject("page", page);
//        mav.addObject("totalPages", prs.getProductsTotalPages(getLoggedUser().getNeighborhood().getNeighborhoodId(), size, Department.fromURLString(department)));
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

//        List<Product> products = prs.getProductsBought(getLoggedUser().getUserId(), page, size);
        ModelAndView mav = new ModelAndView("marketplace/views/myPurchases");
//        mav.addObject("channel", "MyPurchases");
//        mav.addObject("purchases", prchs.getPurchasesByBuyerId(getLoggedUser().getUserId(), page, size));
//        mav.addObject("page", page);
//        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(getLoggedUser().getUserId(), size));
//        mav.addObject("contextPath", "/marketplace/my-purchases");
        return mav;
    }

    @RequestMapping(value = "/currently-requesting", method = RequestMethod.GET)
    public ModelAndView listingRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/currently-requesting'");

        ModelAndView mav = new ModelAndView("marketplace/views/myCurrentlyRequesting");
//        mav.addObject("channel", "CurrentlyRequesting");
//        mav.addObject("page", page);
//        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(getLoggedUser().getUserId(), size));
//        mav.addObject("contextPath", "/marketplace/currently-requesting");
//        mav.addObject("requestList", rqs.getRequestsByUserId(getLoggedUser().getUserId(), page, size));
        return mav;
    }

    @RequestMapping(value = "/my-sales", method = RequestMethod.GET)
    public ModelAndView mySales(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/my-sales'");

        ModelAndView mav = new ModelAndView("marketplace/views/mySales");
//        mav.addObject("purchases", prchs.getPurchasesBySellerId(getLoggedUser().getUserId(), page, size));
//        mav.addObject("channel", "MySales");
//        mav.addObject("page", page);
//        mav.addObject("totalPages", prs.getProductsSoldTotalPages(getLoggedUser().getUserId(), size));
//        mav.addObject("contextPath", "/marketplace/my-sales");
        return mav;
    }

    @PreAuthorize("@authService.canAccessProduct(principal, #productId)")
    @RequestMapping(value = "/my-requests/{productId:\\d+}", method = RequestMethod.GET)
    public ModelAndView listingRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @PathVariable(value = "productId") int productId,
            @ModelAttribute("markAsSoldForm") MarkAsSoldForm markAsSoldForm
    ) {
        LOGGER.info("User arriving at '/marketplace/my-requests/{}'", productId);

        ModelAndView mav = new ModelAndView("marketplace/views/listingRequests");
        mav.addObject("requestList", rqs.getRequestsByProductId(productId, page, size));
        mav.addObject("requests", prs.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")).getRequesters());
        mav.addObject("product", prs.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")));
        return mav;
    }

    @PreAuthorize("@authService.canAccessProduct(principal, #productId)")
    @RequestMapping(value = "/my-requests/{productId:\\d+}", method = RequestMethod.POST)
    public ModelAndView listingRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @PathVariable(value = "productId") int productId,
            @Valid @ModelAttribute("markAsSoldForm") MarkAsSoldForm markAsSoldForm,
            final BindingResult bindingResult
    ) {
        LOGGER.info("User arriving at '/marketplace/my-requests/{}' POST", productId);
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'markAsSoldForm'");
            return listingRequests(page, size, productId, markAsSoldForm);
        }
        prs.markAsBought(markAsSoldForm.getBuyerId(), productId, markAsSoldForm.getQuantity());
        rqs.markRequestAsFulfilled(markAsSoldForm.getRequestId());
        return new ModelAndView("redirect:/marketplace/my-sales");
    }


    @RequestMapping(value = "/my-listings", method = RequestMethod.GET)
    public ModelAndView myListings(
        @RequestParam (value = "page", required = false, defaultValue = "1") int page,
        @RequestParam (value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/my-listings'");

        ModelAndView mav = new ModelAndView("marketplace/views/myListings");

//        mav.addObject("myProductList", prs.getProductsSelling(getLoggedUser().getUserId(), page, size));
//        mav.addObject("channel", "MyListings");
//        mav.addObject("page", page);
//        mav.addObject("totalPages", prs.getProductsSellingTotalPages(getLoggedUser().getUserId(), size));
//        mav.addObject("contextPath", "/marketplace/my-listings");
        return mav;
    }

    @RequestMapping(value = "/create-listing", method = RequestMethod.GET)
    public ModelAndView createListingForm(
        @ModelAttribute("listingForm") ListingForm listingForm
    ) {
        LOGGER.info("User arriving at '/marketplace/create-publishing'");
        ModelAndView mav = new ModelAndView("marketplace/views/productSell");
        mav.addObject("channel", "Sell");
        mav.addObject("departmentList", Department.getDepartments());
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
        User user = getLoggedUser();
        prs.createProduct(user.getUserId(), listingForm.getTitle(), listingForm.getDescription(), listingForm.getPrice(), listingForm.getUsed(), listingForm.getDepartmentId() , listingForm.getImageFiles(), listingForm.getQuantity());
        return new ModelAndView("redirect:/marketplace/my-listings");
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView product(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("replyForm") ReplyForm replyForm,
            @ModelAttribute("phoneRequestForm") PhoneRequestForm phoneRequestForm,
            @RequestParam(value = "requestError", required = false, defaultValue = "false") Boolean requestError,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/products/"+ productId +"' ");
        ModelAndView mav = new ModelAndView("marketplace/views/product");
        mav.addObject("requestError", requestError);
        mav.addObject("questions", inqs.getInquiriesByProductAndCriteria(productId, page, size));
        mav.addObject("product", prs.findProductById(productId).orElseThrow(() -> new NotFoundException("Product not found")));
        mav.addObject("page", page);
        mav.addObject("totalPages", inqs.getTotalInquiryPages(productId, size));
        mav.addObject("contextPath", "/marketplace/products/" + department + "/" + productId);

        return mav;
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}/request", method = RequestMethod.POST)
    public ModelAndView requestProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("requestForm") RequestForm requestForm,
            final BindingResult bindingResult,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("replyForm") ReplyForm replyForm,
            @ModelAttribute("phoneRequestForm") PhoneRequestForm phoneRequestForm
    ) {
        LOGGER.info("User requesting product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'requestForm'");
            return product(productId, department, requestForm, new QuestionForm(), new ReplyForm(), new PhoneRequestForm(), true, 1, 10);
        }
        rqs.createRequest(getLoggedUser().getUserId(), productId, requestForm.getRequestMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}/first-request", method = RequestMethod.POST)
    public ModelAndView firstRequestProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("phoneRequestForm") PhoneRequestForm phoneRequestForm,
            final BindingResult bindingResult,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("replyForm") ReplyForm replyForm
    ) {
        LOGGER.info("User requesting product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'phoneRequestForm'");
            return product(productId, department, requestForm, new QuestionForm(), new ReplyForm(), phoneRequestForm, true, 1, 10);
        }
        us.updatePhoneNumber(getLoggedUser().getUserId(), phoneRequestForm.getPhoneNumber());
        rqs.createRequest(getLoggedUser().getUserId(), productId, phoneRequestForm.getPhoneRequestMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }

    @RequestMapping(value = "/products/{department}/{id:\\d+}/ask", method = RequestMethod.POST)
    public ModelAndView askProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("questionForm") QuestionForm questionForm,
            final BindingResult bindingResult,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("replyForm") ReplyForm replyForm,
            @ModelAttribute("phoneRequestForm") PhoneRequestForm phoneRequestForm
    ) {
        LOGGER.info("User asking on product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'questionForm'");
            return product(productId,department,  new RequestForm(), questionForm, new ReplyForm(), new PhoneRequestForm(), false,1, 10);
        }
        inqs.createInquiry(getLoggedUser().getUserId(), productId, questionForm.getQuestionMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }

    @PreAuthorize("@authService.canAccessProduct(principal, #productId)")
    @RequestMapping(value = "/products/{department}/{id:\\d+}/reply", method = RequestMethod.POST)
    public ModelAndView replyInquiry(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("replyForm") ReplyForm replyForm,
            final BindingResult bindingResult,
            @ModelAttribute("questionForm") QuestionForm questionForm,
            @ModelAttribute("requestForm") RequestForm requestForm,
            @ModelAttribute("phoneRequestForm") PhoneRequestForm phoneRequestForm
    ) {
        LOGGER.info("User replying inquiry in product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'replyForm'");
            return product(productId, department, new RequestForm(), new QuestionForm(), replyForm, new PhoneRequestForm(), false, 1, 10);
        }
        inqs.replyInquiry(Long.parseLong(replyForm.getInquiryId()), replyForm.getReplyMessage());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }

    @PreAuthorize("@authService.canAccessProduct(principal, #productId)")
    @RequestMapping(value = "/products/{department}/{id:\\d+}/edit", method = RequestMethod.GET)
    public ModelAndView editProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @ModelAttribute("listingForm") ListingForm listingForm
    ) {
        LOGGER.info("User arriving at '/marketplace/products/" + department + "/" + productId +"/edit'");
        ModelAndView mav = new ModelAndView("marketplace/views/productEdit");
        mav.addObject("departmentList", Department.getDepartments());
        mav.addObject("product", prs.findProductById(productId).orElseThrow(() -> new NotFoundException("Product not found")));
        return mav;
    }

    @PreAuthorize("@authService.canAccessProduct(principal, #productId)")
    @RequestMapping(value = "/products/{department}/{id:\\d+}/edit", method = RequestMethod.POST)
    public ModelAndView editProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @Valid @ModelAttribute("listingForm") ListingForm listingForm,
            final BindingResult bindingResult
    ) {
        LOGGER.info("User editing product '/"+ productId +"' ");
        if(bindingResult.hasErrors()){
            LOGGER.error("Error in form 'listingForm'");
            return product(productId, department, new RequestForm(), new QuestionForm(), new ReplyForm(), new PhoneRequestForm(), false,1, 10);
        }
        prs.updateProduct(productId, listingForm.getTitle(), listingForm.getDescription(), listingForm.getPrice(), listingForm.getUsed(), listingForm.getDepartmentId() , listingForm.getImageFiles(), listingForm.getQuantity());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }
}
