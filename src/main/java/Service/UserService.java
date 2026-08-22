package Service;

import Model.*;
import java.util.ArrayList;
import java.io.*;

public class UserService {
    private static ArrayList<User> users = new ArrayList<>();
    private static final String FILE = "users.csv";
    private static int nextId = 1;

    static {
        loadUsers();
        if (!users.isEmpty()) {
            for (User u : users) {
                if (u.getUserID() >= nextId) {
                    nextId = u.getUserID() + 1;
                }
            }
        }
        // Add default users only if file is empty
        if (users.isEmpty()) {
            addDefaultUsers();
        }
    }

    private static void addDefaultUsers() {
        // Administrator
        users.add(new Administrator(1, "System Administrator", "admin@namaa.com", 
                "admin", "1234", "30000000", 1));
        
        // Waqif (Donor)
        users.add(new Waqif(2, "Ahmed Ali", "ahmed@gmail.com", 
                "waqif", "1234", "31111111", 101, 500000));
        
        // ===== BENEFICIARIES WITH TARGET GROUPS =====
        users.add(new Beneficiary(3, "Mohammed Hassan", "moh@gmail.com", 
                "beneficiary", "1234", "32222222", 201, "Bachelor", 4, "Farmers"));
        
        users.add(new Beneficiary(4, "Sarah Ahmed", "sarah@gmail.com", 
                "sarah", "1234", "34444444", 202, "Master", 8, "Women"));
        
        users.add(new Beneficiary(5, "Khalid Omar", "khalid@gmail.com", 
                "khalid", "1234", "35555555", 203, "PhD", 12, "Entrepreneurs"));
        
        // ===== ADD MORE BENEFICIARIES FOR COMPLETE COVERAGE =====
        users.add(new Beneficiary(6, "Fatima Al-Hassan", "fatima@gmail.com", 
                "fatima", "1234", "36666666", 204, "Bachelor", 3, "Youth"));
        
        users.add(new Beneficiary(7, "Omar Al-Saud", "omar@gmail.com", 
                "omar", "1234", "37777777", 205, "Diploma", 6, "Small Business Owners"));
        
        users.add(new Beneficiary(8, "Layla Al-Qahtani", "layla@gmail.com", 
                "layla", "1234", "38888888", 206, "Master", 10, "Community Leaders"));
        
        users.add(new Beneficiary(9, "Abdullah Al-Fahd", "abdullah@gmail.com", 
                "abdullah", "1234", "39999999", 207, "Bachelor", 2, "Students"));
        
        users.add(new Beneficiary(10, "Nora Al-Saud", "nora@gmail.com", 
                "nora", "1234", "31111112", 208, "PhD", 5, "Artisans"));
        
        // Committee Member
        users.add(new CommitteeMember(11, "Dr. Ahmed Al-Zahrani", "dr.ahmed@namaa.com", 
                "dr_ahmed", "1234", "32222223", 301, "Finance"));
        
        // Save to file
        saveAllToFile();
    }

    public static User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static void addUser(User user) {
        if (user.getUserID() == 0) {
            user.setUserID(nextId++);
        }
        users.add(user);
        saveAllToFile();
    }

