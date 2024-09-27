package gr.technico.technikon.services.importfiles;

import gr.technico.technikon.model.Property;
import gr.technico.technikon.model.Repair;
import gr.technico.technikon.model.RepairStatus;
import gr.technico.technikon.model.RepairType;
import gr.technico.technikon.repositories.PropertyRepository;
import gr.technico.technikon.repositories.RepairRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@RequestScoped
public class RepairCSVImporter implements FilesImporter {

    @Inject
    private PropertyRepository propertyRepository;

    @Inject
    private RepairRepository repairRepository;

    @Override
    public void importFile(String filePath) throws IOException, OutOfMemoryError, FileNotFoundException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                PropertyCSVImporter.class.getClassLoader().getResourceAsStream(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                if (!(fields.length >= 10 && fields.length <= 12)) {
                    continue;
                }

                Long propertyId;
                try {
                    propertyId = Long.parseLong(fields[0]);
                } catch (NumberFormatException e) {
                    continue;
                }

                RepairType repairType;
                try {
                    repairType = RepairType.valueOf(fields[1]);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                String shortDiscription = fields[2];

                LocalDateTime submitionDateLocal;
                try {
                    submitionDateLocal = LocalDateTime.parse(fields[3], formatter);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                String description = fields[4];
                LocalDateTime proposedStartDateLocal;
                try {
                    proposedStartDateLocal = LocalDateTime.parse(fields[5], formatter);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                LocalDateTime proposedEndDateLocal;
                try {
                    proposedEndDateLocal = LocalDateTime.parse(fields[6], formatter);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                BigDecimal proposedCost;
                try {
                    proposedCost = new BigDecimal(fields[7]);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                boolean acceptanceStatus;
                try {
                    acceptanceStatus = Boolean.parseBoolean(fields[8]);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                RepairStatus repairStatus;
                try {
                    repairStatus = RepairStatus.valueOf(fields[9]);
                } catch (IllegalArgumentException e) {
                    continue;
                }

                LocalDateTime actualStartDateLocal = null;
                if (fields.length >= 11) {
                    try {
                        actualStartDateLocal = LocalDateTime.parse(fields[10], formatter);
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                }

                LocalDateTime actualEndDateLocal = null;
                if (fields.length == 12) {
                    try {
                        actualEndDateLocal = LocalDateTime.parse(fields[11], formatter);
                    } catch (IllegalArgumentException e) {

                        continue;
                    }
                }

                if (repairRepository.findExistingRepair(propertyId, submitionDateLocal).isPresent()) {
                    continue;
                }

                Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
                if (!propertyOptional.isPresent()) {
                    continue;
                }

                Property property = propertyOptional.get();

                Repair repair = new Repair();
                repair.setProperty(property);
                repair.setRepairType(repairType);
                repair.setShortDescription(shortDiscription);

                Date submitionDate = Date.from(submitionDateLocal.atZone(ZoneId.systemDefault()).toInstant());
                Date proposedStartDate = Date.from(proposedStartDateLocal.atZone(ZoneId.systemDefault()).toInstant());
                Date proposedEndDate = Date.from(proposedEndDateLocal.atZone(ZoneId.systemDefault()).toInstant());
                Date actualStartDate = actualStartDateLocal != null ? Date.from(actualStartDateLocal.atZone(ZoneId.systemDefault()).toInstant()) : null;
                Date actualEndDate = actualEndDateLocal != null ? Date.from(actualEndDateLocal.atZone(ZoneId.systemDefault()).toInstant()) : null;

                repair.setSubmissionDate(submitionDate);
                repair.setDescription(description);
                repair.setProposedStartDate(proposedStartDate);
                repair.setProposedEndDate(proposedEndDate);
                repair.setProposedCost(proposedCost);
                repair.setAcceptanceStatus(acceptanceStatus);
                repair.setRepairStatus(repairStatus);
                repair.setActualStartDate(actualStartDate);
                repair.setActualEndDate(actualEndDate);

                repairRepository.save(repair);
            }

        } catch (OutOfMemoryError e) {
            System.out.println("Java run out of memory: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Filepath not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
