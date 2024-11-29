package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.validation.constraints.authorization.UserURNCreateReferenceConstraint;
import ar.edu.itba.paw.webapp.validation.constraints.form.UserURNConstraint;
import ar.edu.itba.paw.webapp.validation.groups.Authorization;
import ar.edu.itba.paw.webapp.validation.groups.Basic;
import ar.edu.itba.paw.webapp.validation.groups.Null;
import ar.edu.itba.paw.webapp.validation.groups.URN;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class InquiryDto {

    @NotNull(groups = Null.class)
    @Size(max = 500, groups = Basic.class)
    private String message;

    @NotNull(groups = Null.class)
    @UserURNConstraint(groups = URN.class)
    @UserURNCreateReferenceConstraint(groups = Authorization.class)
    private String user;

    @Size(max = 500, groups = Basic.class)
    private String reply;

    private Date inquiryDate;

    private Links _links;

    public static InquiryDto fromInquiry(Inquiry inquiry, UriInfo uriInfo) {
        final InquiryDto dto = new InquiryDto();

        dto.message = inquiry.getMessage();
        dto.reply = inquiry.getReply();
        dto.inquiryDate = inquiry.getInquiryDate();

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(inquiry.getProduct().getProductId()))
                .path("inquiries")
                .path(String.valueOf(inquiry.getInquiryId()))
                .build());
        links.setProduct(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("products")
                .path(String.valueOf(inquiry.getProduct().getProductId()))
                .build());
        links.setInquiryUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(inquiry.getUser().getUserId()))
                .build());
        if (inquiry.getReply() != null)
            links.setReplyUser(uriInfo.getBaseUriBuilder()
                    .path("neighborhoods")
                    .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                    .path("users")
                    .path(String.valueOf(inquiry.getProduct().getSeller().getUserId()))
                    .build());
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
