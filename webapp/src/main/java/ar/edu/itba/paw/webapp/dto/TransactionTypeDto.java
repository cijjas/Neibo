package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.Endpoint;
import ar.edu.itba.paw.enums.TransactionType;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class TransactionTypeDto {

    private TransactionType type;

    private Links _links;

    public static TransactionTypeDto fromTransactionType(TransactionType transactionType, UriInfo uriInfo) {
        final TransactionTypeDto dto = new TransactionTypeDto();

        dto.type = transactionType;

        Links links = new Links();

        String transactionTypeId = String.valueOf(transactionType.getId());

        UriBuilder transactionTypeUri = uriInfo.getBaseUriBuilder().path(Endpoint.TRANSACTION_TYPES.toString()).path(transactionTypeId);

        links.setSelf(transactionTypeUri.build());

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
