package ar.edu.itba.paw.webapp.form;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class ReplyForm {

    @NotBlank
    private String inquiryId;

    @NotBlank
    @Size(max = 300)
    private String replyMessage;

    public String getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    @Override
    public String toString() {
        return "ReplyForm{" +
                "inquiryId=" + inquiryId +
                ", replyMessage='" + replyMessage + '\'' +
                '}';
    }
}
