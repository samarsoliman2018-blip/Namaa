package Service;

import Model.CashWaqf;
import Model.Waqif;
import Model.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;

public class WaqfService {
    private static ArrayList<CashWaqf> waqfs = new ArrayList<>();
    private static final String FILE = "cashwaqf.csv";
    private static int nextId = 1;
   

    static {
    	Class<?> userServiceClass = UserService.class;
        loadWaqfs();
        if (!waqfs.isEmpty()) {
            for (CashWaqf w : waqfs) {
                if (w.getWaqfID() >= nextId) {
                    nextId = w.getWaqfID() + 1;
                }
            }
        }
    }

    public static void createCashWaqf(CashWaqf waqf) {
        if (waqf.getWaqfID() == 0) {
            waqf.setWaqfID(nextId++);
        }
        waqfs.add(waqf);
        saveAllToFile();
    }

    public static ArrayList<CashWaqf> getAllWaqfs() {
        return new ArrayList<>(waqfs);
    }

    public static CashWaqf searchWaqf(int id) {
        for (CashWaqf w : waqfs) {
            if (w.getWaqfID() == id)
                return w;
        }
        return null;
    }

    public static boolean allocateFunding(int waqfID, double amount) {
        CashWaqf w = searchWaqf(waqfID);
        if (w == null) return false;
        
        boolean result = w.allocateFunding(amount);
        if (result) {
            saveAllToFile();
        }
        return result;
    }

    public static void receiveRepayment(int waqfID, double amount) {
        CashWaqf w = searchWaqf(waqfID);
        if (w != null) {
            w.receiveRepayment(amount);
            saveAllToFile();
        }
    }

    public static void addDonation(int waqfID, double amount) {
        CashWaqf w = searchWaqf(waqfID);
        if (w != null) {
            w.addDonation(amount);
            saveAllToFile();
        }
    }

    public static ArrayList<CashWaqf> getWaqfsByWaqif(int waqifId) {
        ArrayList<CashWaqf> result = new ArrayList<>();
        for (CashWaqf w : waqfs) {
            if (w.getWaqif() != null && w.getWaqif().getWaqifID() == waqifId) {
                result.add(w);
            }
        }
        return result;
    }

   

    public static int getNextId() {
        return nextId++;
    }

    public static void refreshWaqfs() {
        loadWaqfs();
        System.out.println("🔄 Waqf data refreshed. Total: " + waqfs.size());
    }

    // ===== FIXED: Add this public method =====
    public static void saveWaqfsToFile() {
        saveAllToFile();
        System.out.println("💾 Waqf data saved to file");
    }

