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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
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

    @Autowired
    public MarketplaceController(final UserService us,
                            final ProductService prs,
                            final RequestService rqs,
                            final InquiryService inqs
    ) {
        super(us);
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
        List<Product> productList = prs.getProductsByCriteria(getLoggedUser().getNeighborhood().getNeighborhoodId(), Department.fromURLString(department) , page,size);
        ModelAndView mav = new ModelAndView("marketplace/views/marketplace");
        mav.addObject("productList", productList);
        mav.addObject("channel", "Marketplace");
        mav.addObject("departmentList", Department.getDepartmentsWithUrls());
        mav.addObject("departmentName", Objects.requireNonNull(Department.fromURLString(department)).name());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsTotalPages(getLoggedUser().getNeighborhood().getNeighborhoodId(), size, Department.fromURLString(department)));
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

        List<Product> products = prs.getProductsBought(getLoggedUser().getUserId(), page, size);

        ModelAndView mav = new ModelAndView("marketplace/views/myPurchases");
        mav.addObject("channel", "MyPurchases");
        mav.addObject("products", products);
        mav.addObject("loggedUser", getLoggedUser());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/my-purchases");
        return mav;
    }

    @RequestMapping(value = "/currently-requesting", method = RequestMethod.GET)
    public ModelAndView listingRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/currently-requesting'");

        ModelAndView mav = new ModelAndView("marketplace/views/currentlyRequesting");
        mav.addObject("channel", "CurrentlyRequesting");
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/currently-requesting");
        mav.addObject("requestList", getLoggedUser().getRequestedProducts());
        return mav;
    }

    @RequestMapping(value = "/my-sales", method = RequestMethod.GET)
    public ModelAndView mySales(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        LOGGER.info("User arriving at '/marketplace/my-sales'");

        ModelAndView mav = new ModelAndView("marketplace/views/mySales");
        mav.addObject("products", prs.getProductsSold(getLoggedUser().getUserId(), page, size));
        mav.addObject("channel", "MySales");
        mav.addObject("loggedUser", getLoggedUser());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsSoldTotalPages(getLoggedUser().getUserId(), size));
        mav.addObject("contextPath", "/marketplace/my-sales");
        return mav;
    }



    @RequestMapping(value = "/my-requests/{productId:\\d+}", method = RequestMethod.GET)
    public ModelAndView listingRequests(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @PathVariable(value = "productId") int productId
    ) {
        LOGGER.info("User arriving at '/marketplace/my-requests/{}'", productId);

        ModelAndView mav = new ModelAndView("marketplace/views/listingRequests");
        mav.addObject("requestList", rqs.getRequestsByProductId(productId, page, size));
        System.out.println("HOLA" + rqs.getRequestsByProductId(productId, 1, 10));
        mav.addObject("requests", prs.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")).getRequesters());
        mav.addObject("product", prs.findProductById(productId).orElseThrow(()-> new NotFoundException("Product Not Found")));
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

        mav.addObject("totalPages", prs.getProductsBoughtTotalPages(getLoggedUser().getUserId(), size));
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

        mav.addObject("myProductList", prs.getProductsSelling(getLoggedUser().getUserId(), page, size));
        mav.addObject("channel", "MyListings");
        mav.addObject("loggedUser", getLoggedUser());
        mav.addObject("page", page);
        mav.addObject("totalPages", prs.getProductsSellingTotalPages(getLoggedUser().getUserId(), size));
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
        mav.addObject("loggedUser", getLoggedUser());
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
        rqs.createRequest(getLoggedUser().getUserId(), productId, requestForm.getRequestMessage());
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

    @PreAuthorize("@authService.canAccessProduct(principal, #productId)")
    @RequestMapping(value = "/products/{department}/{id:\\d+}/edit", method = RequestMethod.GET)
    public ModelAndView editProduct(
            @PathVariable(value = "id") Long productId,
            @PathVariable(value = "department") String department,
            @ModelAttribute("listingForm") ListingForm listingForm
    ) {
        LOGGER.info("User arriving at '/marketplace/products/" + department + "/" + productId +"/edit'");
        System.out.println("ARRIVED HERE");
        ModelAndView mav = new ModelAndView("marketplace/views/productEdit");
        mav.addObject("departmentList", Department.getDepartments());
        mav.addObject("loggedUser", getLoggedUser());
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
            return product(productId, department, new RequestForm(), new QuestionForm(), new ReplyForm(),false);
        }
        prs.updateProduct(productId, listingForm.getTitle(), listingForm.getDescription(), listingForm.getPrice(), listingForm.getUsed(), listingForm.getDepartmentId() , listingForm.getImageFiles(), listingForm.getQuantity());
        return new ModelAndView("redirect:/marketplace/products/" + department + "/" + productId);
    }
}
