package gr.technico.technikon.services.importfiles;

import jakarta.enterprise.context.RequestScoped;
import java.io.FileNotFoundException;
import java.io.IOException;

@RequestScoped
public interface FilesImporter {

    void importFile(String filePath) throws IOException, OutOfMemoryError, FileNotFoundException;
}