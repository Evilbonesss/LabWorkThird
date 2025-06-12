/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
