package com.preventivoapp.appproject_preventivo.classes;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.*;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.MIN_NORMAL;

public class Pdf {
    private final float characterDimension;
    private final float spaceSideShort;
    private final float spaceSideLong;
    private final float spaceTop;
    private float leading;
    private final PDFont font;
    private final String pathFile;
    private List<String> pdfHead;
    private List<String> pdfDescription;
    private List<String> pdfPayment;

    /*public Pdf(String pathFile, boolean standard) {
        this.pathFile = pathFile;
        if (standard) {
            this.characterDimension = 11;
            this.spaceSideShort = 25;
            this.spaceSideLong = 40;
            this.spaceTop = 42;
            this.leading = 1;
            this.font = PDType1Font.HELVETICA;
        }
    }*/

    public Pdf(float characterDimension, float leading, float spaceTop, float spaceSideLong, float spaceSideShort, PDFont font, String pathFile){
        if (characterDimension == MIN_NORMAL) this.characterDimension = 11;
        else this.characterDimension = characterDimension;
        if (leading == MIN_NORMAL) this.leading = 1;
        else this.leading = leading;
        if (spaceTop == MIN_NORMAL) this.spaceTop = 42;
        else this.spaceTop = spaceTop;
        if (spaceSideLong == MIN_NORMAL) this.spaceSideLong = 40;
        else this.spaceSideLong = spaceSideLong;
        if (spaceSideShort == MIN_NORMAL) this.spaceSideShort = 40;
        else this.spaceSideShort = spaceSideShort;
        if (font == null) this.font = PDType1Font.HELVETICA;
        else this.font = font;
        this.pathFile = pathFile;
        /*this.pdfHead = new ArrayList<>();
        this.pdfDescription = new ArrayList<>();
        this.pdfPayment = new ArrayList<>();*/
    }

    public Pdf(Pdf origin, String newPath){
        //copy from the origin
        this.characterDimension = origin.getCharacterDimension();
        this.spaceTop = origin.getSpaceTop();
        this.spaceSideLong = origin.getSpaceSideLong();
        this.spaceSideShort = origin.getSpaceSideShort();
        this.font = origin.getFont();
        this.pdfHead = origin.getPdfHeadList();
        this.pdfDescription = origin.getPdfDescriptionList();
        this.pdfPayment = origin.getPdfPaymentList();
        //differ from origin
        this.pathFile = newPath;
    }

