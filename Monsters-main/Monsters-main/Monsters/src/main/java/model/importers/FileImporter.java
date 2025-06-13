package model.importers;

import java.io.File;
import java.util.List;
import model.Monster;

/**
 *
 * @author tsyga
 */
public interface FileImporter {
    void setNext(FileImporter next);
    List<Monster> importFile(File file) throws Exception;
    boolean canHandle(File file);
}
