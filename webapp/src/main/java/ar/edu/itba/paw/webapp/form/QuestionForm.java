package ar.edu.itba.paw.webapp.form;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class QuestionForm {

    @NotBlank
    @Size(max = 500)
    private String questionMessage;


    public String getQuestionMessage() {
        return questionMessage;
    }

    public void setQuestionMessage(String questionMessage) {
        this.questionMessage = questionMessage;
    }

    @Override
    public String toString() {
        return "RequestForm{" +
                ", questionMessage='" + questionMessage + '\'' +
                '}';
    }
}
