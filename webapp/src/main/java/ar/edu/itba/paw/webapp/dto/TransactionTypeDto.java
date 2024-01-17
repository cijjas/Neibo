package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.ShiftStatus;
import ar.edu.itba.paw.enums.TransactionType;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class TransactionTypeDto {

    private TransactionType transactionType;

    private URI self;

    public static TransactionTypeDto fromTransactionType(TransactionType transactionType, UriInfo uriInfo){
        final TransactionTypeDto dto = new TransactionTypeDto();

        dto.transactionType = transactionType;

        dto.self = uriInfo.getBaseUriBuilder()
                .path("transaction-types")
                .path(String.valueOf(transactionType.getId()))
                .build();

        return dto;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
