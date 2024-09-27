package gr.technico.technikon.resources;

import gr.technico.technikon.exceptions.CustomException;
import gr.technico.technikon.model.Owner;
import gr.technico.technikon.services.OwnerService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Path("/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class OwnerResource {

    @Inject
    private OwnerService ownerService;

    @POST
    @Path("/create")
    public Response createOwner(Owner owner) {
        try {
            ownerService.createOwner(
                    owner.getVat(),
                    owner.getName(),
                    owner.getSurname(),
                    owner.getAddress(),
                    owner.getPhoneNumber(),
                    owner.getEmail(),
                    owner.getUsername(),
                    owner.getPassword()
            );
            log.info("Owner created successfully with VAT: {}", owner.getVat());
            return Response.status(Response.Status.CREATED).entity("Owner created successfully").build();
        } catch (CustomException e) {
            log.error("Error creating owner: {}", e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{vat}")
    public Response searchOwnerByVat(@PathParam("vat") String vat) {
        log.debug("Searching for owner with VAT: {}", vat);
        Optional<Owner> owner = ownerService.searchOwnerByVat(vat);
        if (owner.isPresent()) {
            log.info("Owner found with VAT: {}", vat);
            return Response.ok(owner.get()).build();
        } else {
            log.warn("Owner not found with VAT: {}", vat);
            return Response.status(Response.Status.NOT_FOUND).entity("Owner not found").build();
        }
    }

    @GET
    @Path("/email/{email}")
    public Response searchOwnerByEmail(@PathParam("email") String email) {
        log.debug("Searching for owner with email: {}", email);
        Optional<Owner> owner = ownerService.searchOwnerByEmail(email);
        if (owner.isPresent()) {
            log.info("Owner found with email: {}", email);
            return Response.ok(owner.get()).build();
        } else {
            log.warn("Owner not found with email: {}", email);
            return Response.status(Response.Status.NOT_FOUND).entity("Owner not found").build();
        }
    }

    @PUT
    @Path("/{vat}/address")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateOwnerAddress(@PathParam("vat") String vat, Map<String, String> jsonMap) {
        log.debug("Updating address for owner with VAT: {}", vat);
        try {
            String address = jsonMap.get("address");
            if (address == null || address.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Address is missing or empty").build();
            }
            ownerService.updateOwnerAddress(vat, address);
            log.info("Owner address updated successfully for VAT: {}", vat);
            return Response.ok("Owner address updated successfully").build();
        } catch (CustomException e) {
            log.error("Failed to update address for VAT {}: {}", vat, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{vat}/email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateOwnerEmail(@PathParam("vat") String vat, Map<String, String> jsonMap) {
        log.debug("Updating email for owner with VAT: {}", vat);
        try {
            String email = jsonMap.get("email");
            if (email == null || email.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Email is missing or empty").build();
            }
            ownerService.updateOwnerEmail(vat, email);
            log.info("Owner email updated successfully for VAT: {}", vat);
            return Response.ok("Owner email updated successfully").build();
        } catch (CustomException e) {
            log.error("Failed to update email for VAT {}: {}", vat, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{vat}/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateOwnerPassword(@PathParam("vat") String vat, Map<String, String> jsonMap) {
        log.debug("Updating password for owner with VAT: {}", vat);
        try {
            String password = jsonMap.get("password");
            if (password == null || password.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Password is missing or empty").build();
            }
            ownerService.updateOwnerPassword(vat, password);
            log.info("Owner password updated successfully for VAT: {}", vat);
            return Response.ok("Owner password updated successfully").build();
        } catch (CustomException e) {
            log.error("Failed to update password for VAT {}: {}", vat, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{vat}")
    public Response deleteOwnerPermanently(@PathParam("vat") String vat) {
        log.debug("Deleting owner with VAT: {}", vat);
        boolean deleted = ownerService.deleteOwnerPermanently(vat);
        if (deleted) {
            log.info("Owner deleted successfully with VAT: {}", vat);
            return Response.ok("Owner deleted successfully").build();
        } else {
            log.warn("Failed to delete owner with VAT: {}", vat);
            return Response.status(Response.Status.NOT_FOUND).entity("Owner not found").build();
        }
    }

    @PUT
    @Path("/{vat}")
    public Response deleteOwnerSafely(@PathParam("vat") String vat) {
        log.debug("Soft deleting owner with VAT: {}", vat);
        boolean deleted = ownerService.deleteOwnerSafely(vat);
        if (deleted) {
            log.info("Owner marked as deleted with VAT: {}", vat);
            return Response.ok("Owner marked as deleted").build();
        } else {
            log.error("Failed to soft delete owner with VAT: {}", vat);
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to delete owner").build();
        }
    }

    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response authenticateOwner(Map<String, String> jsonMap) {
        String email = jsonMap.get("email");
        String password = jsonMap.get("password");

        log.debug("Authenticating owner with email: {}", email);
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Email or password is missing").build();
        }

        try {
            return ownerService.authenticateOwner(email, password)
                    .map(owner -> {
                        log.info("Authentication successful for email: {}", email);
                        return Response.ok(owner).build();
                    })
                    .orElseGet(() -> {
                        log.warn("Authentication failed for email: {}", email);
                        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
                    });
        } catch (CustomException e) {
            log.error("Authentication error for email: {}, {}", email, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

}
