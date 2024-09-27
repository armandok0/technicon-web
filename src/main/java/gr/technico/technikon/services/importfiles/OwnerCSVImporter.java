package gr.technico.technikon.services.importfiles;

import gr.technico.technikon.exceptions.CustomException;
import gr.technico.technikon.model.Owner;
import gr.technico.technikon.services.OwnerServiceImpl;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

@RequestScoped
public class OwnerCSVImporter implements FilesImporter {

    @Inject
    private OwnerServiceImpl ownerService;

    @Override
    public void importFile(String filePath) throws IOException, OutOfMemoryError, FileNotFoundException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                OwnerCSVImporter.class.getClassLoader().getResourceAsStream(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] fields = line.split(",");

                    if (fields.length != 9) {
                        //The line is malformed, skip it
                        continue;
                    }

                    String vat = fields[0];
                    String name = fields[1];
                    String surname = fields[2];
                    String address = fields[3];
                    String phoneNumber = fields[4];
                    String email = fields[5];
                    String username = fields[6];
                    String password = fields[7];
                    Owner.Role csvRole = Owner.Role.valueOf(fields[8]);

                    ownerService.createOwner(vat, name, surname, address, phoneNumber, email, username, password);

                    if (csvRole != Owner.Role.USER) {
                        ownerService.updateOwnerRole(vat, csvRole);
                    }
                } catch (CustomException e) {
                    System.out.println("Owner doesn't pass validations, skip this line" + e.getMessage());
                }
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
