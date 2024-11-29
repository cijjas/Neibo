package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.TransactionType;

import javax.ws.rs.core.UriInfo;

public class TransactionTypeDto {

    private TransactionType type;

    private Links _links;

    public static TransactionTypeDto fromTransactionType(TransactionType transactionType, UriInfo uriInfo) {
        final TransactionTypeDto dto = new TransactionTypeDto();

        dto.type = transactionType;

        Links links = new Links();
        links.setSelf(uriInfo.getBaseUriBuilder()
                .path("transaction-types")
                .path(String.valueOf(transactionType.getId()))
                .build());
        dto.set_links(links);
        return dto;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }


    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }
}
