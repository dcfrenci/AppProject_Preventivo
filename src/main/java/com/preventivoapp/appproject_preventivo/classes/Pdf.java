package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.color.PDCalGray;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pdf {
    private float characterDimension;
    private float characterTitleDimension;
    private float spaceSideShort;
    private float spaceSideLong;
    private float spaceTop;
    private PDFont font;
    private String pathFile;

    public Pdf(String pathFile, boolean standard) {
        this.pathFile = pathFile;
        if (standard) {
            this.characterDimension = 11;
            this.characterTitleDimension = 15;
            this.spaceSideShort = 25;
            this.spaceSideLong = 40;
            this.spaceTop = 42;
            this.font = PDType1Font.HELVETICA;
        }
    }

    public Pdf(float characterDimension, float characterTitleDimension, float spaceSideShort, float spaceSideLong, float spaceTop, PDFont font, String pathFile) {
        this.characterDimension = characterDimension;
        this.characterTitleDimension = characterTitleDimension;
        this.spaceSideShort = spaceSideShort;
        this.spaceSideLong = spaceSideLong;
        this.spaceTop = spaceTop;
        this.font = font;
        this.pathFile = pathFile;
    }

    public float getCharacterDimension() {
        return characterDimension;
    }

    public float getCharacterTitleDimension() {
        return characterTitleDimension;
    }

    public float getSpaceSideShort() {
        return spaceSideShort;
    }

    public float getSpaceSideLong() {
        return spaceSideLong;
    }

    public float getSpaceTop() {
        return spaceTop;
    }

    public PDFont getFont() {
        return font;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void createQuote(Quote quote){
        PDDocument document = new PDDocument();
        PDPage newPage = new PDPage();
        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, newPage);
            float position = 0;
            float height = newPage.getCropBox().getHeight();
            float width = newPage.getCropBox().getWidth();
            //heading
            contentStream.setFont(getFont(), getCharacterDimension());
            contentStream.setLeading(getCharacterDimension() + 1);
                //sx
            contentStream.beginText();
            contentStream.newLineAtOffset(getSpaceSideShort(), height - getSpaceTop());
            contentStream.showText("Studio dentistico associato");
            contentStream.newLine();
            contentStream.showText("Dott. Della Bella A. - Dott. Scarfone M.");
            contentStream.newLine();
            contentStream.showText("Via Martiri della Libertà n.21");
            contentStream.newLine();
            contentStream.showText("Novi di Modena tel. 059-677050");
            contentStream.endText();
                //dx
            contentStream.beginText();
            contentStream.newLineAtOffset(width - getSpaceSideShort() - getFont().getStringWidth("Preventivo del: " + quote.getQuoteDate().toString()) / 1000 * characterDimension, height - getSpaceTop());
            contentStream.showText("Preventivo del: " + quote.getQuoteDate().toString());
            contentStream.endText();
            contentStream.beginText();
            contentStream.newLineAtOffset(width - getSpaceSideShort() - getFont().getStringWidth("Sig. " + quote.getPerson().getLastName() + quote.getPerson().getFirstName() + 1) / 1000 * getCharacterDimension(), height - getSpaceTop() - getCharacterDimension() * 3 + 2);
            contentStream.showText("Sig. " + quote.getPerson().getFirstName() + " " + quote.getPerson().getLastName());
            contentStream.endText();
            //price table
            String headTable = "Sottoponiamo alla vostra cortese attenzione il seguente preventivo e piano di cura";
            float x = height - getSpaceTop() - 3 - getCharacterDimension() * 6;
            int n = addParagraph(contentStream, headTable, x, getSpaceSideLong(), width - getSpaceSideLong(), 1, getCharacterDimension());
            n = addParagraph(contentStream, "ciao", height - getSpaceTop(), getSpaceSideLong(), width, 1, getCharacterDimension());
            //bottom page

            //save PDF
            //contentStream.endText();
            contentStream.close();
            document.addPage(newPage);
            document.save(getPathFile());
            document.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void addLine(PDPageContentStream contentStream, ArrayList<String> lines, float x, float y, float leading, float fontDimension) throws IOException {
        contentStream.beginText();
        contentStream.setFont(getFont(), fontDimension);
        contentStream.setLeading(leading);
        contentStream.newLineAtOffset(x, y);
        for (String line: lines){
            contentStream.showText(line);
            contentStream.newLine();
        }
        contentStream.endText();
    }

    private int addParagraph(PDPageContentStream contentStream, String string, float x, float y, float yMax, float leading, float fontDimension) throws  IOException {
        int writtenLines = 0;
        //create the array list of words from the string
        ArrayList<String> words = new ArrayList<>();
        while (string.length() > 0){
            if (!string.contains(" ")) {
                words.add(string);
                break;
            }
            else {
                String word = string.substring(0, string.indexOf(" "));
                words.add(word);
                string = string.substring(string.indexOf(" ") + 1);
            }
        }
        //write
        contentStream.beginText();
        contentStream.setFont(getFont(), fontDimension);
        contentStream.setLeading(leading);
        contentStream.newLineAtOffset(x, y);
        float lineDimension = 0;
        for (String word: words){
            if (lineDimension + getFont().getStringWidth(word + 1) / 1000 * fontDimension < yMax - y){
                lineDimension += getFont().getStringWidth(word + 1) / 1000 * fontDimension;
                contentStream.showText(word);
            } else {
                contentStream.newLine();
                contentStream.showText(word + " ");
                lineDimension = 0;
                writtenLines++;
            }
        }
        contentStream.endText();
        contentStream.moveTo(0, 0);
        return writtenLines;
    }
/*
    public static void main(String[] args){
        PDDocument document = new PDDocument();
        PDPage newPage = new PDPage();
        try {
            float f = newPage.getCropBox().getHeight();
            System.out.println("height =" + f);
            PDPageContentStream contentStream = new PDPageContentStream(document, newPage);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 15);
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Studio dentistico associato");
            contentStream.newLineAtOffset(0, - 20);
            contentStream.showText("Dott. Della Bella A. - Dott. Scarfone M.");
            contentStream.newLineAtOffset(0, - 20);
            contentStream.showText("Via Martiri della Libertà n.21");
            contentStream.newLineAtOffset(0, - 20);
            contentStream.showText("Novi di Modena tel. 059-677050");
            contentStream.endText();
            contentStream.close();
            document.addPage(newPage);
            document.save(new File("C:\\Users\\Studi\\Desktop\\Product_Quote\\quote\\quote\\new.pdf"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }*/
    public static void main(String [] args){
        List<ServiceDetail> serviceDetails = new ArrayList<>();
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service 1"), 0.0, 175.0), List.of(18, 19, 20), 1));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service 2"), 750.0, 0.0), List.of(18, 19, 20), 3));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service with a very long name to write down"), 0.0, 175.0), List.of(18, 19, 20), 1));
        Quote quote = new Quote(new Person(new SimpleStringProperty("Francesco"), new SimpleStringProperty("Della Casa")), serviceDetails, new SimpleObjectProperty<>(LocalDate.now()));
        Pdf pdf = new Pdf("C:\\Users\\Studi\\Desktop\\Product_Quote\\quote\\quote\\temp.pdf", true);
        pdf.createQuote(quote);
    }
}
