package gr.technico.technikon.startup;

import gr.technico.technikon.services.importfiles.OwnerCSVImporter;
import gr.technico.technikon.services.importfiles.PropertyCSVImporter;
import gr.technico.technikon.services.importfiles.RepairCSVImporter;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Startup
@Slf4j
public class DatabaseInitialize {

    @Inject
    private OwnerCSVImporter ownerCSVImporter;

    @Inject
    private PropertyCSVImporter propertyCSVImporter;

    @Inject
    private RepairCSVImporter repairCSVImporter;

    @PostConstruct
    public void init() {
        log.info("Starting CSV import process");
        try {
            ownerCSVImporter.importFile("csv_files/owners.csv");
            propertyCSVImporter.importFile("csv_files/properties.csv");
            repairCSVImporter.importFile("csv_files/repairs.csv");
        } catch (IOException e) {
            log.error("IO Exception during import: {}", e.getMessage(), e);
            throw new EJBException("Import failed due to an IO Exception", e);
        } catch (Exception e) {
            log.error("Exception during CSV import: {}", e.getMessage(), e);
            throw new EJBException("Import failed", e);
        } finally {
            log.info("CSV import process completed");
        }
    }
}
