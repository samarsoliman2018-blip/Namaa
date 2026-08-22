package Service;

import Model.NamaaIndex;
import Model.Project;
import java.util.ArrayList;
import java.io.*;

public class NamaaIndexService {
    private static ArrayList<NamaaIndex> indexes = new ArrayList<>();
    private static final String FILE = "namaa_index.csv";
    private static int nextId = 1;

    static {
        loadIndexes();
        if (!indexes.isEmpty()) {
            for (NamaaIndex idx : indexes) {
                if (idx.getIndexID() >= nextId) {
                    nextId = idx.getIndexID() + 1;
                }
            }
        }
    }

    public static void addIndex(NamaaIndex index) {
        if (index.getIndexID() == 0) {
            index.setIndexID(nextId++);
        }
        index.calculateIndex();
        indexes.add(index);
        saveAllToFile();
    }

    public static ArrayList<NamaaIndex> getIndexes() {
        return new ArrayList<>(indexes);
    }

    public static NamaaIndex getIndexByProject(int projectId) {
        for (NamaaIndex idx : indexes) {
            if (idx.getProject() != null && 
                idx.getProject().getProjectID() == projectId) {
                return idx;
            }
        }
        return null;
    }

    public static NamaaIndex getIndexByProject(Project project) {
        if (project == null) return null;
        return getIndexByProject(project.getProjectID());
    }

    public static NamaaIndex searchIndex(int id) {
        for (NamaaIndex idx : indexes) {
            if (idx.getIndexID() == id) {
                return idx;
            }
        }
        return null;
    }

    public static void updateIndex(NamaaIndex index) {
        for (int i = 0; i < indexes.size(); i++) {
            if (indexes.get(i).getIndexID() == index.getIndexID()) {
                index.calculateIndex();
                indexes.set(i, index);
                saveAllToFile();
                return;
            }
        }
    }

    public static double getAverageNamaaIndex() {
        if (indexes.isEmpty()) return 0;
        double total = 0;
        for (NamaaIndex idx : indexes) {
            total += idx.getFinalIndex();
        }
        return total / indexes.size();
    }

    public static NamaaIndex getBestPerformingProject() {
        NamaaIndex best = null;
        for (NamaaIndex idx : indexes) {
            if (best == null || idx.getFinalIndex() > best.getFinalIndex()) {
                best = idx;
            }
        }
        return best;
    }

    public static int getNextId() {
        return nextId++;
    }

    // ===== SAVE ALL =====
    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("IndexID,ProjectID,EconomicImpact,SocialImpact,Sustainability,Innovation,FinalIndex\n");
            
            for (NamaaIndex idx : indexes) {
                writer.write(
                    idx.getIndexID() + "," +
                    idx.getProject().getProjectID() + "," +
                    idx.getEconomicImpact() + "," +
                    idx.getSocialImpact() + "," +
                    idx.getSustainability() + "," +
                    idx.getInnovation() + "," +
                    idx.getFinalIndex() + "\n"
                );
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== LOAD =====
    private static void loadIndexes() {
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
                if (data.length >= 7) {
                    try {
                        int indexId = Integer.parseInt(data[0].trim());
                        int projectId = Integer.parseInt(data[1].trim());
                        double economic = Double.parseDouble(data[2].trim());
                        double social = Double.parseDouble(data[3].trim());
                        double sustainability = Double.parseDouble(data[4].trim());
                        double innovation = Double.parseDouble(data[5].trim());
                        double finalIndex = Double.parseDouble(data[6].trim());
                        
                        Project project = ProjectService.searchProject(projectId);
                        
                        if (project != null) {
                            NamaaIndex idx = new NamaaIndex();
                            idx.setIndexID(indexId);
                            idx.setProject(project);
                            idx.setEconomicImpact(economic);
                            idx.setSocialImpact(social);
                            idx.setSustainability(sustainability);
                            idx.setInnovation(innovation);
                            idx.setFinalIndex(finalIndex);
                            indexes.add(idx);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing Namaa index line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + indexes.size() + " Namaa indexes from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}