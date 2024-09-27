package gr.technico.technikon.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.technico.technikon.exceptions.CustomException;
import gr.technico.technikon.model.Owner;
import gr.technico.technikon.model.Property;
import gr.technico.technikon.model.Repair;
import gr.technico.technikon.model.RepairType;
import gr.technico.technikon.services.OwnerService;
import gr.technico.technikon.services.PropertyService;
import gr.technico.technikon.services.RepairService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Path("/repairs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class RepairResource {

    @Inject
    private RepairService repairService;

    @POST
    @Path("/create")
    public Response createRepair(Map<String, Object> jsonMap) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Property property = mapper.convertValue(jsonMap.get("property"), Property.class);

            String repairTypeString = (String) jsonMap.get("repairType");
            RepairType repairType = RepairType.valueOf(repairTypeString.toUpperCase());

            String shortDescription = (String) jsonMap.get("shortDescription");
            String description = (String) jsonMap.get("description");

            Repair repair = repairService.createRepair(repairType, shortDescription, description, property);

            return Response.status(Response.Status.CREATED).entity(repair).build();
        } catch (CustomException | IllegalArgumentException e) {
            log.error("Error creating repair: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/repairType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateRepairType(@PathParam("id") Long id, Map<String, String> jsonMap) {
        try {
            Optional<Repair> repairOpt = repairService.findRepairById(id);

            if (repairOpt.isEmpty()) {
                log.warn("No repair found with ID: {}", id);
                return Response.status(Response.Status.NOT_FOUND).entity("Repair not found for ID: " + id).build();
            }

            String repairTypeString = jsonMap.get("repairType");
            RepairType repairType = RepairType.valueOf(repairTypeString.toUpperCase());

            repairService.updateRepairType(id, repairType);
            log.info("Repair type updated successfully for ID: {}", id);

            return Response.ok("Repair type updated successfully").build();
        } catch (IllegalArgumentException e) {
            log.error("Error updating repair type for ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid repair type: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}/shortDescription")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateShortDescription(@PathParam("id") Long id, Map<String, String> jsonMap) {
        try {
            String shortDescription = jsonMap.get("shortDescription");

            repairService.updshortDesc(id, shortDescription);
            log.info("Short description updated successfully for ID: {}", id);

            return Response.ok("Short description updated successfully").build();
        } catch (Exception e) {
            log.error("Error updating short description for ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating short description").build();
        }
    }

    @PUT
    @Path("/{id}/description")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateDescription(@PathParam("id") Long id, Map<String, String> jsonMap) {
        try {
            String description = jsonMap.get("description");

            repairService.updDesc(id, description);
            log.info("Description updated successfully for Repair ID: {}", id);

            return Response.ok("Description updated successfully").build();
        } catch (Exception e) {
            log.error("Error updating description for Repair ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating description").build();
        }
    }

    @PUT
    @Path("/{id}/costAndDates")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCostAndDates(@PathParam("id") Long id, Map<String, Object> jsonMap) {
        try {
            BigDecimal proposedCost;
            try {
                proposedCost = new BigDecimal(jsonMap.get("proposedCost").toString());
            } catch (NumberFormatException e) {
                log.error("Invalid proposed cost format: {}", e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid proposed cost format").build();
            }

            Date proposedStartDate = new Date((Long) jsonMap.get("proposedStartDate"));
            Date proposedEndDate = new Date((Long) jsonMap.get("proposedEndDate"));

            repairService.updCostDates(id, proposedCost, proposedStartDate, proposedEndDate);
            log.info("Cost and dates updated successfully for Repair ID: {}", id);

            return Response.ok("Cost and dates updated successfully").build();
        } catch (Exception e) {
            log.error("Error updating cost and dates for Repair ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating cost and dates").build();
        }
    }

    @PUT
    @Path("/{id}/acceptanceStatus")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateAcceptance(@PathParam("id") Long id, Map<String, Integer> jsonMap) {
        try {
            Integer response = jsonMap.get("response");

            if (response == null || (response != 0 && response != 1)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid response value. Must be 1 for acceptance or 0 for decline.").build();
            }
            repairService.updAcceptance(id, response);
            log.info("Acceptance status updated successfully for Repair ID: {}", id);

            return Response.ok("Acceptance status updated successfully").build();
        } catch (Exception e) {
            log.error("Error updating acceptance status for Repair ID {}: {}", id, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating acceptance status").build();
        }
    }

    @PUT
    @Path("/{id}/statusInprogress")
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateStatus(@PathParam("id") Long id) {
        try {
            repairService.updateStatus(id);
            log.info("Status updated to INPROGRESS for Repair ID: {}", id);

            return Response.ok("Status updated to INPROGRESS successfully").build();
        } catch (Exception e) {
            log.error("Unexpected error updating status for Repair ID {}: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating status").build();
        }
    }

    @PUT
    @Path("/{id}/statusComplete")
    @Produces(MediaType.TEXT_PLAIN)
    public Response completeRepair(@PathParam("id") Long id) {
        try {
            repairService.updComplete(id);
            log.info("Repair ID {} marked as complete", id);
            return Response.ok("Repair marked as complete successfully").build();
        } catch (Exception e) {
            log.error("Unexpected error completing repair with ID {}: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error marking repair as complete").build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRepairs() {
        try {
            List<Repair> repairs = repairService.getRepairs();
            return Response.ok(repairs).build();
        } catch (CustomException e) {
            log.error("Error retrieving repairs: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Unexpected error retrieving repairs: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving repairs").build();
        }
    }

    @GET
    @Path("/pending")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingRepairs() {
        try {
            List<Repair> pendingRepairs = repairService.getPendingRepairs();
            return Response.ok(pendingRepairs).build();
        } catch (CustomException e) {
            log.error("Error retrieving pending repairs: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Unexpected error retrieving pending repairs: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving pending repairs").build();
        }
    }

    @Inject
    private OwnerService ownerService;

    @GET
    @Path("/pending/owner/{vat}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPendingRepairsByOwner(@PathParam("vat") String vat) {
        try {
            Optional<Owner> optionalOwner = ownerService.searchOwnerByVat(vat);
            if (optionalOwner.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Owner not found").build();
            }

            Owner owner = optionalOwner.get();
            List<Repair> repairs = repairService.getPendingRepairsByOwner(owner);

            if (repairs.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("No pending repairs found for this owner.").build();
            }

            return Response.ok(repairs).build();
        } catch (CustomException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred").build();
        }
    }

    @GET
    @Path("/inProgress")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInProgressRepairs() {
        try {
            List<Repair> repairs = repairService.getInProgressRepairs();
            return Response.ok(repairs).build();
        } catch (CustomException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred").build();
        }
    }

    @GET
    @Path("/accepted")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAcceptedRepairs() {
        try {
            List<Repair> repairs = repairService.getAcceptedRepairs();
            return Response.ok(repairs).build();
        } catch (CustomException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred").build();
        }
    }

    @GET
    @Path("owner/{vat}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepairsByOwner(@PathParam("vat") String vat) {
        try {
            Optional<Owner> optionalOwner = ownerService.searchOwnerByVat(vat);
            if (optionalOwner.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Owner not found").build();
            }

            Owner owner = optionalOwner.get();
            List<Repair> repairs = repairService.findRepairsByOwner(owner);

            if (repairs.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("No repairs found for this owner.").build();
            }

            return Response.ok(repairs).build();
        } catch (CustomException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred").build();
        }
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepairById(@PathParam("id") Long id) {
        try {
            Optional<Repair> optionalRepair = repairService.findRepairById(id);

            if (optionalRepair.isPresent()) {
                return Response.ok(optionalRepair.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Repair not found for ID: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteRepairSafely(@PathParam("id") Long id) {
        try {
            boolean deleted = repairService.deleteSafely(id);
            if (deleted) {
                return Response.ok()
                        .entity("Repair with ID " + id + " has been successfully marked as deleted.").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Repair not found for ID: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRepairPermanently(@PathParam("id") Long id) {
        try {
            boolean deleted = repairService.deletePermantlyById(id);
            if (deleted) {
                return Response.ok()
                        .entity("Repair with ID " + id + " has been permanently deleted.").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Repair not found for ID: " + id).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    @Inject
    private PropertyService propertyService;

    @GET
    @Path("/property/{propertyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepairsByPropertyId(@PathParam("propertyId") Long propertyId) {
        try {
            Property property = propertyService.findByID(propertyId);
            if (property == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Property not found for ID: " + propertyId).build();
            }

            List<Repair> repairs = repairService.getRepairByPropertyId(property);

            if (repairs.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("No repairs found for this property.").build();
            }

            return Response.ok(repairs).build();
        } catch (CustomException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/datesRange")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepairsByRangeOfDates(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate, @QueryParam("vat") String vat) {
        try {
            LocalDate startLocalDate;
            LocalDate endLocalDate;
            try {
                startLocalDate = LocalDate.parse(startDate);
                endLocalDate = LocalDate.parse(endDate);
            } catch (DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid date format. Use YYYY-MM-DD.").build();
            }

            Optional<Owner> ownerOptional = ownerService.searchOwnerByVat(vat);
            if (ownerOptional.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Owner not found").build();
            }

            Owner owner = ownerOptional.get();

            List<Repair> repairs = repairService.findRepairsByRangeOfDates(startDate, endDate, owner);
            if (repairs.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("No repairs found in the specified date range.").build();
            }

            return Response.ok(repairs).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/date")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRepairsByDate(@QueryParam("date") String date, @QueryParam("vat") String vat) {
        try {
            Optional<Owner> ownerOptional = ownerService.searchOwnerByVat(vat);
            if (ownerOptional.isEmpty()) {
                log.warn("Owner not found for VAT: {}", vat);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Owner not found for VAT: " + vat).build();
            }

            Owner owner = ownerOptional.get();

            List<Repair> repairs = repairService.findRepairsByDate(date, owner);
            if (repairs.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("No repairs found for the specified date.").build();
            }

            return Response.ok(repairs).build();

        } catch (DateTimeParseException e) {
            log.error("Invalid date format for date: {}", date, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid date format. Use YYYY-MM-DD.").build();
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage()).build();
        }
    }
}