package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Entities.Inquiry;
import ar.edu.itba.paw.webapp.validation.constraints.UserURNReferenceConstraintCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnCreate;
import ar.edu.itba.paw.webapp.validation.groups.OnUpdate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

public class InquiryDto {

    @NotNull(groups = OnCreate.class)
    @Size(max = 500, groups = OnCreate.class)
    private String questionMessage;

    @NotNull(groups = OnCreate.class)
    @UserURNReferenceConstraintCreate(groups = OnCreate.class)
    private String user;

    @NotNull(groups = OnUpdate.class)
    @Size(max = 500, groups = OnUpdate.class)
    private String replyMessage;

    private Date inquiryDate;

    private Links _links;

    public static InquiryDto fromInquiry(Inquiry inquiry, UriInfo uriInfo) {
        final InquiryDto dto = new InquiryDto();

        dto.questionMessage = inquiry.getMessage();
        dto.replyMessage = inquiry.getReply();
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
        links.setUser(uriInfo.getBaseUriBuilder()
                .path("neighborhoods")
                .path(String.valueOf(inquiry.getUser().getNeighborhood().getNeighborhoodId()))
                .path("users")
                .path(String.valueOf(inquiry.getUser().getUserId()))
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

    public String getQuestionMessage() {
        return questionMessage;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public void setQuestionMessage(String questionMessage) {
        this.questionMessage = questionMessage;
    }
}
