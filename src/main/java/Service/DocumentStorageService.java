package Service;

import java.io.*;
import java.nio.file.*;
import Model.FundingApplication;

public class DocumentStorageService {
    private static final String DOCUMENTS_DIR = "documents";
    private static final String APPLICATIONS_DIR = DOCUMENTS_DIR + File.separator + "applications";
    
    static {
        createDirectories();
    }
    
    private static void createDirectories() {
        try {
            File dir = new File(APPLICATIONS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("📁 Created documents directory: " + dir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to create documents directory: " + e.getMessage());
        }
    }
    
    public static String getApplicationFolder(int applicationId) {
        String folder = APPLICATIONS_DIR + File.separator + "app_" + applicationId;
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return folder;
    }
    
    public static String saveDocument(int applicationId, File sourceFile, String documentType) {
        try {
            String folder = getApplicationFolder(applicationId);
            String originalName = sourceFile.getName();
            
            // Get file extension
            String extension = "";
            int dotIndex = originalName.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalName.substring(dotIndex);
            }
            
            // Create unique filename
            String timestamp = String.valueOf(System.currentTimeMillis());
            String newFileName = documentType + "_" + timestamp + extension;
            
            Path sourcePath = sourceFile.toPath();
            Path targetPath = Paths.get(folder, newFileName);
            
            // Copy the file
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            System.out.println("✅ Document saved: " + targetPath.toString());
            return newFileName;
            
        } catch (Exception e) {
            System.err.println("❌ Failed to save document: " + e.getMessage());
            return null;
        }
    }
    
    public static String getDocumentPath(int applicationId, String fileName) {
        if (fileName == null || fileName.isEmpty()) return null;
        String folder = getApplicationFolder(applicationId);
        File file = new File(folder, fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;
    }
    
    public static String[] listDocuments(int applicationId) {
        String folder = getApplicationFolder(applicationId);
        File dir = new File(folder);
        if (dir.exists()) {
            return dir.list();
        }
        return new String[0];
    }
}