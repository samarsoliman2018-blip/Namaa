package Service;

import Model.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.*;

public class AdviceService {
    private static ArrayList<AdviceTraining> advices = new ArrayList<>();
    private static final String FILE = "advice.csv";
    private static int nextId = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    static {
        loadAdvice();
        if (!advices.isEmpty()) {
            for (AdviceTraining a : advices) {
                if (a.getTrainingId() >= nextId) {
                    nextId = a.getTrainingId() + 1;
                }
            }
        }
    }

    public static void sendAdvice(AdviceTraining advice) {
        if (advice.getTrainingId() == 0) {
            advice.setTrainingId(nextId++);
        }
        if (advice.getSentDate() == null) {
            advice.setSentDate(LocalDate.now());
        }
        advices.add(advice);
        saveAllToFile();
    }

    public static void sendAdviceToBeneficiary(int beneficiaryId, String title, String content, String type) {
        Beneficiary beneficiary = (Beneficiary) UserService.searchUserById(beneficiaryId);
        if (beneficiary == null) {
            System.err.println("Beneficiary not found with ID: " + beneficiaryId);
            return;
        }

        AdviceTraining advice = new AdviceTraining();
        advice.setTrainingId(nextId++);
        advice.setBeneficiary(beneficiary);
        advice.setTitle(title);
        advice.setContent(content);
        advice.setType(type);
        advice.setSentDate(LocalDate.now());
        advice.setRead(false);
        
        advices.add(advice);
        saveAllToFile();
        System.out.println("Advice sent to: " + beneficiary.getFullName());
    }

    public static void sendTrainingToBeneficiary(int beneficiaryId, String title, String content, String videoUrl) {
        Beneficiary beneficiary = (Beneficiary) UserService.searchUserById(beneficiaryId);
        if (beneficiary == null) {
            System.err.println("Beneficiary not found with ID: " + beneficiaryId);
            return;
        }

        AdviceTraining advice = new AdviceTraining();
        advice.setTrainingId(nextId++);
        advice.setBeneficiary(beneficiary);
        advice.setTitle(title);
        advice.setContent(content);
        advice.setType("Training");
        advice.setSentDate(LocalDate.now());
        advice.setRead(false);
        advice.setVideoUrl(videoUrl);
        
        advices.add(advice);
        saveAllToFile();
        System.out.println("Training sent to: " + beneficiary.getFullName());
    }

    public static ArrayList<AdviceTraining> getAdvice() {
        return new ArrayList<>(advices);
    }

    public static ArrayList<AdviceTraining> getAdviceForBeneficiary(int beneficiaryId) {
        ArrayList<AdviceTraining> result = new ArrayList<>();
        for (AdviceTraining a : advices) {
            if (a.getBeneficiary() != null && 
                a.getBeneficiary().getBeneficiaryID() == beneficiaryId) {
                result.add(a);
            }
        }
        return result;
    }

    public static ArrayList<AdviceTraining> getUnreadAdvice(int beneficiaryId) {
        ArrayList<AdviceTraining> result = new ArrayList<>();
        for (AdviceTraining a : advices) {
            if (a.getBeneficiary() != null && 
                a.getBeneficiary().getBeneficiaryID() == beneficiaryId && 
                !a.isRead()) {
                result.add(a);
            }
        }
        return result;
    }

    public static int getUnreadCount(int beneficiaryId) {
        int count = 0;
        for (AdviceTraining a : advices) {
            if (a.getBeneficiary() != null && 
                a.getBeneficiary().getBeneficiaryID() == beneficiaryId && 
                !a.isRead()) {
                count++;
            }
        }
        return count;
    }

    public static void markAsRead(int adviceId) {
        for (AdviceTraining a : advices) {
            if (a.getTrainingId() == adviceId) {
                a.setRead(true);
                saveAllToFile();
                return;
            }
        }
    }

    public static void markAllAsRead(int beneficiaryId) {
        boolean updated = false;
        for (AdviceTraining a : advices) {
            if (a.getBeneficiary() != null && 
                a.getBeneficiary().getBeneficiaryID() == beneficiaryId && 
                !a.isRead()) {
                a.setRead(true);
                updated = true;
            }
        }
        if (updated) {
            saveAllToFile();
        }
    }

