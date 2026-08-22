package Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import Model.*;

public class AIService {
    
    // ============================================================
    // CONFIGURATION
    // ============================================================
    private static final String AI_PROVIDER = "groq";
    private static boolean offlineMode = false;
    
    // API Keys
    private static String openaiKey;
    private static String geminiKey;
    private static String groqKey;
    
    // API URLs
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(60))
            .build();

    // ============================================================
    // STATIC INITIALIZER
    // ============================================================
    static {
        // Load API keys from system properties
        openaiKey = System.getProperty("openai.api.key", "");
        geminiKey = System.getProperty("gemini.api.key", "");
        groqKey = System.getProperty("groq.api.key", "");
        
        // Also try environment variables as fallback
        if (openaiKey.isEmpty()) {
            openaiKey = System.getenv("OPENAI_API_KEY");
            if (openaiKey == null) openaiKey = "";
        }
        if (geminiKey.isEmpty()) {
            geminiKey = System.getenv("GEMINI_API_KEY");
            if (geminiKey == null) geminiKey = "";
        }
        if (groqKey.isEmpty()) {
            groqKey = System.getenv("GROQ_API_KEY");
            if (groqKey == null) groqKey = "";
        }
        
        String provider = getCurrentProvider();
        String keyStatus = isProviderConfigured(provider) ? "CONFIGURED ✅" : "NOT CONFIGURED ⚠️";
        System.out.println("🤖 AI Provider: " + provider + " (" + keyStatus + ")");
        
        if (!groqKey.isEmpty()) {
            String maskedKey = groqKey.substring(0, Math.min(8, groqKey.length())) + "...";
            System.out.println("   Groq Key: " + maskedKey);
        }
        
        // Check if AI is available
        if (!isAIAvailable()) {
            System.out.println("   ⚠️ AI Service is not available. Running in OFFLINE MODE.");
            offlineMode = true;
        }
    }

    // ============================================================
    // PROVIDER METHODS
    // ============================================================
    public static String getCurrentProvider() {
        return AI_PROVIDER;
    }
    
    public static boolean isProviderConfigured(String provider) {
        switch (provider.toLowerCase()) {
            case "openai": 
                return openaiKey != null && !openaiKey.isEmpty();
            case "gemini": 
                return geminiKey != null && !geminiKey.isEmpty() && 
                       (geminiKey.startsWith("AIzaSy") || geminiKey.startsWith("AQ."));
            case "groq": 
                return groqKey != null && !groqKey.isEmpty() && groqKey.startsWith("gsk_");
            default: 
                return false;
        }
    }
    
    public static boolean isAIAvailable() {
        return !offlineMode && isProviderConfigured(getCurrentProvider());
    }
    
    public static void setOfflineMode(boolean mode) {
        offlineMode = mode;
        System.out.println("🔧 AI Offline Mode: " + (mode ? "ENABLED" : "DISABLED"));
    }
    
    public static boolean isOfflineMode() {
        return offlineMode;
    }
    
    // ============================================================
    // KEY SETTERS
    // ============================================================
    public static void setOpenAIKey(String key) { 
        openaiKey = key; 
        System.setProperty("openai.api.key", key); 
        offlineMode = !isAIAvailable();
    }
    
    public static void setGeminiKey(String key) {
        if (key != null && !key.isEmpty()) {
            if (key.startsWith("AIzaSy") || key.startsWith("AQ.")) {
                geminiKey = key;
                System.setProperty("gemini.api.key", key);
                System.out.println("✅ Gemini API Key set successfully!");
                offlineMode = !isAIAvailable();
            } else {
                System.err.println("❌ Invalid Gemini API Key format.");
            }
        }
    }
    
    public static void setGroqKey(String key) { 
        if (key != null && !key.isEmpty() && key.startsWith("gsk_")) {
            groqKey = key; 
            System.setProperty("groq.api.key", key);
            System.out.println("✅ Groq API Key set successfully!");
            offlineMode = !isAIAvailable();
        } else {
            System.err.println("❌ Invalid Groq API Key format. Expected: gsk_...");
        }
    }
    
    // ============================================================
    // AVAILABLE PROVIDERS
    // ============================================================
    public static String getAvailableProviders() {
        StringBuilder sb = new StringBuilder();
        sb.append("Available Providers:\n");
        sb.append("  OpenAI: ").append(isProviderConfigured("openai") ? "✅" : "❌").append("\n");
        sb.append("  Gemini: ").append(isProviderConfigured("gemini") ? "✅" : "❌").append("\n");
        sb.append("  Groq: ").append(isProviderConfigured("groq") ? "✅" : "❌").append("\n");
        sb.append("\nCurrent Provider: ").append(getCurrentProvider());
        sb.append("\nOffline Mode: ").append(offlineMode ? "✅ ENABLED" : "❌ DISABLED");
        return sb.toString();
    }

    // ============================================================
    // MAIN ENTRY POINT - SEND CHAT MESSAGE
    // ============================================================
    public static String sendChatMessage(String systemPrompt, String userMessage) {
        // If offline mode is enabled, use fallback
        if (offlineMode || !isAIAvailable()) {
            return getFallbackResponse(systemPrompt, userMessage);
        }

        String provider = getCurrentProvider();
        
        try {
            switch (provider.toLowerCase()) {
                case "openai":
                    return sendOpenAI(systemPrompt, userMessage);
                case "gemini":
                    return sendGemini(systemPrompt, userMessage);
                case "groq":
                    return sendGroq(systemPrompt, userMessage);
                default:
                    return getFallbackResponse(systemPrompt, userMessage);
            }
        } catch (Exception e) {
            System.err.println("❌ Error calling " + provider + " API: " + e.getMessage());
            return getFallbackResponse(systemPrompt, userMessage);
        }
    }

    // ============================================================
    // SEND TO OPENAI
    // ============================================================
    private static String sendOpenAI(String systemPrompt, String userMessage) throws Exception {
        String requestBody = String.format(
            "{\"model\":\"gpt-3.5-turbo\"," +
            "\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"%s\"}," +
                "{\"role\":\"user\",\"content\":\"%s\"}" +
            "]," +
            "\"temperature\":0.7,\"max_tokens\":1000}",
            escapeJson(systemPrompt), escapeJson(userMessage)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openaiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseOpenAIResponse(response.body());
        } else {
            return "⚠️ OpenAI Error: " + response.statusCode() + "\n" + response.body();
        }
    }

    // ============================================================
    // SEND TO GEMINI
    // ============================================================
    private static String sendGemini(String systemPrompt, String userMessage) throws Exception {
        String fullPrompt = systemPrompt + "\n\n" + userMessage;
        
        String requestBody = String.format(
            "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
            escapeJson(fullPrompt)
        );

        String url = GEMINI_URL + "?key=" + geminiKey;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseGeminiResponse(response.body());
        } else {
            return "⚠️ Gemini Error: " + response.statusCode() + "\n" + response.body();
        }
    }

    // ============================================================
    // SEND TO GROQ
    // ============================================================
    private static String sendGroq(String systemPrompt, String userMessage) throws Exception {
        String requestBody = String.format(
            "{\"model\":\"groq/compound-mini\"," +
            "\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"%s\"}," +
                "{\"role\":\"user\",\"content\":\"%s\"}" +
            "]," +
            "\"temperature\":0.7,\"max_tokens\":1000}",
            escapeJson(systemPrompt), escapeJson(userMessage)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + groqKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseOpenAIResponse(response.body());
        } else {
            return "⚠️ Groq Error: " + response.statusCode() + "\n" + response.body();
        }
    }

    // ============================================================
    // PARSE RESPONSES
    // ============================================================
    private static String parseOpenAIResponse(String jsonResponse) {
        try {
            String contentMarker = "\"content\":\"";
            int startIndex = jsonResponse.indexOf(contentMarker);
            if (startIndex == -1) {
                return "⚠️ Could not parse AI response.";
            }
            
            startIndex += contentMarker.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return "⚠️ Could not parse AI response.";
            }
            
            return jsonResponse.substring(startIndex, endIndex)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
                    
        } catch (Exception e) {
            return "⚠️ Error parsing AI response.";
        }
    }

    private static String parseGeminiResponse(String jsonResponse) {
        try {
            String textMarker = "\"text\":\"";
            int startIndex = jsonResponse.indexOf(textMarker);
            if (startIndex == -1) {
                return "⚠️ Could not parse Gemini response.";
            }
            
            startIndex += textMarker.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return "⚠️ Could not parse Gemini response.";
            }
            
            return jsonResponse.substring(startIndex, endIndex)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");
                    
        } catch (Exception e) {
            return "⚠️ Error parsing Gemini response.";
        }
    }

    // ============================================================
    // ESCAPE JSON
    // ============================================================
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    // ============================================================
    // FALLBACK SYSTEM - FULL IMPLEMENTATION
    // ============================================================
    
    /**
     * Main fallback response method - decides which fallback to use
     */
    private static String getFallbackResponse(String systemPrompt, String userMessage) {
        // Check if it's a project evaluation request
        if (userMessage.contains("PRI Score") && userMessage.contains("Economic")) {
            return getFallbackProjectEvaluation(userMessage);
        }
        
        // Check if it's a lessons learned request
        if (userMessage.contains("Duration") && userMessage.contains("Success Rate")) {
            return getFallbackLessons(userMessage);
        }
        
        // Check if it's a template report analysis
        if (userMessage.contains("Template Report") || userMessage.contains("Progress")) {
            return getFallbackTemplateAnalysis(userMessage);
        }
        
        // Generic fallback
        return getGenericFallbackResponse();
    }

    /**
     * Fallback: Project Evaluation
     */
    private static String getFallbackProjectEvaluation(String userMessage) {
        try {
            // Parse values from the user message
            double priScore = extractDouble(userMessage, "PRI Score:");
            double economic = extractDouble(userMessage, "Economic:");
            double technical = extractDouble(userMessage, "Technical:");
            double social = extractDouble(userMessage, "Social:");
            double environmental = extractDouble(userMessage, "Environmental:");
            double innovation = extractDouble(userMessage, "Innovation:");
            String projectName = extractText(userMessage, "Project Name:");
            String sector = extractText(userMessage, "Sector:");
            
            StringBuilder sb = new StringBuilder();
            sb.append("📊 AI ASSESSMENT REPORT (Offline Mode)\n");
            sb.append("─────────────────────────────────────────────\n\n");
            sb.append("Project: ").append(projectName).append("\n");
            sb.append("Sector: ").append(sector).append("\n");
            sb.append("PRI Score: ").append(String.format("%.2f", priScore)).append("\n\n");
            
            // ===== STRENGTHS =====
            sb.append("✅ KEY STRENGTHS:\n");
            int strengths = 0;
            if (economic >= 70) { sb.append("   • Strong economic viability\n"); strengths++; }
            if (social >= 70) { sb.append("   • High social impact potential\n"); strengths++; }
            if (technical >= 70) { sb.append("   • Technically feasible\n"); strengths++; }
            if (environmental >= 70) { sb.append("   • Environmentally sustainable\n"); strengths++; }
            if (innovation >= 70) { sb.append("   • Innovative approach\n"); strengths++; }
            if (strengths == 0) sb.append("   • Moderate potential - needs further analysis\n");
            sb.append("\n");
            
            // ===== AREAS FOR IMPROVEMENT =====
            sb.append("⚠️ AREAS FOR IMPROVEMENT:\n");
            int weaknesses = 0;
            if (economic < 60) { sb.append("   • Strengthen financial projections\n"); weaknesses++; }
            if (social < 60) { sb.append("   • Increase community engagement\n"); weaknesses++; }
            if (technical < 60) { sb.append("   • Address technical feasibility\n"); weaknesses++; }
            if (environmental < 60) { sb.append("   • Enhance environmental considerations\n"); weaknesses++; }
            if (innovation < 60) { sb.append("   • Develop more innovative elements\n"); weaknesses++; }
            if (weaknesses == 0) sb.append("   • No major weaknesses identified\n");
            sb.append("\n");
            
            // ===== RECOMMENDATION =====
            sb.append("💡 RECOMMENDATION:\n");
            if (priScore >= 80) {
                sb.append("   ✅ APPROVED\n");
                sb.append("   Project shows strong potential for success.\n");
            } else if (priScore >= 60) {
                sb.append("   ⚠️ NEEDS REVISION\n");
                sb.append("   Project is promising but needs refinement.\n");
            } else {
                sb.append("   ❌ REJECTED\n");
                sb.append("   Project requires significant improvement.\n");
            }
            sb.append("\n");
            sb.append(getOfflineModeNotice());
            
            return sb.toString();
            
        } catch (Exception e) {
            return getGenericFallbackResponse();
        }
    }

    /**
     * Fallback: Lessons Learned
     */
    private static String getFallbackLessons(String userMessage) {
        try {
            String projectName = extractText(userMessage, "Project Name:");
            String sector = extractText(userMessage, "Sector:");
            int duration = (int) extractDouble(userMessage, "Duration:");
            int beneficiaries = (int) extractDouble(userMessage, "Beneficiaries:");
            double successRate = extractDouble(userMessage, "Success Rate:") / 100.0;
            double finalIndex = extractDouble(userMessage, "Namaa Index:");
            
            StringBuilder sb = new StringBuilder();
            sb.append("📚 LESSONS LEARNED (Offline Mode)\n");
            sb.append("─────────────────────────────────────────────\n\n");
            sb.append("Project: ").append(projectName).append("\n");
            sb.append("Sector: ").append(sector).append("\n");
            sb.append("Duration: ").append(duration).append(" months\n");
            sb.append("Beneficiaries: ").append(beneficiaries).append("\n");
            sb.append("Success Rate: ").append(String.format("%.0f%%", successRate * 100)).append("\n");
            sb.append("Namaa Index: ").append(String.format("%.2f", finalIndex)).append("\n\n");
            
            // ===== SUCCESS FACTORS =====
            sb.append("✅ KEY SUCCESS FACTORS:\n");
            if (successRate >= 0.8) {
                sb.append("   • Effective planning and execution\n");
                sb.append("   • Strong community engagement\n");
                sb.append("   • Adequate resource allocation\n");
                sb.append("   • Good risk management\n");
            } else if (successRate >= 0.6) {
                sb.append("   • Moderate planning effectiveness\n");
                sb.append("   • Some community engagement\n");
                sb.append("   • Resource constraints identified\n");
            } else {
                sb.append("   • Planning needs improvement\n");
                sb.append("   • Limited community engagement\n");
                sb.append("   • Resource challenges faced\n");
            }
            sb.append("\n");
            
            // ===== CHALLENGES =====
            sb.append("⚠️ KEY CHALLENGES:\n");
            if (successRate < 0.8) {
                sb.append("   • Implementation delays\n");
                sb.append("   • Resource management issues\n");
                sb.append("   • Monitoring and evaluation gaps\n");
            } else {
                sb.append("   • Minimal challenges encountered\n");
                sb.append("   • Good risk management\n");
            }
            sb.append("\n");
            
            // ===== RECOMMENDATIONS =====
            sb.append("💡 RECOMMENDATIONS:\n");
            sb.append("   • Start documentation from day one\n");
            sb.append("   • Engage community early\n");
            sb.append("   • Plan for contingencies\n");
            sb.append("   • Regular monitoring and reporting\n");
            sb.append("\n");
            sb.append(getOfflineModeNotice());
            
            return sb.toString();
            
        } catch (Exception e) {
            return getGenericFallbackResponse();
        }
    }

    /**
     * Fallback: Template Report Analysis
     */
    private static String getFallbackTemplateAnalysis(String userMessage) {
        try {
            String projectName = extractText(userMessage, "Project Name:");
            String sector = extractText(userMessage, "Sector:");
            double progress = extractDouble(userMessage, "Progress:");
            double spent = extractDouble(userMessage, "Amount Spent:");
            double remaining = extractDouble(userMessage, "Amount Remaining:");
            double revenue = extractDouble(userMessage, "Revenue Generated:");
            int beneficiaries = (int) extractDouble(userMessage, "Beneficiaries Reached:");
            String achievements = extractText(userMessage, "ACHIEVEMENTS:");
            String challenges = extractText(userMessage, "CHALLENGES:");
            String futurePlans = extractText(userMessage, "FUTURE PLANS:");
            
            StringBuilder sb = new StringBuilder();
            sb.append("📊 TEMPLATE ANALYSIS (Offline Mode)\n");
            sb.append("─────────────────────────────────────────────\n\n");
            sb.append("Project: ").append(projectName).append("\n");
            sb.append("Sector: ").append(sector).append("\n");
            sb.append("Progress: ").append(String.format("%.1f%%", progress)).append("\n");
            sb.append("Spent: ").append(String.format("%.2f QR", spent)).append("\n");
            sb.append("Remaining: ").append(String.format("%.2f QR", remaining)).append("\n");
            sb.append("Revenue: ").append(String.format("%.2f QR", revenue)).append("\n");
            sb.append("Beneficiaries: ").append(beneficiaries).append("\n\n");
            
            // Performance
            double totalBudget = spent + remaining;
            double spentRatio = totalBudget > 0 ? (spent / totalBudget) * 100 : 0;

            sb.append("📈 PERFORMANCE:\n");
            if (progress >= 80 && spentRatio <= 80) {
                sb.append("   ✅ On track and within budget.\n");
            } else if (progress < 50 && spentRatio > 60) {
                sb.append("   ⚠️ Budget may be insufficient for remaining work.\n");
            } else if (progress >= 80 && spentRatio > 90) {
                sb.append("   ⚠️ Project nearly complete, budget almost fully utilized.\n");
            } else {
                sb.append("   ℹ️ Progressing as expected.\n");
            }
            
            // Revenue
            if (revenue > 0) {
                double roi = spent > 0 ? (revenue / spent) * 100 : 0;
                sb.append("\n💰 REVENUE:\n");
                sb.append("   ROI: ").append(String.format("%.1f%%", roi)).append("\n");
                if (roi > 100) sb.append("   ✅ Excellent return on investment.\n");
                else if (roi > 50) sb.append("   ✅ Good return on investment.\n");
                else sb.append("   ℹ️ Revenue generation needs improvement.\n");
            }
            
            // Achievements
            if (achievements != null && !achievements.isEmpty() && !achievements.equals("None reported")) {
                sb.append("\n✅ ACHIEVEMENTS:\n").append("   ").append(achievements.replace("\n", "\n   ")).append("\n");
            }
            
            // Challenges
            if (challenges != null && !challenges.isEmpty() && !challenges.equals("None reported")) {
                sb.append("\n⚠️ CHALLENGES:\n").append("   ").append(challenges.replace("\n", "\n   ")).append("\n");
            }
            
            // Future Plans
            if (futurePlans != null && !futurePlans.isEmpty() && !futurePlans.equals("Not specified")) {
                sb.append("\n📋 FUTURE PLANS:\n").append("   ").append(futurePlans.replace("\n", "\n   ")).append("\n");
            }
            
            // Recommendations
            sb.append("\n💡 RECOMMENDATIONS:\n");
            if (progress < 50 && spent > 0) sb.append("   • Review project timeline and resource allocation.\n");
            if (challenges != null && !challenges.isEmpty() && !challenges.equals("None reported")) {
                sb.append("   • Address challenges proactively with committee support.\n");
            }
            if (progress >= 80) sb.append("   • Plan for project completion and documentation.\n");
            sb.append("   • Continue regular reporting for accurate tracking.\n");
            sb.append("\n");
            sb.append(getOfflineModeNotice());
            
            return sb.toString();
            
        } catch (Exception e) {
            return getGenericFallbackResponse();
        }
    }

    /**
     * Generic fallback response when parsing fails
     */
    private static String getGenericFallbackResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("🤖 AI SERVICE - OFFLINE MODE\n");
        sb.append("═══════════════════════════════════════════════════\n\n");
        sb.append("⚠️ AI service is currently unavailable.\n\n");
        sb.append("📌 To enable AI features:\n");
        sb.append("   1. Check your internet connection\n");
        sb.append("   2. Verify your API key\n");
        sb.append("   3. Restart the application\n\n");
        sb.append("📝 Using fallback evaluation mode.\n");
        sb.append("   Basic analysis is provided based on the data entered.\n\n");
        sb.append("💡 The system will reconnect automatically when the service becomes available.\n");
        sb.append("═══════════════════════════════════════════════════");
        return sb.toString();
    }

    /**
     * Offline mode notice
     */
    private static String getOfflineModeNotice() {
        return "ℹ️ This is a fallback analysis. Connect to AI for detailed insights.\n" +
               "─────────────────────────────────────────────";
    }

    // ============================================================
    // HELPER METHODS FOR PARSING
    // ============================================================
    
    private static double extractDouble(String text, String label) {
        try {
            int start = text.indexOf(label);
            if (start == -1) return 0;
            start += label.length();
            int end = text.indexOf("\n", start);
            if (end == -1) end = text.length();
            String value = text.substring(start, end).trim();
            // Remove any non-numeric characters except decimal point
            value = value.replaceAll("[^0-9.]", "");
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String extractText(String text, String label) {
        try {
            int start = text.indexOf(label);
            if (start == -1) return "N/A";
            start += label.length();
            int end = text.indexOf("\n", start);
            if (end == -1) end = text.length();
            String value = text.substring(start, end).trim();
            if (value.isEmpty()) return "N/A";
            return value;
        } catch (Exception e) {
            return "N/A";
        }
    }

    // ============================================================
    // PUBLIC AI METHODS (With Offline Mode Support)
    // ============================================================
    
    /**
     * Generate project evaluation - uses AI if available, otherwise fallback
     */
    public static String generateProjectEvaluation(String projectName, String sector, 
            double priScore, double economic, double technical, double social, 
            double environmental, double innovation) {
        
        if (offlineMode || !isAIAvailable()) {
            // Build a message that can be parsed by the fallback
            String userMessage = String.format(
                "Project Name: %s\nSector: %s\nPRI Score: %.2f\nEconomic: %.2f\nTechnical: %.2f\nSocial: %.2f\nEnvironmental: %.2f\nInnovation: %.2f",
                projectName, sector, priScore, economic, technical, social, environmental, innovation
            );
            return getFallbackProjectEvaluation(userMessage);
        }
        
        String systemPrompt = "You are an expert project evaluator for a community development fund. " +
                              "Provide a detailed, professional evaluation with clear sections.";

        String userMessage = String.format(
            "Project Name: %s\nSector: %s\nPRI Score: %.2f\nEconomic: %.2f\nTechnical: %.2f\nSocial: %.2f\nEnvironmental: %.2f\nInnovation: %.2f\n\n" +
            "Provide:\n1. Key Strengths (3 bullet points)\n2. Areas for Improvement (3 bullet points)\n3. Final Recommendation (Approved/Needs Revision/Rejected)\n4. Brief Justification (2-3 sentences)",
            projectName, sector, priScore, economic, technical, social, environmental, innovation
        );

        return sendChatMessage(systemPrompt, userMessage);
    }

    /**
     * Generate lessons learned - uses AI if available, otherwise fallback
     */
    public static String generateLessonsLearned(String projectName, String sector, 
            int duration, int beneficiaries, double successRate, double finalIndex) {
        
        if (offlineMode || !isAIAvailable()) {
            String userMessage = String.format(
                "Project Name: %s\nSector: %s\nDuration: %d\nBeneficiaries: %d\nSuccess Rate: %.0f\nNamaa Index: %.2f",
                projectName, sector, duration, beneficiaries, successRate * 100, finalIndex
            );
            return getFallbackLessons(userMessage);
        }
        
        String systemPrompt = "You are a project librarian. Write a concise, practical summary of lessons learned.";

        String userMessage = String.format(
            "Project Name: %s\nSector: %s\nDuration: %d months\nBeneficiaries: %d\nSuccess Rate: %.0f%%\nNamaa Index: %.2f\n\n" +
            "Write a 100-word summary including:\n- One key success factor\n- One key challenge\n- One recommendation for future projects",
            projectName, sector, duration, beneficiaries, successRate * 100, finalIndex
        );

        return sendChatMessage(systemPrompt, userMessage);
    }

    /**
     * Generate project overview - uses AI if available, otherwise fallback
     */
    public static String generateProjectOverview(String projectName, String description, 
            double budget, String sector) {
        
        if (offlineMode || !isAIAvailable()) {
            return getGenericFallbackResponse();
        }
        
        String systemPrompt = "You are a project summary expert. Provide a concise, professional overview.";

        String userMessage = String.format(
            "Project: %s\nDescription: %s\nBudget: %.2f\nSector: %s\n\n" +
            "Provide a 2-3 sentence summary of this project's potential impact.",
            projectName, description, budget, sector
        );

        return sendChatMessage(systemPrompt, userMessage);
    }

    /**
     * Analyze template report - uses AI if available, otherwise fallback
     */
    public static String analyzeTemplateReport(
            String projectName,
            String sector,
            double progressPercentage,
            double amountSpent,
            double amountRemaining,
            double revenueGenerated,
            int actualBeneficiaries,
            String achievements,
            String challenges,
            String futurePlans) {
        
        String userMessage = String.format(
            "Template Report Analysis\n" +
            "Project Name: %s\n" +
            "Sector: %s\n" +
            "Progress: %.1f\n" +
            "Amount Spent: %.2f\n" +
            "Amount Remaining: %.2f\n" +
            "Revenue Generated: %.2f\n" +
            "Beneficiaries Reached: %d\n" +
            "ACHIEVEMENTS:\n%s\n" +
            "CHALLENGES:\n%s\n" +
            "FUTURE PLANS:\n%s",
            projectName,
            sector,
            progressPercentage,
            amountSpent,
            amountRemaining,
            revenueGenerated,
            actualBeneficiaries,
            achievements != null && !achievements.isEmpty() ? achievements : "None reported",
            challenges != null && !challenges.isEmpty() ? challenges : "None reported",
            futurePlans != null && !futurePlans.isEmpty() ? futurePlans : "Not specified"
        );
        
        if (offlineMode || !isAIAvailable()) {
            return getFallbackTemplateAnalysis(userMessage);
        }
        
        String systemPrompt = "You are an expert project monitoring analyst and business coach. " +
                              "You provide clear, actionable insights based on project data. " +
                              "Be specific and constructive in your feedback.";

        return sendChatMessage(systemPrompt, userMessage);
    }
}