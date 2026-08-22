package Main;

import UI.LoginFrame;
import Service.AIService;
import Service.HistoricalDataService;
import Model.User;
import Service.UserService;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    
    private static final String CONFIG_FILE = "config.properties";
    private static final String API_KEY_FILE = "api_key.txt";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    public static void main(String[] args) {
        
        // ============================================================
        // STEP 1: LOAD API KEYS FROM MULTIPLE SOURCES
        // ============================================================
        loadApiKeys(args);
        
        // ============================================================
        // STEP 2: PRINT STATUS
        // ============================================================
        printStartupStatus();
        
        // ============================================================
        // STEP 3: SET SYSTEM LOOK AND FEEL
        // ============================================================
        setLookAndFeel();
        
        // ============================================================
        // STEP 4: LOAD DATA
        // ============================================================
        loadData();
        
        // ============================================================
        // STEP 5: START APPLICATION
        // ============================================================
        startApplication();
    }
    
    // ===== LOAD API KEYS =====
    private static void loadApiKeys(String[] args) {
        System.out.println("🔑 Loading API keys...");
        
        // 1. Try command line arguments (Highest Priority)
        for (String arg : args) {
            if (arg.startsWith("-Dgemini.api.key=")) {
                String key = arg.substring("-Dgemini.api.key=".length());
                if (!key.isEmpty()) {
                    System.setProperty("gemini.api.key", "AQ.Ab8RN6LVgj51pvf4Yw8yD02pPLW3OY_XDW91GOM6LWtxmanZ2A");
                    System.out.println("  ✅ Gemini key loaded from command line");
                }
            } else if (arg.startsWith("-Dopenai.api.key=")) {
                String key = arg.substring("-Dopenai.api.key=".length());
                if (!key.isEmpty()) {
                    System.setProperty("openai.api.key", key);
                    System.out.println("  ✅ OpenAI key loaded from command line");
                }
            } else if (arg.startsWith("-Dgroq.api.key=")) {
                String key = arg.substring("-Dgroq.api.key=".length());
                if (!key.isEmpty()) {
                    System.setProperty("groq.api.key", key);
                    System.out.println("  ✅ Groq key loaded from command line");
                }
            }
        }
        
        // 2. Try config.properties file
        try {
            Properties props = new Properties();
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                    String geminiKey = props.getProperty("gemini.api.key");
                    if (geminiKey != null && !geminiKey.isEmpty() && 
                        System.getProperty("gemini.api.key") == null) {
                        System.setProperty("gemini.api.key", geminiKey);
                        System.out.println("  ✅ Gemini key loaded from " + CONFIG_FILE);
                    }
                    String openaiKey = props.getProperty("openai.api.key");
                    if (openaiKey != null && !openaiKey.isEmpty() && 
                        System.getProperty("openai.api.key") == null) {
                        System.setProperty("openai.api.key", openaiKey);
                        System.out.println("  ✅ OpenAI key loaded from " + CONFIG_FILE);
                    }
                    String groqKey = props.getProperty("groq.api.key");
                    if (groqKey != null && !groqKey.isEmpty() && 
                        System.getProperty("groq.api.key") == null) {
                        System.setProperty("groq.api.key", groqKey);
                        System.out.println("  ✅ Groq key loaded from " + CONFIG_FILE);
                    }
                }
            }
        } catch (IOException e) {
            // Config file doesn't exist or can't be read - skip
        }
        
        // 3. Try api_key.txt file (simple text file with just the key)
        try {
            File keyFile = new File(API_KEY_FILE);
            if (keyFile.exists() && System.getProperty("gemini.api.key") == null) {
                String key = new String(Files.readAllBytes(Paths.get(API_KEY_FILE))).trim();
                if (!key.isEmpty()) {
                    System.setProperty("gemini.api.key", key);
                    System.out.println("  ✅ Gemini key loaded from " + API_KEY_FILE);
                }
            }
        } catch (IOException e) {
            // Key file doesn't exist - skip
        }
        
        // 4. Try environment variables
        String geminiEnv = System.getenv("GEMINI_API_KEY");
        if (geminiEnv != null && !geminiEnv.isEmpty() && 
            System.getProperty("gemini.api.key") == null) {
            System.setProperty("gemini.api.key", geminiEnv);
            System.out.println("  ✅ Gemini key loaded from environment variable");
        }
        
        String openaiEnv = System.getenv("OPENAI_API_KEY");
        if (openaiEnv != null && !openaiEnv.isEmpty() && 
            System.getProperty("openai.api.key") == null) {
            System.setProperty("openai.api.key", openaiEnv);
            System.out.println("  ✅ OpenAI key loaded from environment variable");
        }
        
        String groqEnv = System.getenv("GROQ_API_KEY");
        if (groqEnv != null && !groqEnv.isEmpty() && 
            System.getProperty("groq.api.key") == null) {
            System.setProperty("groq.api.key", groqEnv);
            System.out.println("  ✅ Groq key loaded from environment variable");
        }
        
        // 5. Fallback: Try a default key file location
        try {
            File homeKeyFile = new File(System.getProperty("user.home"), ".namaa_api_key");
            if (homeKeyFile.exists() && System.getProperty("gemini.api.key") == null) {
                String key = new String(Files.readAllBytes(homeKeyFile.toPath())).trim();
                if (!key.isEmpty()) {
                    System.setProperty("gemini.api.key", key);
                    System.out.println("  ✅ Gemini key loaded from ~/.namaa_api_key");
                }
            }
        } catch (IOException e) {
            // Skip
        }
        
        // Warn if no API key found
        if (System.getProperty("gemini.api.key") == null &&
            System.getProperty("openai.api.key") == null &&
            System.getProperty("groq.api.key") == null) {
            System.out.println("  ⚠️ No API keys found. AI features will be disabled.");
            System.out.println("  💡 To enable AI, set an API key using one of these methods:");
            System.out.println("     1. Create config.properties file with:");
            System.out.println("        gemini.api.key=your-key-here");
            System.out.println("     2. Set environment variable: GEMINI_API_KEY");
            System.out.println("     3. Run with VM argument: -Dgemini.api.key=your-key-here");
            System.out.println("     4. Create api_key.txt file with your key");
        }
    }
    
    // ===== PRINT STARTUP STATUS =====
    private static void printStartupStatus() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║              NAMAA SMART WAQF PLATFORM                         ║");
        System.out.println("║                      Starting Up...                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");
        
        // AI Status
        String provider = AIService.getCurrentProvider();
        boolean available = AIService.isAIAvailable();
        String aiStatus = available ? "ENABLED ✅" : "DISABLED ⚠️";
        String aiColor = available ? "\033[32m" : "\033[33m";
        String resetColor = "\033[0m";
        
        System.out.println("🤖 AI Service:");
        System.out.println("   • Provider:     " + provider);
        System.out.println("   • Status:       " + aiColor + aiStatus + resetColor);
        System.out.println("   • Available:    " + (available ? "Yes" : "No"));
        
        if (available) {
            // Check which providers are configured
            String[] providers = {"gemini", "openai", "groq"};
            for (String p : providers) {
                boolean configured = AIService.isProviderConfigured(p);
                System.out.println("   • " + p + ":          " + (configured ? "✅ Configured" : "❌ Not configured"));
            }
        }
        
        System.out.println("\n📊 System Information:");
        System.out.println("   • Java Version: " + System.getProperty("java.version"));
        System.out.println("   • OS:           " + System.getProperty("os.name"));
        System.out.println("   • User:         " + System.getProperty("user.name"));
        System.out.println("   • Working Dir:  " + System.getProperty("user.dir"));
        
        // Check for default users
        System.out.println("\n👥 User Information:");
        int userCount = UserService.getUsers().size();
        System.out.println("   • Total Users:  " + userCount);
        if (userCount > 0) {
            System.out.println("   • Default users loaded");
        } else {
            System.out.println("   ⚠️ No users found. Creating default users...");
        }
        
        // Historical data
        int historyCount = HistoricalDataService.getAllProjects().size();
        System.out.println("   • Historical Projects: " + historyCount);
        if (historyCount == 0) {
            System.out.println("   ℹ️  No historical data found. Default data will be created.");
        }
        
        System.out.println("\n" + "═".repeat(63) + "\n");
    }
    
    // ===== SET LOOK AND FEEL =====
    private static void setLookAndFeel() {
        try {
            // Try Nimbus first for better look
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("🎨 Using Nimbus Look and Feel");
                    return;
                }
            }
            // Fallback to system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("🎨 Using System Look and Feel");
        } catch (Exception e) {
            System.out.println("⚠️ Could not set Look and Feel: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                System.out.println("🎨 Using Cross-Platform Look and Feel");
            } catch (Exception ex) {
                System.out.println("⚠️ Could not set any Look and Feel");
            }
        }
    }
    
    // ===== LOAD DATA =====
    private static void loadData() {
        System.out.println("📂 Loading data...");
        
        // Force historical data to load
        int historyCount = HistoricalDataService.getAllProjects().size();
        System.out.println("   • Historical projects loaded: " + historyCount);
        
        // Force users to load
        int userCount = UserService.getUsers().size();
        System.out.println("   • Users loaded: " + userCount);
        
        // Check waqf data
        int waqfCount = Service.WaqfService.getAllWaqfs().size();
        System.out.println("   • Waqfs loaded: " + waqfCount);
        
        // Check loan data
        int loanCount = Service.LoanService.getLoans().size();
        System.out.println("   • Loans loaded: " + loanCount);
        
        // Check application data
        int appCount = Service.FundingService.getApplications().size();
        System.out.println("   • Applications loaded: " + appCount);
        
        System.out.println("✅ Data loaded successfully\n");
    }
    
    // ===== START APPLICATION =====
    private static void startApplication() {
        System.out.println("🚀 Starting application...\n");
        
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame();
                System.out.println("✅ Application started successfully!");
                System.out.println("📍 Login window opened");
            } catch (Exception e) {
                System.err.println("❌ Error starting application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error starting application:\n" + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    // ===== GENERATE CONFIG FILE (Utility Method) =====
    public static void generateConfigFile() {
        System.out.println("📝 Generating config.properties file...");
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            writer.write("# Namaa Smart Waqf Platform Configuration\n");
            writer.write("# Generated on: " + new java.util.Date() + "\n");
            writer.write("# \n");
            writer.write("# To enable AI features, uncomment and set your API key:\n");
            writer.write("# \n");
            writer.write("# Get your API key from:\n");
            writer.write("#   Gemini: https://makersuite.google.com/app/apikey\n");
            writer.write("#   OpenAI: https://platform.openai.com/api-keys\n");
            writer.write("#   Groq:   https://console.groq.com/keys\n");
            writer.write("# \n");
            writer.write("# gemini.api.key=your-actual-gemini-api-key-here\n");
            writer.write("# openai.api.key=your-actual-openai-api-key-here\n");
            writer.write("# groq.api.key=your-actual-groq-api-key-here\n");
            writer.flush();
            System.out.println("✅ Config file created: " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("❌ Error creating config file: " + e.getMessage());
        }
    }
    
    // ===== MAIN WITH ARGUMENTS =====
    public static void mainWithArgs(String[] args) {
        // Check for special commands
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("--generate-config")) {
                generateConfigFile();
                return;
            }
            if (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h")) {
                printHelp();
                return;
            }
        }
        
        // Normal startup
        main(args);
    }
    
    // ===== PRINT HELP =====
    private static void printHelp() {
        System.out.println("Namaa Smart Waqf Platform - Help\n");
        System.out.println("Usage: java -jar namaa.jar [options]");
        System.out.println("\nOptions:");
        System.out.println("  --generate-config    Generate config.properties file");
        System.out.println("  --help, -h           Show this help message");
        System.out.println("\nAPI Key Configuration:");
        System.out.println("  1. Command Line:  java -Dgemini.api.key=your-key -jar namaa.jar");
        System.out.println("  2. Config File:   Create config.properties with gemini.api.key=your-key");
        System.out.println("  3. Environment:   Set GEMINI_API_KEY environment variable");
        System.out.println("  4. Key File:      Create api_key.txt with your key");
        System.out.println("\nDefault Login Credentials:");
        System.out.println("  admin      / 1234  (Administrator)");
        System.out.println("  waqif      / 1234  (Waqif/Donor)");
        System.out.println("  beneficiary/ 1234  (Beneficiary)");
        System.out.println("  sarah      / 1234  (Beneficiary)");
        System.out.println("  khalid     / 1234  (Beneficiary)");
        System.out.println("  guest      / guest (Guest User)");
    }
}