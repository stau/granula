package nl.tudelft.pds.granula.util;

import nl.tudelft.pds.granula.ArchiverConfiguration;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by wing on 26-8-15.
 */
public class JobListGenerator {

    public static void main(String[] args) {
        JobListGenerator jobListGenerator  = new JobListGenerator();
        jobListGenerator.generateRecentJobsList();
    }

    public void generateRecentJobsList() {
        String vizRepoPath = ArchiverConfiguration.repoPath + "/granula-visualizer";
        generateForDirectory(
                vizRepoPath + "/archive/",
                vizRepoPath + "/list" + "/recent-jobs.xml");
    }

    public void generateForDirectory(String arcFileDir, String outputPath) {

        StringBuilder xmlNodeBuilder = new StringBuilder();

        xmlNodeBuilder.append("<Jobs>");

        File[] jobArcFiles = new File(arcFileDir).listFiles();
        Arrays.sort(jobArcFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        for (File workloadFile : jobArcFiles) {
            if(workloadFile.isFile()) {
                if(!workloadFile.getName().equals("list.xml")) {
                    System.out.println(workloadFile.getAbsolutePath());
                    xmlNodeBuilder.append(generateJobXmlNode(workloadFile.getAbsolutePath()));
                }

            }
        }

        xmlNodeBuilder.append("</Jobs>");

        try {
            PrintWriter writer;
            writer = new PrintWriter(outputPath, "UTF-8");
            writer.print(XMLFormatter.format(xmlNodeBuilder.toString()));
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateJobXmlNode(String jobArcFilePath) {

        String jobXmlNode = null;

        SAXBuilder builder = new SAXBuilder();
        File jobArcFile = new File(jobArcFilePath);

        try {

            Document document = (Document) builder.build(jobArcFile);
            Element rootNode = document.getRootElement();

            String jobName = rootNode.getAttribute("name").getValue();
            String jobType = rootNode.getAttribute("type").getValue();
            String jobUuid = rootNode.getAttribute("uuid").getValue();

            String jobUrl = "archive/" + new File(jobArcFilePath).getName();
            String jobDescription = jobName;

            StringBuilder xmlNodeBuilder = new StringBuilder();
            xmlNodeBuilder.append(String.format("<Job uuid=\"%s\" name=\"%s\" type=\"%s\">", jobUuid, jobName, jobType));
            xmlNodeBuilder.append(String.format("<Url>%s</Url>", jobUrl));
            xmlNodeBuilder.append(String.format("<Description>%s</Description>", jobDescription));
            xmlNodeBuilder.append(String.format("</Job>"));
            jobXmlNode = xmlNodeBuilder.toString();


        } catch (IOException io) {
            System.out.println(io.getMessage());
        } catch (JDOMException jdomex) {
            System.out.println(jdomex.getMessage());
        }

        if(jobXmlNode == null) {
            System.out.println(String.format("Failed to read job archive at %s.", jobArcFilePath));
        }

        return jobXmlNode;
    }


}
