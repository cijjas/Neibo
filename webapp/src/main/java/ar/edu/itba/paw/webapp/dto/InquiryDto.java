package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;
import ar.edu.itba.paw.webapp.validation.URIValidator;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class InquiryDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 512)
    private String message;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = URIValidator.USER_URI_REGEX)
    private String user;

    @Size(max = 512)
    private String reply;

    private Date inquiryDate;

    private Links _links;

    public static InquiryDto fromInquiry(Inquiry inquiry, UriInfo uriInfo) {
        final InquiryDto dto = new InquiryDto();

        dto.message = inquiry.getMessage();
        dto.reply = inquiry.getReply();
        dto.inquiryDate = inquiry.getInquiryDate();

        Links links = new Links();

        String neighborhoodId = String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId());
        String productId = String.valueOf(inquiry.getProduct().getProductId());
        String inquiryUserId = String.valueOf(inquiry.getUser().getUserId());
        String replyUserId = String.valueOf(inquiry.getProduct().getSeller().getUserId());
        String inquiryId = String.valueOf(inquiry.getInquiryId());

        UriBuilder neighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(neighborhoodId);
        UriBuilder productUri = neighborhoodUri.clone().path(Endpoint.PRODUCTS).path(productId);
        UriBuilder inquiryUserUri = neighborhoodUri.clone().path(Endpoint.USERS).path(inquiryUserId);
        UriBuilder replyUserUri = neighborhoodUri.clone().path(Endpoint.USERS).path(replyUserId);
        UriBuilder inquiryUri = productUri.clone().path(Endpoint.INQUIRIES).path(inquiryId);

        links.setSelf(inquiryUri.build());
        links.setProduct(productUri.build());
        links.setInquiryUser(inquiryUserUri.build());
        if (inquiry.getReply() != null)
            links.setReplyUser(replyUserUri.build());

        dto.set_links(links);
        return dto;
    }


    public Date getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(Date inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