    public float getCharacterDimension() {
        return characterDimension;
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

    public String getPdfHead() {
        return listStringToString(pdfHead);
    }

    public String getPdfDescription() {
        return listStringToString(pdfDescription);
    }

    public String getPdfPayment() {
        return listStringToString(pdfPayment);
    }

    public void setPdfHead(String pdfHead) {
        this.pdfHead = stringToListString(pdfHead);
    }

    public void setPdfDescription(String pdfDescription) {
        this.pdfDescription = stringToListString(pdfDescription);
    }

    public void setPdfPayment(String pdfPayment) {
        this.pdfPayment = stringToListString(pdfPayment);
    }

    private List<String> getPdfHeadList() {
        return this.pdfHead;
    }

    private List<String> getPdfDescriptionList() {
        return this.pdfDescription;
    }

    private List<String> getPdfPaymentList() {
        return this.pdfPayment;
    }
    private List<String> stringToListString(String string){
        List<String> stringList = new ArrayList<>();
        if (string.equals("")) return stringList;
        StringBuilder stringBuilder = new StringBuilder();
        for (char c: string.toCharArray()){
            if (c == '\n'){
                stringList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append(c);
            }
        }
        return stringList;
    }

    private String listStringToString(List<String> stringList) {
        if (stringList.size() == 0) return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String string: stringList){
            stringBuilder.append(string);
            stringBuilder.append('\n');
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
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
            float yPayment = addPaymentDescription(contentStream, getSpaceSideLong(), height - yHead - yDescription - yTable - getCharacterDimension() * 3, getLeading(), getCharacterDimension()) + getCharacterDimension() * 3;
                //sign
            float ySign = addSign(contentStream, getSpaceSideLong(), height - yHead - yDescription - yTable - yPayment - getCharacterDimension() * 3, getLeading(), getCharacterDimension(), width);
            //save PDF
            contentStream.close();
            if (yHead + yDescription + yTable + yPayment + ySign <= height) document.addPage(newPage);
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
        /*//sx
        addLine(contentStream, "Studio dentistico associato", x, y, leading, characterDimension, true);
        writtenLines += characterDimension + leading;
        addLine(contentStream, "Dott. Della Bella A. - Dott. Scarfone M.", x, y - writtenLines * 1, leading, characterDimension, true);
        addLine(contentStream, "Via Martiri della Libertà n.21", x, y - writtenLines * 2, leading, characterDimension, true);
        addLine(contentStream, "Novi di Modena tel. 059-677050", x, y - writtenLines * 3, leading, characterDimension, true);
        //dx
        addLine(contentStream, date.toString(), width - getSpaceSideShort(), y, leading, characterDimension, false);
        addLine(contentStream, "Sig. " + person.getFirstName()+ " " + person.getLastName(), width - getSpaceSideShort(), y - writtenLines * 3, leading, characterDimension, false);
        return writtenLines * 3;
        */
        //head description
        for (String string: getPdfHeadList()){
            addLine(contentStream, string, x, y - writtenLines, leading, characterDimension, true);
            writtenLines += characterDimension + leading;
        }
        //date and person
        addLine(contentStream, date.toString(), width - getSpaceSideShort(), y, leading, characterDimension, false);
        addLine(contentStream, "Sig. " + person.getFirstName()+ " " + person.getLastName(), width - getSpaceSideShort(), y - writtenLines, leading, characterDimension, false);
        return writtenLines;
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
            float descriptionHeight = addParagraph(contentStream, description.toString(), x, y - writtenLines, width / 2, leading, characterDimension);
            writtenLines += descriptionHeight;
            if (service.getChosenService().getServicePrice() > 0 && service.getTimeSelected() > 1){
                addLine(contentStream, "(" + service.getTimeSelected() + "x) " + price, (width / 2) + (width / 4) - x, y - writtenLines, leading, characterDimension, false);
            } else {
                addLine(contentStream, Double.toString(price), (width / 2) + (width / 4) - x, y - writtenLines, leading, characterDimension, false);
            }
            if (service.getChosenService().getServicePriceForTooth() != 0){
                price *= service.getChosenTeeth().size();
            } else {
                price *= service.getTimeSelected();
            }
            price = round(price);
            addLine(contentStream, Double.toString(price), width - x, y - writtenLines, leading, characterDimension, false);
            writtenLines += characterDimension + leading;
            total += price;
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
    private float addPaymentDescription(PDPageContentStream contentStream, float x, float y, float leading, float characterDimension) throws IOException {
        float writtenLines = 0;
        String payment = "Pagamento:";
        addLine(contentStream, payment, x, y, leading, characterDimension, true);
        /*addParagraph(contentStream, "50% inizio lavori", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        addParagraph(contentStream, "25% metà lavori", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        addParagraph(contentStream, "25% fine lavori", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;
        addParagraph(contentStream, "Pianificazione pagamenti condivisa con il paziente", x + stringWidth(string + "    ", characterDimension, getFont()), y - writtenLines, width - x, leading, characterDimension);
        writtenLines += characterDimension + leading;*/
        for (String string: getPdfPaymentList()){
            addLine(contentStream, string, x + stringWidth(payment + "    ", characterDimension, getFont()), y - writtenLines, leading, characterDimension, true);
            writtenLines += characterDimension + leading;
        }
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

    /*public static void main(String [] args){
        List<ServiceDetail> serviceDetails = new ArrayList<>();
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service 1"), 0.0, 175.0), List.of(18, 19, 20), 1));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service 2"), 750.0, 0.0), List.of(18, 19, 20), 3));
        serviceDetails.add(new ServiceDetail(new Service(new SimpleStringProperty("Service with a very long name to write down maybe too long to be written in one single line"), 0.0, 175.0), List.of(18, 19, 20), 1));
        Quote quote = new Quote(new Person(new SimpleStringProperty("Francesco"), new SimpleStringProperty("Della Casa")), serviceDetails, new SimpleObjectProperty<>(LocalDate.now()));
        Pdf pdf = new Pdf("C:\\Users\\dcfre\\Desktop\\programma\\temp.pdf", true);
        pdf.createQuote(quote);
    }*/
}
