/*
 * Copyright (C) 2007-2015 Syed Asad Rahman <asad @ ebi.ac.uk>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package aamtool;

import static aamtool.Helper.getHeader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.openscience.cdk.interfaces.IReaction;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import uk.ac.ebi.reactionblast.mechanism.ReactionMechanismTool;

/**
 * @contact Syed Asad Rahman, EMBL-EBI, Cambridge, UK.
 * @author Syed Asad Rahman <asad @ ebi.ac.uk>
 */
public class ReactionDecoder extends Annotator {

    private final static boolean DEBUG = false;

    private void FormatXMLToFile(Document doc, String fileName) throws TransformerConfigurationException, TransformerException {

        // write xml to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        doc.setXmlStandalone(true);
        DOMSource source = new DOMSource(doc);

        /*
         Write to a File
         */
        File file = new File(fileName + ".xml");
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);

        System.out.println("Output is presented in xml format: " + file.getAbsolutePath());

        if (DEBUG) {
            // Show output on console during development
            result = new StreamResult(System.out);
            transformer.transform(source, result);
        }
    }

    private void FormatTextToFile(StringBuilder doc, String fileName) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        File file = new File(fileName + ".txt");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
            writer.write(doc.toString());
        }

        System.out.println("Output is presented in text format: " + file.getAbsolutePath());

        if (DEBUG) {
            // Show output on console during development
            System.out.println(doc.toString());
        }
    }

    public ReactionDecoder() {
        super();
    }

    /**
     * @param args the command line areactionWithLayoutgumentheaderString
     */
    public static void main(String[] args) {
        try {
            CommandLineOptions cmd = new CommandLineOptions();
            Options createAAMOptions = cmd.createAAMOptions();
            Options createCompareOptions = cmd.createCompareOptions();
            Options createAnnotateOptions = cmd.createAnnotateOptions();
            Options createSimilarityOptions = cmd.createSimilarityOptions();

            PosixParser parser1 = new PosixParser();
            CommandLine aamLine = parser1.parse(createAAMOptions, args, true);
            PosixParser parser2 = new PosixParser();
            CommandLine compareLine = parser2.parse(createCompareOptions, args, true);
            PosixParser parser4 = new PosixParser();
            CommandLine annotateLine = parser4.parse(createAnnotateOptions, args, true);
            PosixParser parser3 = new PosixParser();
            CommandLine similarityLine = parser3.parse(createSimilarityOptions, args, true);

            /*
             * Print the Header
             */
            getHeader();

            if (aamLine.hasOption('j') && aamLine.getOptionValue("j").equalsIgnoreCase("AAM")
                    && aamLine.hasOption('Q') && aamLine.hasOption('q') && aamLine.hasOption('f')) {

                System.out.println("-- AAM --");
                ReactionDecoder rxn = new ReactionDecoder();
                rxn.AAMTask(aamLine, createAAMOptions);

            } else if (compareLine.hasOption('j') && compareLine.getOptionValue("j").equalsIgnoreCase("COMPARE")
                    && compareLine.hasOption('Q') && compareLine.hasOption('q')
                    && compareLine.hasOption('T') && compareLine.hasOption('t')
                    && compareLine.hasOption('f')) {

                System.out.println("-- COMPARE --");
                ReactionDecoder rxn = new ReactionDecoder();
                rxn.CompareTask(compareLine, createCompareOptions);

            } else if (annotateLine.hasOption('j') && annotateLine.getOptionValue("j").equalsIgnoreCase("ANNOTATE")
                    && annotateLine.hasOption('Q') && annotateLine.hasOption('q')
                    && annotateLine.hasOption('f')) {

                System.out.println("-- ANNOTATE --");
                ReactionDecoder rxn = new ReactionDecoder();
                rxn.AnnotateTask(annotateLine, createAnnotateOptions);

            } else if (similarityLine.hasOption('j') && similarityLine.getOptionValue("j").equalsIgnoreCase("SIMILARITY")
                    && similarityLine.hasOption('Q') && similarityLine.hasOption('q')) {

                System.out.println("-- SIMILARITY --");
                ReactionDecoder rxn = new ReactionDecoder();
                rxn.SimilarityTask(similarityLine, createSimilarityOptions);

            } else if (aamLine.hasOption('j') && aamLine.getOptionValue("j").equalsIgnoreCase("AAM")) {
                System.out.println("-- AAM USAGE --");
                printHelp(System.out, createAAMOptions);
            } else if (compareLine.hasOption('j') && compareLine.getOptionValue("j").equalsIgnoreCase("COMPARE")) {
                System.out.println("-- REACTION COMPARE USAGE --");
                printHelp(System.out, createCompareOptions);
            } else if (compareLine.hasOption('j') && compareLine.getOptionValue("j").equalsIgnoreCase("SIMILARITY")) {
                System.out.println("-- REACTION SIMILARITY USAGE --");
                printHelp(System.out, createSimilarityOptions);
            } else if (compareLine.hasOption('j') && compareLine.getOptionValue("j").equalsIgnoreCase("ANNOTATE")) {
                System.out.println("-- REACTION ANNOTATION USAGE --");
                printHelp(System.out, createAnnotateOptions);
            } else {
                System.out.println("-- REACTION DECODER HELP --");
                Map<String, Options> options = new TreeMap<>();
                options.put("Atom-Atom Mapping (AAM Tool)", createAAMOptions);
                options.put("Reaction Annotation (RA Tool)", createAnnotateOptions);
                options.put("Reaction Comparison (RC Tool)", createCompareOptions);
                options.put("Reaction Similarity (RM Tool)", createSimilarityOptions);
                printHelp(options, 80, "EC-BLAST", "End of Help",
                        5, 3, true, System.out);
            }
        } catch (ParseException ex) {
            Logger.getLogger(ReactionDecoder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ReactionDecoder.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void AAMTask(CommandLine aamLine, Options createAAMOptions)
            throws Exception {

        // TODO code application logic here
        String optionValue = aamLine.getOptionValue("q");

        if (aamLine.hasOption('m')) {
            REPORT_ALL_MAPPINGS = true;
        }

        if (aamLine.hasOption('g')) {
            GENERATE_IMAGE = false;
            GENERATE_AAMIMAGE = true;
        }

        if (aamLine.hasOption('p')) {
            PREFIX = aamLine.getOptionValue("p");
        }

        if (aamLine.hasOption('u')) {
            REMAP = false;
        }

        IReaction reaction = null;

        switch (aamLine.getOptionValue("Q")) {
            case "SMI":
                if (optionValue.contains(">>")) {
                    List<IReaction> parseReactions = parseReactionSMILES(optionValue);
                    if (parseReactions.iterator().hasNext()) {
                        reaction = parseReactions.iterator().next();
                    }
                } else {
                    System.err.println("Not a valid reaction SMILES");
                }
                break;
            case "RXN":
                List<IReaction> parseReactions = parseRXN(optionValue);
                if (parseReactions.iterator().hasNext()) {
                    reaction = parseReactions.iterator().next();
                }
                break;
            default:
                displayBlankLines(2, System.out);
                System.out.println("-- USAGE --");
                printHelp(System.out, createAAMOptions);
                break;
        }
        if (reaction == null) {
            return;
        }

        String jobFileName;

        if (!PREFIX.isEmpty()) {
            jobFileName = PREFIX + "_ECBLAST_" + reaction.getID() + "_AAM";
        } else {
            jobFileName = "ECBLAST_" + reaction.getID() + "_AAM";
        }

        ReactionMechanismTool annotateReaction = getReactionMechanismTool(reaction, REMAP);
        boolean writeFiles = writeFiles(jobFileName, annotateReaction);

        if (writeFiles && aamLine.getOptionValue("f").equalsIgnoreCase("XML")) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root element
            org.w3c.dom.Document doc = docBuilder.newDocument();

            org.w3c.dom.Element rootElement = doc.createElement("EC_BLAST");
            doc.appendChild(rootElement);
            annotateReactionAsXML(annotateReaction, jobFileName, doc, rootElement);
            FormatXMLToFile(doc, jobFileName);
            System.out.println("XML File saved!");

        } else if (writeFiles && aamLine.getOptionValue("f").equalsIgnoreCase("TEXT")) {
            StringBuilder sb = new StringBuilder();
            annotateReactionAsText(annotateReaction, reaction.getID() + "_AAM", sb);
            FormatTextToFile(sb, jobFileName);
        } else if (writeFiles && aamLine.getOptionValue("f").equalsIgnoreCase("BOTH")) {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root element
            org.w3c.dom.Document doc = docBuilder.newDocument();

            org.w3c.dom.Element rootElement = doc.createElement("EC_BLAST");
            doc.appendChild(rootElement);
            annotateReactionAsXML(annotateReaction, jobFileName, doc, rootElement);

            StringBuilder sb = new StringBuilder();
            annotateReactionAsText(annotateReaction, jobFileName, sb);

            /*
             Write XML and TEXT file
             */
            FormatTextToFile(sb, jobFileName);
            FormatXMLToFile(doc, jobFileName);
            System.out.println("XML File saved!");

        } else {
            displayBlankLines(2, System.out);
            System.out.println("-- USAGE --");
            printHelp(System.out, createAAMOptions);
        }
    }

    private void CompareTask(CommandLine compareLine, Options createCompareOptions)
            throws ParserConfigurationException, Exception {

        String optionValueQ = compareLine.getOptionValue("q");
        String optionValueT = compareLine.getOptionValue("t");

        /*
         Report bond changes and reaction centres
         */
        REPORT_PATTERNS = true;

        if (compareLine.hasOption("g")) {
            GENERATE_IMAGE = true;
            GENERATE_AAMIMAGE = false;
        }
        if (compareLine.hasOption('p')) {
            PREFIX = compareLine.getOptionValue("p");
        }

        if (compareLine.hasOption('u')) {
            REMAP = false;
        }

        if (compareLine.hasOption('x')) {
            REPORT_MMP = true;
        }

        IReaction queryReaction = null;

        switch (compareLine.getOptionValue("Q")) {

            case "SMI":
                if (optionValueQ.contains(">>")) {
                    List<IReaction> parseReactions = parseReactionSMILES(optionValueQ);
                    if (parseReactions.iterator().hasNext()) {
                        queryReaction = parseReactions.iterator().next();
                    }
                } else {
                    System.err.println("Not a valid reaction SMILES");
                }
                break;

            case "RXN":
                List<IReaction> parseReactions = parseRXN(optionValueQ);
                if (parseReactions.iterator().hasNext()) {
                    queryReaction = parseReactions.iterator().next();
                }
                break;
            default:
                displayBlankLines(2, System.out);
                System.out.println("-- USAGE --");
                printHelp(System.out, createCompareOptions);
                break;
        }
        IReaction targetReaction = null;
        switch (compareLine.getOptionValue("T")) {

            case "SMI":
                if (optionValueT.contains(">>")) {
                    List<IReaction> parseReactions = parseReactionSMILES(optionValueT);
                    if (parseReactions.iterator().hasNext()) {
                        targetReaction = parseReactions.iterator().next();
                    }
                } else {
                    System.err.println("Not a valid reaction SMILES");
                }
                break;

            case "RXN":
                List<IReaction> parseReactions = parseRXN(optionValueT);
                if (parseReactions.iterator().hasNext()) {
                    targetReaction = parseReactions.iterator().next();
                }
                break;
            default:
                displayBlankLines(2, System.out);
                System.out.println("-- USAGE --");
                printHelp(System.out, createCompareOptions);
                break;
        }

        if (queryReaction == null || targetReaction == null) {
            return;
        }

        String jobFileName;
        if (!PREFIX.isEmpty()) {
            jobFileName = PREFIX + "_ECBLAST_" + queryReaction.getID() + "_" + targetReaction.getID() + "_COMPARE";
        } else {
            jobFileName = "ECBLAST_" + queryReaction.getID() + "_" + targetReaction.getID() + "_COMPARE";
        }

        String jobFileNameQuery;
        if (!PREFIX.isEmpty()) {
            jobFileNameQuery = PREFIX + "_ECBLAST_" + queryReaction.getID() + "_Query";
        } else {
            jobFileNameQuery = "ECBLAST_" + queryReaction.getID() + "_Query";
        }

        String jobFileNameTarget;
        if (!PREFIX.isEmpty()) {
            jobFileNameTarget = PREFIX + "_ECBLAST_" + targetReaction.getID() + "_Target";
        } else {
            jobFileNameTarget = "ECBLAST_" + targetReaction.getID() + "_Target";
        }

        ReactionMechanismTool annotateReactionQ;
        ReactionMechanismTool annotateReactionT;

        annotateReactionQ = getReactionMechanismTool(queryReaction, REMAP);
        annotateReactionT = getReactionMechanismTool(targetReaction, REMAP);
        boolean writeFiles1 = writeFiles(jobFileNameQuery, annotateReactionQ);
        boolean writeFiles2 = writeFiles(jobFileNameTarget, annotateReactionT);

        boolean writeFiles = writeFiles1 && writeFiles2;

        if (writeFiles && annotateReactionQ != null && annotateReactionT != null) {
            if (compareLine.getOptionValue("f").equalsIgnoreCase("XML")) {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                // root element
                org.w3c.dom.Document doc = docBuilder.newDocument();
                org.w3c.dom.Element rootElement = doc.createElement("EC_BLAST");
                doc.appendChild(rootElement);
                compareRXNXML(annotateReactionQ, jobFileNameQuery, annotateReactionT, jobFileNameTarget, doc, rootElement);
                FormatXMLToFile(doc, jobFileName);
                System.out.println("XML File saved!");
            } else if (writeFiles && compareLine.getOptionValue("f").equalsIgnoreCase("TEXT")) {
                StringBuilder sb = new StringBuilder();
                compareRXNText(annotateReactionQ, jobFileNameQuery, annotateReactionT, jobFileNameTarget, sb);
                FormatTextToFile(sb, jobFileName);
            } else if (writeFiles && compareLine.getOptionValue("f").equalsIgnoreCase("BOTH")) {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                // root element for XML
                org.w3c.dom.Document doc = docBuilder.newDocument();
                org.w3c.dom.Element rootElement = doc.createElement("EC_BLAST");
                doc.appendChild(rootElement);
                compareRXNXML(annotateReactionQ, jobFileNameQuery, annotateReactionT, jobFileNameTarget, doc, rootElement);

                // root element for TEXT
                StringBuilder sb = new StringBuilder();
                compareRXNText(annotateReactionQ, jobFileNameQuery, annotateReactionT, jobFileNameTarget, sb);

                /*
                 Write XML and TEXT file
                 */
                FormatTextToFile(sb, jobFileName);
                FormatXMLToFile(doc, jobFileName);
                System.out.println("XML File saved!");
            } else {
                displayBlankLines(2, System.out);
                System.out.println("-- USAGE --");
                printHelp(System.out, createCompareOptions);
            }
        }
    }

    private void SimilarityTask(CommandLine similarityLine, Options createSimilarityOptions)
            throws ParserConfigurationException, Exception {
        if (similarityLine.hasOption('p')) {
            PREFIX = similarityLine.getOptionValue("p");
        }

        if (similarityLine.hasOption('u')) {
            REMAP = false;
        }

        String optionValueQ = similarityLine.getOptionValue("q");
        System.out.println("SimilarityTask");
        List<IReaction> reactions = new ArrayList<>();

        switch (similarityLine.getOptionValue("Q")) {

            case "SMI":
                reactions = parseReactionSMILES(optionValueQ);
                break;
            case "RXN":
                reactions = parseRXN(optionValueQ);
                break;
            default:
                displayBlankLines(2, System.out);
                System.out.println("-- USAGE --");
                printHelp(System.out, createSimilarityOptions);
                break;
        }

        if (reactions.isEmpty()) {
            displayBlankLines(2, System.out);
            System.out.println("-- USAGE --");
            printHelp(System.out, createSimilarityOptions);
            return;
        }

        String jobFileName;
        if (!PREFIX.isEmpty()) {
            jobFileName = PREFIX + "_ECBLAST_" + "SIMILARITY";
        } else {
            jobFileName = "ECBLAST_" + "SIMILARITY";
        }

        List<SimilarityResult> results = new ArrayList<>();
        Map<IReaction, ReactionMechanismTool> mech = new HashMap<>();
        for (IReaction q : reactions) {
            ReactionMechanismTool annotateReactionQ;
            annotateReactionQ = getReactionMechanismTool(q, REMAP);
            mech.put(q, annotateReactionQ);
        }

        List<IReaction> names = new LinkedList<>(mech.keySet());

        for (int i = 0; i < names.size(); i++) {
            ReactionMechanismTool annotateReactionQ = mech.get(names.get(i));
            for (int j = 0; j < names.size(); j++) {
                ReactionMechanismTool annotateReactionT = mech.get(names.get(j));
                Map<String, String> similarityReactions = similarityReactions(annotateReactionQ, names.get(i).getID(), annotateReactionT, names.get(j).getID());
                SimilarityResult similarityResult = new SimilarityResult(names.get(i).getID(), names.get(j).getID(), similarityReactions);
                results.add(similarityResult);
                Logger.getLogger(ReactionDecoder.class.getName()).log(Level.INFO, ("Scoring: " + names.get(i).getID() + "_" + names.get(j).getID()));
            }
            i++;
        }
        mech.clear();
        writeSimilarityMatrix(results, jobFileName);
        System.out.println("MATRIX files (.mat) saved with prefix " + jobFileName + "!");
    }

    private void AnnotateTask(CommandLine annotateLine, Options createAnnotateOptions)
            throws TransformerException,
            CloneNotSupportedException,
            FileNotFoundException,
            IOException,
            Exception {

        String optionValue = annotateLine.getOptionValue("q");

        /*
         Report bond changes and reaction centres
         */
        REPORT_PATTERNS = true;

        if (annotateLine.hasOption("g")) {
            GENERATE_IMAGE = true;
            GENERATE_AAMIMAGE = false;
        }
        if (annotateLine.hasOption('p')) {
            PREFIX = annotateLine.getOptionValue("p");
        }

        if (annotateLine.hasOption('x')) {
            REPORT_MMP = true;
        }

        if (annotateLine.hasOption('u')) {
            REMAP = false;
        }
        IReaction reaction = null;

        switch (annotateLine.getOptionValue("Q")) {
            case "SMI":
                if (optionValue.contains(">>")) {
                    List<IReaction> parseReactions = parseReactionSMILES(optionValue);
                    if (parseReactions.iterator().hasNext()) {
                        reaction = parseReactions.iterator().next();
                    }
                } else {
                    System.err.println("Not a valid reaction SMILES");
                }
                break;
            case "RXN":
                List<IReaction> parseReactions = parseRXN(optionValue);
                if (parseReactions.iterator().hasNext()) {
                    reaction = parseReactions.iterator().next();
                }
                break;
            default:
                displayBlankLines(2, System.out);
                System.out.println("-- USAGE --");
                printHelp(System.out, createAnnotateOptions);
                break;
        }
        if (reaction == null) {
            return;
        }

        String jobFileName;

        if (!PREFIX.isEmpty()) {
            jobFileName = PREFIX + "_ECBLAST_" + reaction.getID() + "_ANNONATE";
        } else {
            jobFileName = "ECBLAST_" + reaction.getID() + "_ANNONATE";
        }

        ReactionMechanismTool annotateReaction = getReactionMechanismTool(reaction, REMAP);
        boolean writeFiles = writeFiles(jobFileName, annotateReaction);

        if (writeFiles && annotateLine.getOptionValue("f").equalsIgnoreCase("XML")) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root element
            org.w3c.dom.Document doc = docBuilder.newDocument();

            org.w3c.dom.Element rootElement = doc.createElement("EC_BLAST");
            doc.appendChild(rootElement);
            annotateReactionAsXML(annotateReaction, jobFileName, doc, rootElement);
            FormatXMLToFile(doc, jobFileName);
            System.out.println("XML File saved!");

        } else if (writeFiles && annotateLine.getOptionValue("f").equalsIgnoreCase("TEXT")) {
            StringBuilder sb = new StringBuilder();
            annotateReactionAsText(annotateReaction, reaction.getID() + "_AAM", sb);
            FormatTextToFile(sb, jobFileName);
        } else if (writeFiles && annotateLine.getOptionValue("f").equalsIgnoreCase("BOTH")) {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root element
            org.w3c.dom.Document doc = docBuilder.newDocument();

            org.w3c.dom.Element rootElement = doc.createElement("EC_BLAST");
            doc.appendChild(rootElement);
            annotateReactionAsXML(annotateReaction, jobFileName, doc, rootElement);

            StringBuilder sb = new StringBuilder();
            annotateReactionAsText(annotateReaction, jobFileName, sb);

            /*
             Write XML and TEXT file
             */
            FormatTextToFile(sb, jobFileName);
            FormatXMLToFile(doc, jobFileName);
            System.out.println("XML File saved!");

        } else {
            displayBlankLines(2, System.out);
            System.out.println("-- USAGE --");
            printHelp(System.out, createAnnotateOptions);
        }
    }

}