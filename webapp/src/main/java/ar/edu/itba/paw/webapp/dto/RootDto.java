package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.webapp.controller.constants.Endpoint;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class RootDto {
    private RootLinks _links;


    public static RootDto createRootDto(UriInfo uriInfo) {
        final RootDto dto = new RootDto();

        RootLinks links = new RootLinks();

        String workersNeighborhoodId = String.valueOf(BaseNeighborhood.WORKERS.getId());
        String rejectedNeighborhoodId = String.valueOf(BaseNeighborhood.REJECTED.getId());

        UriBuilder rootUri = uriInfo.getBaseUriBuilder().path(Endpoint.API);
        UriBuilder affiliationsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.AFFILIATIONS);
        UriBuilder userRolesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USER_ROLES);
        UriBuilder neighborhoodsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS);
        UriBuilder workersNeighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(workersNeighborhoodId);
        UriBuilder rejectedNeighborhoodUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.NEIGHBORHOODS).path(rejectedNeighborhoodId);
        UriBuilder workersUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKERS);
        UriBuilder imagesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.IMAGES);
        UriBuilder usersUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.USERS);

        UriBuilder departmentsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.DEPARTMENTS);
        UriBuilder professionsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.PROFESSIONS);
        UriBuilder shiftsUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.SHIFTS);

        UriBuilder languagesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.LANGUAGES);
        UriBuilder postStatusesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.POST_STATUSES);
        UriBuilder productStatusesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.PRODUCT_STATUSES);
        UriBuilder requestStatusesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.REQUEST_STATUSES);
        UriBuilder transactionTypesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.TRANSACTION_TYPES);
        UriBuilder workerRolesUri = uriInfo.getBaseUriBuilder().path(Endpoint.API).path(Endpoint.WORKER_ROLES);

        links.setSelf(rootUri.build());

        links.setWorkersNeighborhood(workersNeighborhoodUri.build());
        links.setRejectedNeighborhood(rejectedNeighborhoodUri.build());

        links.setAffiliations(affiliationsUri.build());
        links.setNeighborhoods(neighborhoodsUri.build());
        links.setWorkers(workersUri.build());
        links.setImages(imagesUri.build());
        links.setUsers(usersUri.build());

        links.setDepartments(departmentsUri.build());
        links.setProfessions(professionsUri.build());
        links.setShifts(shiftsUri.build());

        links.setLanguages(languagesUri.clone().build());
        links.setEnglishLanguage(languagesUri.clone().path(String.valueOf(Language.ENGLISH.getId())).build());
        links.setSpanishLanguage(languagesUri.clone().path(String.valueOf(Language.SPANISH.getId())).build());

        links.setPostStatuses(postStatusesUri.build());
        links.setHotPostStatus(postStatusesUri.clone().path(String.valueOf(PostStatus.HOT.getId())).build());
        links.setTrendingPostStatus(postStatusesUri.clone().path(String.valueOf(PostStatus.TRENDING.getId())).build());
        links.setNonePostStatus(postStatusesUri.clone().path(String.valueOf(PostStatus.NONE.getId())).build());

        links.setAcceptedRequestStatus(requestStatusesUri.clone().path(String.valueOf(RequestStatus.ACCEPTED.getId())).build());
        links.setDeclinedRequestStatus(requestStatusesUri.clone().path(String.valueOf(RequestStatus.DECLINED.getId())).build());
        links.setRequestedRequestStatus(requestStatusesUri.clone().path(String.valueOf(RequestStatus.REQUESTED.getId())).build());

        links.setPurchaseTransactionType(transactionTypesUri.clone().path(String.valueOf(TransactionType.PURCHASE.getId())).build());
        links.setSaleTransactionType(transactionTypesUri.clone().path(String.valueOf(TransactionType.SALE.getId())).build());

        links.setBoughtProductStatus(productStatusesUri.clone().path(String.valueOf(ProductStatus.BOUGHT.getId())).build());
        links.setSellingProductStatus(productStatusesUri.clone().path(String.valueOf(ProductStatus.SELLING.getId())).build());
        links.setSoldProductStatus(productStatusesUri.clone().path(String.valueOf(ProductStatus.SOLD.getId())).build());

        links.setAdministratorUserRole(userRolesUri.clone().path(String.valueOf(UserRole.ADMINISTRATOR.getId())).build());
        links.setNeighborUserRole(userRolesUri.clone().path(String.valueOf(UserRole.NEIGHBOR.getId())).build());
        links.setUnverifiedNeighborUserRole(userRolesUri.clone().path(String.valueOf(UserRole.UNVERIFIED_NEIGHBOR.getId())).build());
        links.setRejectedUserRole(userRolesUri.clone().path(String.valueOf(UserRole.REJECTED.getId())).build());
        links.setWorkerUserRole(userRolesUri.clone().path(String.valueOf(UserRole.WORKER.getId())).build());
        links.setSuperAdministratorUserRole(userRolesUri.clone().path(String.valueOf(UserRole.SUPER_ADMINISTRATOR.getId())).build());

        links.setVerifiedWorkerRole(workerRolesUri.clone().path(String.valueOf(WorkerRole.VERIFIED_WORKER.getId())).build());
        links.setUnverifiedWorkerRole(workerRolesUri.clone().path(String.valueOf(WorkerRole.UNVERIFIED_WORKER.getId())).build());
        links.setRejectedWorkerRole(workerRolesUri.clone().path(String.valueOf(WorkerRole.REJECTED.getId())).build());

        dto.set_links(links);
        return dto;
    }

    public RootLinks get_links() {
        return _links;
    }

    public void set_links(RootLinks _links) {
        this._links = _links;
    }
}
