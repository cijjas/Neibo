package ar.edu.itba.paw.models;

public class ErrorDetail {

    private String field;
    private String hint;

    public ErrorDetail() {
    }

    public ErrorDetail(String field, String hint) {
        this.field = field;
        this.hint = hint;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
