package gr.technico.technikon.resources;

import gr.technico.technikon.exceptions.CustomException;
import gr.technico.technikon.model.Property;
import gr.technico.technikon.model.PropertyType;
import gr.technico.technikon.services.PropertyService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Path("/properties")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PropertyResource {

    @Inject
    private PropertyService propertyService;

    @POST
    @Path("/create")
    public Response createProperty(Map<String, Object> jsonMap) {
        try {
            String e9 = (String) jsonMap.get("e9");
            String address = (String) jsonMap.get("address");
            int year = (Integer) jsonMap.get("year");
            PropertyType propertyType = PropertyType.valueOf((String) jsonMap.get("propertyType"));
            String vat = (String) jsonMap.get("vat");

            Property property = propertyService.createProperty(e9, address, year, propertyType, vat);
            log.info("Property created successfully with E9: {}", e9);
            return Response.status(Response.Status.CREATED).entity(property).build();
        } catch (CustomException | IllegalArgumentException e) {
            log.error("Error creating property: {}", e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{e9}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response searchPropertyByE9(@PathParam("e9") String e9) {
        log.debug("Searching for property with E9: {}", e9);
        try {
            Property property = propertyService.findByE9(e9);
            log.info("Property found with E9: {}", e9);
            return Response.ok(property).build();
        } catch (CustomException e) {
            log.warn("Property not found with E9: {}", e9);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/vat/{vat}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response searchPropertyByVAT(@PathParam("vat") String vat) {
        log.debug("Searching for properties with VAT: {}", vat);
        try {
            List<Property> properties = propertyService.findByVAT(vat);
            log.info("Properties found for VAT: {}", vat);
            return Response.ok(properties).build();
        } catch (CustomException e) {
            log.warn("Properties not found for VAT: {}", vat);
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{e9}/address")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePropertyAddress(@PathParam("e9") String e9, Map<String, String> jsonMap) {
        log.debug("Updating address for property with E9: {}", e9);
        try {
            String address = jsonMap.get("address");
            Property property = propertyService.findByE9(e9);
            propertyService.updatePropertyAddress(property, address);
            log.info("Property address updated successfully for E9: {}", e9);
            return Response.ok("Property address updated successfully").build();
        } catch (CustomException e) {
            log.error("Failed to update address for E9 {}: {}", e9, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{e9}/e9")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePropertyE9(@PathParam("e9") String e9, Map<String, String> jsonMap) {
        log.debug("Received request to update E9 for property with current E9: {}", e9);
        try {
            String newE9 = jsonMap.get("newE9");
            Property property = propertyService.findByE9(e9);
            Property updatedProperty = propertyService.updatePropertyE9(property, newE9);
            log.debug("Successfully updated property with new E9: {}", updatedProperty.getE9());
            return Response.ok("Property e9 updated successfully").build();

        } catch (CustomException e) {
            log.error("Failed to update E9 for property with current E9 {}: {}", e9, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{e9}/PropertyConstructionYear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePropertyConstructionYear(@PathParam("e9") String e9, Map<String, Integer> jsonMap) {
        log.debug("Updating construction year for property with E9: {}", e9);
        try {
            int year = jsonMap.get("year");
            Property property = propertyService.findByE9(e9);
            Property updatedProperty = propertyService.updatePropertyConstructionYear(property, year);
            log.info("Property construction year updated successfully for E9: {}", e9);
            return Response.ok("Property construction year updated successfully").build();
        } catch (CustomException e) {
            log.error("Failed to update construction year for E9 {}: {}", e9, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{e9}/PropertyType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updatePropertyType(@PathParam("e9") String e9, Map<String, String> jsonMap) {
        log.debug("Updating property type for property with E9: {}", e9);
        try {
            PropertyType propertyType = PropertyType.valueOf(jsonMap.get("propertyType"));
            Property property = propertyService.findByE9(e9);
            Property updatedProperty = propertyService.updatePropertyType(property, propertyType);
            log.info("Property type updated successfully for E9: {}", e9);
            return Response.ok("Property type updated successfully").build();
        } catch (CustomException | IllegalArgumentException e) {
            log.error("Failed to update property type for E9 {}: {}", e9, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deletePropertyPermanently(@PathParam("id") Long id) {
        log.debug("Deleting property with ID: {}", id);
        try {
            boolean deleted = propertyService.permenantlyDeleteByID(id);
            if (deleted) {
                log.info("Property deleted successfully with ID: {}", id);
                return Response.ok("Property deleted successfully").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Property not found").build();
            }
        } catch (CustomException e) {
            log.error("Failed to delete property with ID {}: {}", id, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response softDeleteProperty(@PathParam("id") Long id) {
        log.debug("Soft deleting property with ID: {}", id);
        try {
            boolean deleted = propertyService.safelyDeleteByID(id);
            if (deleted) {
                log.info("Property marked as deleted with ID: {}", id);
                return Response.ok("Property marked as deleted").build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Failed to mark property as deleted").build();
            }
        } catch (CustomException e) {
            log.error("Failed to soft delete property with ID {}: {}", id, e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