    // ===== SAVE ALL: Rewrite entire file =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("WaqfID,WaqifID,WaqfAmount,AvailableBalance,CreationDate,Status,TotalImpactScore,NumberOfBeneficiaries,AverageRepaymentRate,WaqfPurpose\n");
            for (CashWaqf w : waqfs) {
                writer.write(
                    w.getWaqfID() + "," +
                    w.getWaqif().getWaqifID() + "," +
                    w.getWaqfAmount() + "," +
                    w.getAvailableBalance() + "," +
                    w.getCreationDate() + "," +
                    w.getStatus() + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + waqfs.size() + " waqfs to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD from CSV =====
 // In WaqfService.java - Replace the loadWaqfs() method

    private static void loadWaqfs() {
        File file = new File(FILE);
        if (!file.exists()) {
            System.out.println("⚠️ No cashwaqf.csv file found. Creating empty list.");
            return;
        }
        
        waqfs.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            boolean isFirstLine = true;
            
            System.out.println("📂 Loading waqfs from: " + file.getAbsolutePath());
            System.out.println("📂 File exists: " + file.exists() + " | Size: " + file.length() + " bytes");
            
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                lineCount++;
                if (line.trim().isEmpty()) continue;
                if (isFirstLine) {
                    System.out.println("📋 Header: " + line);
                    isFirstLine = false;
                    continue;
                }
                
                System.out.println("📋 Line " + lineCount + ": " + line);
                
                String[] data = line.split(",");
                if (data.length >= 6) {
                    try {
                        int waqfId = Integer.parseInt(data[0].trim());
                        int waqifId = Integer.parseInt(data[1].trim());
                        double waqfAmount = Double.parseDouble(data[2].trim());
                        double availableBalance = Double.parseDouble(data[3].trim());
                        LocalDate creationDate = LocalDate.parse(data[4].trim());
                        String status = data[5].trim();
                        
                        System.out.println("  🔍 Searching for Waqif ID: " + waqifId);
                        
                        // Try to find the Waqif
                        User user = UserService.searchUserById(waqifId);
                        Waqif waqif = null;
                        
                        if (user != null) {
                            System.out.println("  👤 Found user: " + user.getFullName() + " | Type: " + user.getClass().getSimpleName());
                            if (user instanceof Waqif) {
                                waqif = (Waqif) user;
                                System.out.println("  ✅ Valid Waqif found!");
                            } else {
                                System.err.println("  ⚠️ User ID " + waqifId + " is not a Waqif. It is: " + user.getClass().getSimpleName());
                                System.err.println("  💡 Creating a temporary Waqif for this ID");
                                // Create a temporary Waqif
                                waqif = new Waqif();
                                waqif.setWaqifID(waqifId);
                                waqif.setFullName("Waqif #" + waqifId);
                            }
                        } else {
                            System.err.println("  ❌ User ID " + waqifId + " not found in users.csv");
                            System.err.println("  💡 Creating a temporary Waqif for this ID");
                            waqif = new Waqif();
                            waqif.setWaqifID(waqifId);
                            waqif.setFullName("Waqif #" + waqifId);
                        }
                        
                        if (waqif != null) {
                            CashWaqf waqf = new CashWaqf(
                                waqfId,
                                waqif,
                                waqfAmount,
                                availableBalance,
                                creationDate,
                                status
                            );
                            waqfs.add(waqf);
                            System.out.println("  ✅ Loaded Waqf #" + waqfId + " | Amount: " + waqfAmount + " | Balance: " + availableBalance);
                        }
                        
                    } catch (Exception e) {
                        System.err.println("❌ Error parsing waqf line: " + line);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("❌ Invalid line format (expected 6 columns, got " + data.length + "): " + line);
                }
            }
            
            System.out.println("✅ Loaded " + waqfs.size() + " waqfs from CSV");
            if (waqfs.isEmpty()) {
                System.out.println("💡 No waqfs loaded. Possible reasons:");
                System.out.println("   - CSV file is empty or has no data rows");
                System.out.println("   - Waqif ID doesn't match any existing Waqif user");
                System.out.println("   - CSV format is incorrect");
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error loading waqfs: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
 
    
 // In WaqfService.java

    public static double getTotalWaqfAmount() {
        double total = 0;
        System.out.println("🔍 Calculating Total Waqf Amount:");
        for (CashWaqf w : waqfs) {
            System.out.println("  Waqf #" + w.getWaqfID() + " Amount: " + w.getWaqfAmount());
            total += w.getWaqfAmount();
        }
        System.out.println("  Total: " + total);
        return total;
    }

    public static double getTotalWaqfBalance() {
        double total = 0;
        System.out.println("🔍 Calculating Total Waqf Balance:");
        for (CashWaqf w : waqfs) {
            System.out.println("  Waqf #" + w.getWaqfID() + " Balance: " + w.getAvailableBalance());
            total += w.getAvailableBalance();
        }
        System.out.println("  Total: " + total);
        return total;
    }

    public static double getAllocatedFunds() {
        double amount = getTotalWaqfAmount();
        double balance = getTotalWaqfBalance();
        double allocated = amount - balance;
        System.out.println("🔍 Allocated Funds: " + amount + " - " + balance + " = " + allocated);
        return allocated;
    }
}