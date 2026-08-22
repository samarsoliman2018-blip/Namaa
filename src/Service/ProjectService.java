package Service;

import Model.Project;
import java.util.ArrayList;
import java.io.*;

public class ProjectService {
    private static ArrayList<Project> projects = new ArrayList<>();
    private static final String FILE = "projects.csv";
    private static int nextId = 1;

    static {
        loadProjects();
        if (!projects.isEmpty()) {
            for (Project p : projects) {
                if (p.getProjectID() >= nextId) {
                    nextId = p.getProjectID() + 1;
                }
            }
        }
    }

    public static void addProject(Project project) {
        if (project.getProjectID() == 0) {
            project.setProjectID(nextId++);
        }
        // Check if project already exists
        for (Project p : projects) {
            if (p.getProjectID() == project.getProjectID()) {
                // Update existing project
                updateProject(project);
                return;
            }
        }
        projects.add(project);
        saveAllToFile();
        System.out.println("✅ Project added: " + project.getProjectName() + " (ID: " + project.getProjectID() + ")");
    }

    public static ArrayList<Project> getProjects() {
        return new ArrayList<>(projects);
    }

    public static Project searchProject(int id) {
        for (Project p : projects) {
            if (p.getProjectID() == id)
                return p;
        }
        return null;
    }

    public static void updateProject(Project project) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getProjectID() == project.getProjectID()) {
                projects.set(i, project);
                saveAllToFile();
                return;
            }
        }
    }

    public static int getNextId() {
        return nextId++;
    }

    private static void saveAllToFile() {
        try (FileWriter writer = new FileWriter(FILE, false)) {
            writer.write("ProjectID,ProjectName,Sector,Description,Location,ProjectCost,ExpectedBeneficiaries,DurationMonths\n");
            
            for (Project p : projects) {
                writer.write(
                    p.getProjectID() + "," +
                    p.getProjectName() + "," +
                    p.getSector() + "," +
                    p.getDescription() + "," +
                    p.getLocation() + "," +
                    p.getProjectCost() + "," +
                    p.getExpectedBeneficiaries() + "," +
                    p.getDurationMonths() + "\n"
                );
            }
            writer.flush();
            System.out.println("Saved " + projects.size() + " projects to CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProjects() {
        File file = new File(FILE);
        if (!file.exists()) return;
        
        projects.clear();
        
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
                if (data.length >= 8) {
                    try {
                        Project p = new Project();
                        p.setProjectID(Integer.parseInt(data[0].trim()));
                        p.setProjectName(data[1].trim());
                        p.setSector(data[2].trim());
                        p.setDescription(data[3].trim());
                        p.setLocation(data[4].trim());
                        p.setProjectCost(Double.parseDouble(data[5].trim()));
                        p.setExpectedBeneficiaries(Integer.parseInt(data[6].trim()));
                        p.setDurationMonths(Integer.parseInt(data[7].trim()));
                        projects.add(p);
                    } catch (Exception e) {
                        System.err.println("Error parsing project line: " + line);
                    }
                }
            }
            System.out.println("Loaded " + projects.size() + " projects from CSV");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}