    public static User searchUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username))
                return user;
        }
        return null;
    }

    public static User searchUserById(int id) {
        for (User user : users) {
            if (user.getUserID() == id)
                return user;
        }
        return null;
    }

    public static void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == user.getUserID()) {
                users.set(i, user);
                saveAllToFile();
                return;
            }
        }
    }

    public static void deleteUser(int id) {
        users.removeIf(u -> u.getUserID() == id);
        saveAllToFile();
    }

    public static ArrayList<User> getUsers() {
        return new ArrayList<>(users);
    }

    // ===== ROLE-BASED FILTERING =====
    public static ArrayList<Waqif> getAllWaqifs() {
        ArrayList<Waqif> waqifs = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Waqif) {
                waqifs.add((Waqif) user);
            }
        }
        return waqifs;
    }

    public static ArrayList<Beneficiary> getAllBeneficiaries() {
        ArrayList<Beneficiary> beneficiaries = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Beneficiary) {
                beneficiaries.add((Beneficiary) user);
            }
        }
        return beneficiaries;
    }

    public static ArrayList<Administrator> getAllAdmins() {
        ArrayList<Administrator> admins = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Administrator) {
                admins.add((Administrator) user);
            }
        }
        return admins;
    }

    public static ArrayList<CommitteeMember> getAllCommitteeMembers() {
        ArrayList<CommitteeMember> members = new ArrayList<>();
        for (User user : users) {
            if (user instanceof CommitteeMember) {
                members.add((CommitteeMember) user);
            }
        }
        return members;
    }

    public static int getNextId() {
        return nextId++;
    }

    // ===== SAVE ALL: Rewrite entire file =====
 // In UserService.java - saveAllToFile()

    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            // ===== FIXED: Added Education, Experience, TargetGroup columns =====
            writer.write("UserID,FullName,Email,Username,Password,PhoneNumber,UserType,Education,Experience,TargetGroup,Specialization\n");
            
            for (User user : users) {
                String type = user.getClass().getSimpleName();
                
                StringBuilder line = new StringBuilder();
                line.append(user.getUserID()).append(",");
                line.append(user.getFullName()).append(",");
                line.append(user.getEmail()).append(",");
                line.append(user.getUsername()).append(",");
                line.append(user.getPassword()).append(",");
                line.append(user.getPhoneNumber()).append(",");
                line.append(type);
                
                // ===== FIXED: Add extra fields based on user type =====
                if (user instanceof Beneficiary) {
                    Beneficiary ben = (Beneficiary) user;
                    line.append(",").append(ben.getEducation() != null ? ben.getEducation() : "");
                    line.append(",").append(ben.getExperienceYears());
                    line.append(",").append(ben.getTargetGroup() != null ? ben.getTargetGroup() : "");
                    line.append(","); // Specialization (empty for beneficiary)
                } else if (user instanceof Waqif) {
                    line.append(",,"); // Empty for Education, Experience
                    line.append(","); // Empty for TargetGroup
                    line.append(","); // Empty for Specialization
                } else if (user instanceof CommitteeMember) {
                    CommitteeMember cm = (CommitteeMember) user;
                    line.append(",,"); // Empty for Education, Experience
                    line.append(","); // Empty for TargetGroup
                    line.append(",").append(cm.getSpecialization() != null ? cm.getSpecialization() : "");
                } else if (user instanceof Administrator) {
                    line.append(",,"); // Empty for Education, Experience
                    line.append(","); // Empty for TargetGroup
                    line.append(","); // Empty for Specialization
                }
                
                writer.write(line.toString() + "\n");
            }
            writer.flush();
            System.out.println("Saved " + users.size() + " users to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
 // ===== LOAD from CSV =====
 // In UserService.java - loadUsers()

    private static void loadUsers() {
        File file = new File(FILE);
        if (!file.exists()) return;
        
        users.clear();
        
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
                        int userId = Integer.parseInt(data[0].trim());
                        String fullName = data[1].trim();
                        String email = data[2].trim();
                        String username = data[3].trim();
                        String password = data[4].trim();
                        String phone = data[5].trim();
                        String type = data[6].trim();
                        
                        User user = null;
                        
                        switch (type) {
                            case "Administrator":
                                user = new Administrator(userId, fullName, email, username, password, phone, userId);
                                break;
                                
                            case "Waqif":
                                user = new Waqif(userId, fullName, email, username, password, phone, userId, 0);
                                break;
                                
                            case "Beneficiary":
                                // ===== FIXED: Read Education, Experience, TargetGroup =====
                                String education = "";
                                int experience = 0;
                                String targetGroup = "General";
                                
                                if (data.length > 7) {
                                    education = data[7].trim();
                                }
                                if (data.length > 8) {
                                    try {
                                        experience = Integer.parseInt(data[8].trim());
                                    } catch (NumberFormatException e) {
                                        experience = 0;
                                    }
                                }
                                if (data.length > 9) {
                                    targetGroup = data[9].trim();
                                    if (targetGroup.isEmpty()) targetGroup = "General";
                                }
                                
                                user = new Beneficiary(userId, fullName, email, username, password, phone, 
                                                       userId, education, experience, targetGroup);
                                break;
                                
                            case "CommitteeMember":
                                String specialization = "";
                                if (data.length > 10) {
                                    specialization = data[10].trim();
                                }
                                user = new CommitteeMember(userId, fullName, email, username, password, phone, 
                                                           userId, specialization);
                                break;
                                
                            default:
                                user = new User(userId, fullName, email, username, password, phone);
                        }
                        
                        if (user != null) {
                            users.add(user);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing user line: " + line);
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Loaded " + users.size() + " users from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}