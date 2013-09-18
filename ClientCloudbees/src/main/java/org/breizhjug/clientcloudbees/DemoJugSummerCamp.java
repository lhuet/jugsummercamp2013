package org.breizhjug.clientcloudbees;

import com.cloudbees.api.ApplicationInfo;
import com.cloudbees.api.ApplicationListResponse;
import com.cloudbees.api.ApplicationStatusResponse;
import com.cloudbees.api.BeesClient;
import com.cloudbees.api.DatabaseInfo;
import com.cloudbees.api.DatabaseListResponse;
import java.net.URL;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Demo JugSummerCamp 2013 - Minitel dans les nuages
 *
 */
public class DemoJugSummerCamp {

    private static BeesClient client;
    private static ApplicationListResponse appList;
    private static DatabaseListResponse dbList;

    public static void main(String[] args) throws CmdLineException, Exception {


        // Get api Key / Secret / Url from command line
        CmdLineOptions cmdArgs = new CmdLineOptions();
        CmdLineParser parser = new CmdLineParser(cmdArgs);
        parser.parseArgument(args);

        init(cmdArgs);

        Scanner scan = new Scanner(System.in);
        boolean end = false;

        while (!end) {
            printMenu();

            String cmd = scan.next();

            switch (cmd) {
                case "1":
                    printListApplication();
                    break;
                case "2":
                    printListDatabase();
                    break;
                case "3":
                    printListApplication();
                    System.out.println("Which application do you want to stop ?");
                    String n = scan.next();
                    stopApplication(n);
                    printListApplication();
                    break;
                case "4":
                    printListApplication();
                    System.out.println("Which application do you want to start ?");
                    String m = scan.next();
                    startApplication(m);
                    printListApplication();
                    break;
                case "5":
                    printListJob(cmdArgs.jenkinsUrl);
                    break;
                case "Q":
                case "q":
                    end = true;
                    break;
                default:
                    System.out.println("---------------------");
            }
        }

        System.out.println("Bye !");

    }

    private static void init(CmdLineOptions cmdArgs) {
        // Cloudbees client
        client = new BeesClient(cmdArgs.apiUrl, cmdArgs.apiKey, cmdArgs.apiSecret, "xml", "1.0");
        // Trace desactivation
        client.setVerbose(false);
    }

    private static void printMenu() {
        System.out.println("------------------------------");
        System.out.println("-- 3615 Cloud@JugSummerCamp --");
        System.out.println("------------------------------");
        System.out.println("1- List applications");
        System.out.println("2- List databases");
        System.out.println("3- Stop application");
        System.out.println("4- Start application");
        System.out.println("5- List Jenkins Jobs");
        System.out.println("Q- Quit");
    }

    private static void printListApplication() throws Exception {
        System.out.println("Querying Cloudbess PaaS ...");
        appList = client.applicationList();
        Formatter formatter = new Formatter(System.out);
        System.out.println("-------------------------------------------------------------------------------");
        System.out.println("| # |       Application ID      |      Application name     |      Status     |");
        System.out.println("-------------------------------------------------------------------------------");
        for (int i = 0; i < appList.getApplications().size(); i++) {
            ApplicationInfo appinfo = appList.getApplications().get(i);
            formatter.format("| %1$1s | %2$25s | %3$25s | %4$15s |\n", i, appinfo.getId(), appinfo.getTitle(), appinfo.getStatus());
        }
        System.out.println("-------------------------------------------------------------------------------");

    }

    private static void printListDatabase() throws Exception {
        System.out.println("Querying Cloudbess PaaS ...");
        dbList = client.databaseList();
        Formatter formatter = new Formatter(System.out);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("|       Database name       |      Status     |       Username       |");
        System.out.println("----------------------------------------------------------------------");
        for (DatabaseInfo dbInfo : dbList.getDatabases()) {
            formatter.format("| %1$25s | %2$15s | %3$20s |\n", dbInfo.getName(), dbInfo.getStatus(), dbInfo.getUsername());
        }
        System.out.println("----------------------------------------------------------------------");
    }

    private static void stopApplication(String n) throws Exception {
        System.out.println("Stopping application : " + appList.getApplications().get(Integer.valueOf(n)).getTitle() + " ...");
        String appId = appList.getApplications().get(Integer.valueOf(n)).getId();
        ApplicationStatusResponse resp = client.applicationStop(appId);
        System.out.println("Stop status : " + resp.getStatus());
    }

    private static void startApplication(String n) throws Exception {
        System.out.println("Starting application : " + appList.getApplications().get(Integer.valueOf(n)).getTitle() + " ...");
        String appId = appList.getApplications().get(Integer.valueOf(n)).getId();
        ApplicationStatusResponse resp = client.applicationStart(appId);
        System.out.println("Start status : " + resp.getStatus());
    }

    private static void printListJob(String jenkinsUrl) throws Exception {
        System.out.println("Querying Jenkins REST API ...");
        URL url = new URL(jenkinsUrl);
        Document dom = new SAXReader().read(url);
        
        Formatter formatter = new Formatter(System.out);
        System.out.println("------------------------------------------");
        System.out.println("|       Job name       |      Status     |");
        System.out.println("------------------------------------------");
        for (Object job : dom.getRootElement().elements("job")) {
            Element tmpJob = (Element) job;
            formatter.format("| %1$20s | %2$15s |\n", tmpJob.elementText("name"), tmpJob.elementText("color"));
        }
        System.out.println("------------------------------------------");
    }
}