    public static ArrayList<AdviceTraining> getAdviceByType(String type) {
        ArrayList<AdviceTraining> result = new ArrayList<>();
        for (AdviceTraining a : advices) {
            if (a.getType().equalsIgnoreCase(type)) {
                result.add(a);
            }
        }
        return result;
    }

    public static ArrayList<AdviceTraining> getAdviceByDateRange(LocalDate startDate, LocalDate endDate) {
        ArrayList<AdviceTraining> result = new ArrayList<>();
        for (AdviceTraining a : advices) {
            if (a.getSentDate() != null && 
                !a.getSentDate().isBefore(startDate) && 
                !a.getSentDate().isAfter(endDate)) {
                result.add(a);
            }
        }
        return result;
    }

    public static void deleteAdvice(int adviceId) {
        advices.removeIf(a -> a.getTrainingId() == adviceId);
        saveAllToFile();
    }

    public static void deleteAllAdviceForBeneficiary(int beneficiaryId) {
        advices.removeIf(a -> a.getBeneficiary() != null && 
                           a.getBeneficiary().getBeneficiaryID() == beneficiaryId);
        saveAllToFile();
    }

    public static AdviceTraining getAdviceById(int id) {
        for (AdviceTraining a : advices) {
            if (a.getTrainingId() == id) {
                return a;
            }
        }
        return null;
    }

    public static int getNextId() {
        return nextId++;
    }

    // ===== SAVE ALL =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("AdviceID,BeneficiaryID,Title,Content,Type,SentDate,IsRead,VideoURL,AttachmentURL\n");
            
            for (AdviceTraining a : advices) {
                writer.write(
                    a.getTrainingId() + "," +
                    (a.getBeneficiary() != null ? a.getBeneficiary().getBeneficiaryID() : "") + "," +
                    escapeCSV(a.getTitle()) + "," +
                    escapeCSV(a.getContent()) + "," +
                    a.getType() + "," +
                    (a.getSentDate() != null ? a.getSentDate().format(DATE_FORMATTER) : "") + "," +
                    a.isRead() + "," +
                    (a.getVideoUrl() != null ? escapeCSV(a.getVideoUrl()) : "") + "," +
                    (a.getAttachmentUrl() != null ? escapeCSV(a.getAttachmentUrl()) : "") + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + advices.size() + " advice/training entries to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD =====
    private static void loadAdvice() {
        File file = new File(FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] data = line.split(",", -1); // Keep empty fields
                if (data.length >= 9) {
                    try {
                        int adviceId = Integer.parseInt(data[0].trim());
                        int beneficiaryId = data[1].trim().isEmpty() ? 0 : Integer.parseInt(data[1].trim());
                        String title = data[2].trim();
                        String content = data[3].trim();
                        String type = data[4].trim();
                        LocalDate sentDate = data[5].trim().isEmpty() ? null : LocalDate.parse(data[5].trim(), DATE_FORMATTER);
                        boolean isRead = Boolean.parseBoolean(data[6].trim());
                        String videoUrl = data[7].trim();
                        String attachmentUrl = data[8].trim();
                        
                        if (beneficiaryId > 0) {
                            Beneficiary beneficiary = (Beneficiary) UserService.searchUserById(beneficiaryId);
                            
                            if (beneficiary != null) {
                                AdviceTraining advice = new AdviceTraining();
                                advice.setTrainingId(adviceId);
                                advice.setBeneficiary(beneficiary);
                                advice.setTitle(title);
                                advice.setContent(content);
                                advice.setType(type);
                                advice.setSentDate(sentDate);
                                advice.setRead(isRead);
                                advice.setVideoUrl(videoUrl);
                                advice.setAttachmentUrl(attachmentUrl);
                                advices.add(advice);
                            } else {
                                System.err.println("Beneficiary not found for ID: " + beneficiaryId);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing advice line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + advices.size() + " advice/training entries from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== CSV HELPER =====
    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}