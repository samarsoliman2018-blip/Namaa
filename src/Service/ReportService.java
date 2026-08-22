package Service;

import java.util.ArrayList;
import Model.*;

public class ReportService {

    private static ArrayList<ImpactReport> reports
            = new ArrayList<>();

    public static void addReport(ImpactReport report) {

        reports.add(report);

    }

    public static ArrayList<ImpactReport> getReports() {

        return reports;

    }

    public static ImpactReport searchReport(int id) {

        for (ImpactReport report : reports) {

            if (report.getReportID() == id)
                return report;

        }

        return null;

    }

}