import gr.technico.technikon.jpa.JpaUtil;
import gr.technico.technikon.model.Property;
import gr.technico.technikon.model.Repair;
import gr.technico.technikon.model.RepairStatus;
import gr.technico.technikon.model.RepairType;
import gr.technico.technikon.repositories.PropertyRepository;
import gr.technico.technikon.repositories.RepairRepository;
import gr.technico.technikon.services.importfiles.FilesImporter;
import gr.technico.technikon.services.importfiles.PropertyCSVImporter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class RepairCSVImporter implements FilesImporter {

    @Override
    public void importFile(String filePath) throws IOException, OutOfMemoryError, FileNotFoundException {

        PropertyRepository propertyRepository = new PropertyRepository(JpaUtil.getEntityManager());
        RepairRepository repairRepository = new RepairRepository(JpaUtil.getEntityManager());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                PropertyCSVImporter.class.getClassLoader().getResourceAsStream(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                if (!(fields.length >= 10 && fields.length <= 12)) {
                    // The line is malformed, skip it
                    continue;
                }

                Long propertyId;
                try {
                    propertyId = Long.parseLong(fields[0]);
                } catch (NumberFormatException e) {
                    // Invalid CSV format: propertyId must be Long, skip line
                    continue;
                }

                RepairType repairType;
                try {
                    repairType = RepairType.valueOf(fields[1]);
                } catch (IllegalArgumentException e) {
                    // Invalid CSV format: invalid repair type, skip line
                    continue;
                }

                String shortDiscription = fields[2];

                Date submitionDate;
                try {
                    submitionDate = formatter.parse(fields[3]);
                } catch (ParseException e) {
                    // Invalid CSV format: invalid date format, skip line
                    continue;
                }

                String description = fields[4];
                Date proposedStartDate;
                try {
                    proposedStartDate = formatter.parse(fields[5]);
                } catch (ParseException e) {
                    // Invalid CSV format: invalid date format, skip line
                    continue;
                }

                Date proposedEndDate;
                try {
                    proposedEndDate = formatter.parse(fields[6]);
                } catch (ParseException e) {
                    // Invalid CSV format: invalid date format, skip line
                    continue;
                }

                BigDecimal proposedCost;
                try {
                    proposedCost = new BigDecimal(fields[7]);
                } catch (IllegalArgumentException e) {
                    // Invalid CSV format: invalid cost format, skip line
                    continue;
                }

                boolean acceptanceStatus;
                try {
                    acceptanceStatus = Boolean.parseBoolean(fields[8]);
                } catch (IllegalArgumentException e) {
                    // Invalid CSV format: invalid status format, skip line
                    continue;
                }

                RepairStatus repairStatus;
                try {
                    repairStatus = RepairStatus.valueOf(fields[9]);
                } catch (IllegalArgumentException e) {
                    // Invalid CSV format: invalid repair status, skip line
                    continue;
                }

                Date actualStartDate = null;
                if (fields.length >= 11) {
                    try {
                        actualStartDate = formatter.parse(fields[10]);
                    } catch (ParseException e) {
                        // Invalid CSV format: invalid date format, skip line
                        continue;
                    }
                }

                Date actualEndDate = null;
                if (fields.length == 12) {
                    try {
                        actualEndDate = formatter.parse(fields[11]);
                    } catch (ParseException e) {
                        // Invalid CSV format: invalid date format, skip line
                        continue;
                    }
                }

                Optional<Property> propertyOptional = propertyRepository.findById(propertyId);
                if (!propertyOptional.isPresent()) {
                    // Property not found, skip line
                    continue;
                }
                Property property = propertyOptional.get();

                Repair repair = new Repair();
                repair.setProperty(property);
                repair.setRepairType(repairType);
                repair.setShortDescription(shortDiscription);
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
            System.out.println("Java ran out of memory: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Filepath not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
