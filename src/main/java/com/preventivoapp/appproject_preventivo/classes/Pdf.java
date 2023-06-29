package com.preventivoapp.appproject_preventivo.classes;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pdf {
    private float characterDimension;
    private float characterTitleDimension;
    private float spaceSideShort;
    private float spaceSideLong;
    private float spaceTop;
    private float leading;
    private PDFont font;
    private final String pathFile;

    public Pdf(String pathFile, boolean standard) {
        this.pathFile = pathFile;
        if (standard) {
            this.characterDimension = 11;
            this.characterTitleDimension = 15;
            this.spaceSideShort = 25;
            this.spaceSideLong = 40;
            this.spaceTop = 42;
            this.leading = 1;
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

    public float getLeading() {
        return leading;
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
            float height = newPage.getCropBox().getHeight();
            float width = newPage.getCropBox().getWidth();
            //heading
            float yHead = addHead(contentStream, quote.getPerson(), quote.getQuoteDate(), getSpaceSideShort(), height - getSpaceTop(), getLeading(), getCharacterDimension(), width) + getSpaceTop();
            //price table
                //description
            String headTable = "Sottoponiamo alla vostra cortese attenzione il seguente preventivo e piano di cura";
            float yDescription = addParagraph(contentStream, headTable, getSpaceSideLong(), height - yHead - getCharacterDimension() * 3, width - getSpaceSideLong(), 1, getCharacterDimension()) + getCharacterDimension() * 3;
                //table
            float yTable = addTable(quote, contentStream, getSpaceSideShort(), height - yHead - yDescription - getCharacterDimension() * 2, getLeading(), getCharacterDimension(), width) + getCharacterDimension() * 2;
            //bottom page
                //payment
            float yPayment = addPaymentDescription(contentStream, getSpaceSideLong(), height - yHead - yDescription - yTable - getCharacterDimension() * 3, getLeading(), getCharacterDimension(), width) + getCharacterDimension() * 3;
                //sign
            float ySign = addSign(contentStream, getSpaceSideLong(), height - yHead - yDescription - yTable - yPayment - getCharacterDimension() * 3, getLeading(), getCharacterDimension(), width);
            //save PDF
            contentStream.close();
            document.addPage(newPage);
            document.save(getPathFile());
            document.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private float stringWidth(String string, float characterDimension, PDFont font) throws IOException{
        return font.getStringWidth(string) / 1000 * characterDimension;
    }

    private void addLine(PDPageContentStream contentStream, String line, float x, float y, float leading, float fontDimension, boolean alignment) throws IOException {
        contentStream.beginText();
        contentStream.setFont(getFont(), fontDimension);
        contentStream.setLeading(fontDimension + leading);
        if (!alignment) {
            x -= stringWidth(line, fontDimension, getFont());
        }
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(line);
        contentStream.endText();
    }

    private float addParagraph(PDPageContentStream contentStream, String string, float x, float y, float xMax, float leading, float fontDimension) throws  IOException {
        float writtenLines = 0;
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
        contentStream.setLeading(fontDimension + leading);
        contentStream.newLineAtOffset(x, y);
        float lineDimension = x;
        for (String word: words){
            if (lineDimension + stringWidth(word + " ", fontDimension, getFont()) < xMax){
                lineDimension += stringWidth(word + " ", fontDimension, getFont());
                contentStream.showText(word + " ");
            } else {
                contentStream.newLine();
                contentStream.showText(word + " ");
                lineDimension = x + stringWidth(word + " ", fontDimension, getFont());
                writtenLines++;
            }
        }
        contentStream.endText();
        contentStream.moveTo(0, 0);
        return writtenLines * (fontDimension + leading);
    }

    private float addHead(PDPageContentStream contentStream, Person person, LocalDate date, float x, float y, float leading, float characterDimension, float width) throws IOException {
        float writtenLines = 0;
        //sx
        addLine(contentStream, "Studio dentistico associato", x, y, leading, characterDimension, true);
        writtenLines += characterDimension + leading;
        addLine(contentStream, "Dott. Della Bella A. - Dott. Scarfone M.", x, y - writtenLines * 1, leading, characterDimension, true);
        addLine(contentStream, "Via Martiri della Libertà n.21", x, y - writtenLines * 2, leading, characterDimension, true);
        addLine(contentStream, "Novi di Modena tel. 059-677050", x, y - writtenLines * 3, leading, characterDimension, true);
        //dx
        addLine(contentStream, date.toString(), width - getSpaceSideShort(), y, leading, characterDimension, false);
        addLine(contentStream, "Sig. " + person.getFirstName()+ " " + person.getLastName(), width - getSpaceSideShort(), y - writtenLines * 3, leading, characterDimension, false);
        return writtenLines * 3;
    }

    private float addTable(Quote quote, PDPageContentStream contentStream, float x, float y, float leading, float characterDimension, float width) throws IOException{
        float writtenLines = 0;
        //column name
        addLine(contentStream, "Descrizione", x, y, leading, characterDimension, true);
        addLine(contentStream, "Prezzo", width / 2, y, leading, characterDimension, true);
        addLine(contentStream, "Importo", (width / 2) + (width / 4), y, leading, characterDimension, true);
        writtenLines += characterDimension + leading;
        //fill columns
        double total = 0;
        for (ServiceDetail service: quote.getServicesChosen()){
            StringBuilder description = new StringBuilder();
            double price = service.getChosenService().getServicePrice();
            description.append(service.getChosenService().getServiceName());
            if (service.getChosenService().getServicePriceForTooth() != 0) {
                description.append(" (").append(service.showTeeth()).append(")");
                price = service.getChosenService().getServicePriceForTooth();
            }
            addParagraph(contentStream, description.toString(), x, y - writtenLines, width / 2, leading, characterDimension);
            addLine(contentStream, Double.toString(price), (width / 2) + (width / 4) - x, y - writtenLines, leading, characterDimension, false);
            if (service.getChosenService().getServicePriceForTooth() != 0){
                price *= service.getChosenTeeth().size();
                price = round(price);
                addLine(contentStream, Double.toString(price), width - x, y - writtenLines, leading, characterDimension, false);
            } else {
                price *= service.getTimeSelected();
                price = round(price);
                addLine(contentStream, Double.toString(price), width - x, y - writtenLines, leading, characterDimension, false);
            }
            writtenLines += characterDimension + leading;
            total += price;
            System.out.println(price);
        }
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.addRect(getSpaceSideShort(), y - writtenLines + 5, width - getSpaceSideShort() * 2, 1f);
        contentStream.fill();
        writtenLines += 11;
        addLine(contentStream, "Totale:", (width / 2) + (width / 4), y - writtenLines, leading, characterDimension, true);
        addLine(contentStream, Double.toString(total), width - x, y - writtenLines, leading, characterDimension, false);
        return writtenLines;
    }
    private double round(double price){
        price *= 100;
        price = Math.round(price);
        return price / 100;
    }
    private float addPaymentDescription(PDPageContentStream contentStream, float x, float y, float leading, float characterDimension, float width) throws IOException {
        float writtenLines = 0;
        String string = "Pagamento:";
        addLine(contentStream, string, x, y, leading, characterDimension, true);
        addParagraph(contentStream, "50% inizio lavori", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        addParagraph(contentStream, "25% metà lavori", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        addParagraph(contentStream, "25% fine lavori", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        addParagraph(contentStream, "Pianificazione pagamenti condivisa con il paziente", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        return writtenLines;
    }

    private float addSign(PDPageContentStream contentStream, float x, float y, float leading, float characterDimension, float width) throws IOException {
        float writtenLines = 0;
        addLine(contentStream, "Firma per accettazione", x, y, leading, characterDimension, true);
        writtenLines += characterDimension + leading;
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.addRect(getSpaceSideLong(), y - writtenLines - characterDimension * 2, width / 2 - x, 1f);
        contentStream.fill();
        writtenLines += 1 + characterDimension * 2;
        return writtenLines;
    }

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
