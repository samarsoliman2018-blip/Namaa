package Service;

import Model.CashWaqf;
import Model.WaqfCondition;
import java.util.ArrayList;
import java.io.*;

public class WaqfConditionService {
    private static ArrayList<WaqfCondition> conditions = new ArrayList<>();
    private static final String FILE = "waqf_conditions.csv";

    static {
        loadConditions();
    }

    // ===== ADD CONDITION =====
    public static void addCondition(WaqfCondition condition) {
        conditions.add(condition);
        saveAllToFile();
    }

    // ===== GET CONDITION BY WAQF ID =====
    public static WaqfCondition getConditionByWaqfId(int waqfId) {
        for (WaqfCondition c : conditions) {
            if (c.getCashWaqf().getWaqfID() == waqfId) {
                return c;
            }
        }
        return null;
    }

    // ===== GET ALL CONDITIONS =====
    public static ArrayList<WaqfCondition> getConditions() {
        return new ArrayList<>(conditions);
    }

    // ===== UPDATE CONDITION =====
    public static void updateCondition(WaqfCondition condition) {
        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.get(i).getConditionID() == condition.getConditionID()) {
                conditions.set(i, condition);
                saveAllToFile();
                return;
            }
        }
    }

    // ===== DELETE CONDITION =====
    public static void deleteCondition(int conditionId) {
        conditions.removeIf(c -> c.getConditionID() == conditionId);
        saveAllToFile();
    }

    // ===== SAVE ALL =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("ConditionID,WaqfID,AllowedSector,MaximumFunding,MinimumPRI,TargetBeneficiaries\n");
            
            for (WaqfCondition c : conditions) {
                writer.write(
                    c.getConditionID() + "," +
                    c.getCashWaqf().getWaqfID() + "," +
                    c.getAllowedSector() + "," +
                    c.getMaximumFunding() + "," +
                    c.getMinimumPRI() + "," +
                    c.getTargetBeneficiaries() + "\n"
                );
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD CONDITIONS =====
    private static void loadConditions() {
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
                
                String[] data = line.split(",");
                if (data.length >= 6) {
                    try {
                        int conditionId = Integer.parseInt(data[0].trim());
                        int waqfId = Integer.parseInt(data[1].trim());
                        String sector = data[2].trim();
                        double maxFunding = Double.parseDouble(data[3].trim());
                        int minPRI = Integer.parseInt(data[4].trim());
                        String targetGroup = data[5].trim();
                        
                        CashWaqf waqf = WaqfService.searchWaqf(waqfId);
                        if (waqf != null) {
                            WaqfCondition condition = new WaqfCondition(
                                conditionId,
                                waqf,
                                sector,
                                maxFunding,
                                minPRI,
                                targetGroup
                            );
                            conditions.add(condition);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing condition line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + conditions.size() + " Waqf conditions");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}