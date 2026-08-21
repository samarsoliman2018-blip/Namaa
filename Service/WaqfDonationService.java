package Service;

import Model.CashWaqf;
import Model.WaqfDonation;
import Model.Waqif;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.*;

public class WaqfDonationService {
    private static ArrayList<WaqfDonation> donations = new ArrayList<>();
    private static final String FILE = "donations.csv";
    private static int nextId = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    static {
        loadDonations();
        if (!donations.isEmpty()) {
            for (WaqfDonation d : donations) {
                if (d.getDonationId() >= nextId) {
                    nextId = d.getDonationId() + 1;
                }
            }
        }
    }

    public static void donate(WaqfDonation donation) {
        if (donation.getDonationId() == 0) {
            donation.setDonationId(nextId++);
        }
        if (donation.getDonationDate() == null) {
            donation.setDonationDate(LocalDate.now());
        }
        if (donation.getPaymentMethod() == null || donation.getPaymentMethod().isEmpty()) {
            donation.setPaymentMethod("Bank Transfer");
        }
        
        donations.add(donation);
        saveAllToFile();
        
        // Update Waqf balance
        CashWaqf waqf = donation.getCashWaqf();
        if (waqf != null) {
            waqf.addDonation(donation.getAmount());
            WaqfService.addDonation(waqf.getWaqfID(), donation.getAmount());
        }
        
        // Update Waqif total
        Waqif waqif = donation.getWaqif();
        if (waqif != null) {
            waqif.setTotalWaqfAmount(waqif.getTotalWaqfAmount() + donation.getAmount());
            UserService.updateUser(waqif);
        }
    }

    public static void donate(int waqifId, int waqfId, double amount, String paymentMethod, String transactionId) {
        Waqif waqif = (Waqif) UserService.searchUserById(waqifId);
        CashWaqf waqf = WaqfService.searchWaqf(waqfId);
        
        if (waqif == null) {
            System.err.println("Waqif not found with ID: " + waqifId);
            return;
        }
        if (waqf == null) {
            System.err.println("Waqf not found with ID: " + waqfId);
            return;
        }
        if (amount <= 0) {
            System.err.println("Amount must be greater than 0");
            return;
        }

        WaqfDonation donation = new WaqfDonation();
        donation.setDonationId(nextId++);
        donation.setWaqif(waqif);
        donation.setCashWaqf(waqf);
        donation.setAmount(amount);
        donation.setDonationDate(LocalDate.now());
        donation.setPaymentMethod(paymentMethod != null ? paymentMethod : "Bank Transfer");
        donation.setTransactionId(transactionId);
        
        donations.add(donation);
        saveAllToFile();
        
        // Update Waqf balance
        waqf.addDonation(amount);
        WaqfService.addDonation(waqfId, amount);
        
        // Update Waqif total
        waqif.setTotalWaqfAmount(waqif.getTotalWaqfAmount() + amount);
        UserService.updateUser(waqif);
        
        System.out.println("Donation of " + amount + " made by " + waqif.getFullName() + 
                          " to Waqf #" + waqfId);
    }

    public static ArrayList<WaqfDonation> getDonations() {
        return new ArrayList<>(donations);
    }

    public static ArrayList<WaqfDonation> getDonationsByWaqif(int waqifId) {
        ArrayList<WaqfDonation> result = new ArrayList<>();
        for (WaqfDonation d : donations) {
            if (d.getWaqif() != null && d.getWaqif().getWaqifID() == waqifId) {
                result.add(d);
            }
        }
        return result;
    }

    public static ArrayList<WaqfDonation> getDonationsByWaqf(int waqfId) {
        ArrayList<WaqfDonation> result = new ArrayList<>();
        for (WaqfDonation d : donations) {
            if (d.getCashWaqf() != null && d.getCashWaqf().getWaqfID() == waqfId) {
                result.add(d);
            }
        }
        return result;
    }

    public static ArrayList<WaqfDonation> getDonationsByDateRange(LocalDate startDate, LocalDate endDate) {
        ArrayList<WaqfDonation> result = new ArrayList<>();
        for (WaqfDonation d : donations) {
            if (d.getDonationDate() != null && 
                !d.getDonationDate().isBefore(startDate) && 
                !d.getDonationDate().isAfter(endDate)) {
                result.add(d);
            }
        }
        return result;
    }

    public static double getTotalDonationsByWaqif(int waqifId) {
        double total = 0;
        for (WaqfDonation d : donations) {
            if (d.getWaqif() != null && d.getWaqif().getWaqifID() == waqifId) {
                total += d.getAmount();
            }
        }
        return total;
    }

    public static double getTotalDonationsByWaqf(int waqfId) {
        double total = 0;
        for (WaqfDonation d : donations) {
            if (d.getCashWaqf() != null && d.getCashWaqf().getWaqfID() == waqfId) {
                total += d.getAmount();
            }
        }
        return total;
    }

    public static double getTotalDonations() {
        double total = 0;
        for (WaqfDonation d : donations) {
            total += d.getAmount();
        }
        return total;
    }

    public static int getDonationCountByWaqif(int waqifId) {
        int count = 0;
        for (WaqfDonation d : donations) {
            if (d.getWaqif() != null && d.getWaqif().getWaqifID() == waqifId) {
                count++;
            }
        }
        return count;
    }

    public static WaqfDonation getDonationById(int id) {
        for (WaqfDonation d : donations) {
            if (d.getDonationId() == id) {
                return d;
            }
        }
        return null;
    }

    public static void deleteDonation(int id) {
        donations.removeIf(d -> d.getDonationId() == id);
        saveAllToFile();
    }

    public static void deleteDonationsByWaqif(int waqifId) {
        donations.removeIf(d -> d.getWaqif() != null && d.getWaqif().getWaqifID() == waqifId);
        saveAllToFile();
    }

    public static int getNextId() {
        return nextId++;
    }

    // ===== SAVE ALL =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("DonationID,WaqifID,WaqfID,Amount,DonationDate,PaymentMethod,TransactionID\n");
            
            for (WaqfDonation d : donations) {
                writer.write(
                    d.getDonationId() + "," +
                    (d.getWaqif() != null ? d.getWaqif().getWaqifID() : "") + "," +
                    (d.getCashWaqf() != null ? d.getCashWaqf().getWaqfID() : "") + "," +
                    d.getAmount() + "," +
                    (d.getDonationDate() != null ? d.getDonationDate().format(DATE_FORMATTER) : "") + "," +
                    (d.getPaymentMethod() != null ? d.getPaymentMethod() : "") + "," +
                    (d.getTransactionId() != null ? d.getTransactionId() : "") + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + donations.size() + " donations to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD =====
    private static void loadDonations() {
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
                
                String[] data = line.split(",", -1);
                if (data.length >= 7) {
                    try {
                        int donationId = Integer.parseInt(data[0].trim());
                        int waqifId = data[1].trim().isEmpty() ? 0 : Integer.parseInt(data[1].trim());
                        int waqfId = data[2].trim().isEmpty() ? 0 : Integer.parseInt(data[2].trim());
                        double amount = Double.parseDouble(data[3].trim());
                        LocalDate donationDate = data[4].trim().isEmpty() ? null : LocalDate.parse(data[4].trim(), DATE_FORMATTER);
                        String paymentMethod = data[5].trim();
                        String transactionId = data[6].trim();
                        
                        if (waqifId > 0 && waqfId > 0) {
                            Waqif waqif = (Waqif) UserService.searchUserById(waqifId);
                            CashWaqf waqf = WaqfService.searchWaqf(waqfId);
                            
                            if (waqif != null && waqf != null) {
                                WaqfDonation donation = new WaqfDonation();
                                donation.setDonationId(donationId);
                                donation.setWaqif(waqif);
                                donation.setCashWaqf(waqf);
                                donation.setAmount(amount);
                                donation.setDonationDate(donationDate);
                                donation.setPaymentMethod(paymentMethod);
                                donation.setTransactionId(transactionId);
                                donations.add(donation);
                            } else {
                                System.err.println("Waqif or Waqf not found for donation: " + donationId);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing donation line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + donations.size() + " donations from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}