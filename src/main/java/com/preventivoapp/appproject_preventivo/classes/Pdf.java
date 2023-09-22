package com.preventivoapp.appproject_preventivo.classes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

@JsonSerialize(using = PdfSerializer.class)
@JsonDeserialize(using = PdfDeserializer.class)
public class Pdf {
    private final float characterDimension;
    private final float spaceSideShort;
    private final float spaceSideLong;
    private final float spaceTop;
    private float leading;
    private final String font;
    private final String pathFile;
    private final String language;
    private final List<String> languageEnglish = new ArrayList<>(List.of("Mx. ", "Description", "Price", "Amount", "Total:", "Payment:", "Signature for acceptance"));
    private final List<String> languageItalian = new ArrayList<>(List.of("Sig. ", "Descrizione", "Prezzo", "Importo", "Totale", "Pagamento:", "Firma per accettazione"));
    private List<String> pdfHead;
    private List<String> pdfDescription;
    private List<String> pdfPayment;

    public Pdf(float characterDimension, float leading, float spaceTop, float spaceSideLong, float spaceSideShort, PDFont font, String pathFile, String language) {
        if (characterDimension == MIN_NORMAL)
            this.characterDimension = 11;
        else
            this.characterDimension = characterDimension;
        if (leading == MIN_NORMAL)
            this.leading = 1;
        else
            this.leading = leading;
        if (spaceTop == MIN_NORMAL)
            this.spaceTop = 42;
        else
            this.spaceTop = spaceTop;
        if (spaceSideLong == MIN_NORMAL)
            this.spaceSideLong = 40;
        else
            this.spaceSideLong = spaceSideLong;
        if (spaceSideShort == MIN_NORMAL)
            this.spaceSideShort = 25;
        else
            this.spaceSideShort = spaceSideShort;
        if (font == null)
            this.font = "HELVETICA";
        else
            this.font = setFont(font);
        this.pathFile = pathFile;
        if (language == null)
            this.language = "English";
        else
            this.language = language;
    }

    public Pdf(Pdf origin, String newPath) {
        //copy from the origin
        this.characterDimension = origin.getCharacterDimension();
        this.spaceTop = origin.getSpaceTop();
        this.spaceSideLong = origin.getSpaceSideLong();
        this.spaceSideShort = origin.getSpaceSideShort();
        this.font = origin.getFontString();
        this.pdfHead = origin.getPdfHeadList();
        this.pdfDescription = origin.getPdfDescriptionList();
        this.pdfPayment = origin.getPdfPaymentList();
        this.language = origin.getLanguage();
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
        if (font.equalsIgnoreCase("COURIER"))
            return PDType1Font.COURIER;
        if (font.equalsIgnoreCase("TIMES ROMAN"))
            return PDType1Font.TIMES_ROMAN;
        return PDType1Font.HELVETICA;
    }

    public String getFontString() {
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

    public String getLanguage() {
        return language;
    }

    public String setFont(PDFont font) {
        if (font.toString().toUpperCase().contains("TIMES-ROMAN")) {
            return "TIMES ROMAN";
        }
        if (font.toString().toUpperCase().contains("COURIER")) {
            return "COURIER";
        }
        return "HELVETICA";
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

    public List<String> getPdfHeadList() {
        return this.pdfHead;
    }

    public List<String> getPdfDescriptionList() {
        return this.pdfDescription;
    }

    public List<String> getPdfPaymentList() {
        return this.pdfPayment;
    }

    private List<String> stringToListString(String string) {
        List<String> stringList = new ArrayList<>();
        if (string.equals(""))
            return stringList;
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (c == '\n') {
                stringList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            } else {
                stringBuilder.append(c);
            }
        }
        if (string.charAt(string.length() - 1) != '\n')
            stringList.add(stringBuilder.toString());
        return stringList;
    }

    private String listStringToString(List<String> stringList) {
        if (stringList == null || stringList.size() == 0)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringList) {
            stringBuilder.append(string);
            stringBuilder.append('\n');
        }
        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
    }

    public void createQuote(Quote quote) {
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
            if (getPdfDescription() == null || getPdfDescription().equals(""))
                setPdfDescription("(Missing pdf description)");
            float yDescription = addParagraph(contentStream, getPdfDescription(), getSpaceSideLong(), height - yHead - getCharacterDimension() * 3, width - getSpaceSideLong(), 1, getCharacterDimension()) + getCharacterDimension() * 3;
            //table
            float yTable = addTable(quote, contentStream, getSpaceSideShort(), height - yHead - yDescription - getCharacterDimension() * 2, getLeading(), getCharacterDimension(), width) + getCharacterDimension() * 2;
            //bottom page
            //payment
            float yPayment = addPaymentDescription(contentStream, getSpaceSideLong(), height - yHead - yDescription - yTable - getCharacterDimension() * 3, getLeading(), getCharacterDimension()) + getCharacterDimension() * 3;
            //sign
            float ySign = addSign(contentStream, getSpaceSideLong(), height - yHead - yDescription - yTable - yPayment - getCharacterDimension() * 3, getLeading(), getCharacterDimension(), width);
            //save PDF
            contentStream.close();
            if (yHead + yDescription + yTable + yPayment + ySign <= height)
                document.addPage(newPage);
            document.save(getPathFile());
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float stringWidth(String string, float characterDimension, PDFont font) throws IOException {
        if (string.length() == 0)
            return 0;
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

    private float addParagraph(PDPageContentStream contentStream, String string, float x, float y, float xMax, float leading, float fontDimension) throws IOException {
        //create the array list of sentences
        float paragraph = 0;
        List<String> lines = new ArrayList<>();
        if (string.indexOf('\n') != -1)
            lines = stringToListString(string);
        else
            lines.add(string);
        for (String line : lines) {
            float writtenLines = 1;
            //create the array list of words from the string
            ArrayList<String> words = new ArrayList<>();
            while (line.length() > 0) {
                if (!line.contains(" ")) {
                    words.add(line);
                    break;
                } else {
                    String word = line.substring(0, line.indexOf(" "));
                    words.add(word);
                    line = line.substring(line.indexOf(" ") + 1);
                }
            }
            //write
            contentStream.beginText();
            contentStream.setFont(getFont(), fontDimension);
            contentStream.setLeading(fontDimension + leading);
            contentStream.newLineAtOffset(x, y - paragraph);
            float lineDimension = x;
            for (String word : words) {
                //if (word.equals("")) continue;
                if (lineDimension + stringWidth(word + " ", fontDimension, getFont()) < xMax) {
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
            paragraph += writtenLines * (fontDimension + leading);
        }
        return paragraph;
    }

    private float addHead(PDPageContentStream contentStream, Person person, LocalDate date, float x, float y, float leading, float characterDimension, float width) throws IOException {
        float writtenLines = 0;
        //head description
        if (getPdfHeadList() == null || getPdfHead().equals(""))
            setPdfHead("(Missing head pdf)\n ");
        for (String string : getPdfHeadList()) {
            addLine(contentStream, string, x, y - writtenLines, leading, characterDimension, true);
            writtenLines += characterDimension + leading;
        }
        //date and person
        addLine(contentStream, date.toString(), width - getSpaceSideShort(), y, leading, characterDimension, false);
        String client = "Mx. ";
        int index = languageEnglish.indexOf(client);
        if (getLanguage().equalsIgnoreCase("Italian"))
            client = languageItalian.get(index);
        if (writtenLines == characterDimension + leading) {
            writtenLines += characterDimension + leading;
            addLine(contentStream, client + person.getFirstName() + " " + person.getLastName(), width - getSpaceSideShort(), y - writtenLines + characterDimension + leading, leading, characterDimension, false);
        } else {
            addLine(contentStream, client + person.getFirstName() + " " + person.getLastName(), width - getSpaceSideShort(), y - writtenLines + characterDimension + leading, leading, characterDimension, false);
        }
        return writtenLines;
    }

    private float addTable(Quote quote, PDPageContentStream contentStream, float x, float y, float leading, float characterDimension, float width) throws IOException {
        float writtenLines = 0;
        //column name
        if (getLanguage().equalsIgnoreCase("Italian")) {
            addLine(contentStream, languageItalian.get(languageEnglish.indexOf("Description")), x, y, leading, characterDimension, true);
            addLine(contentStream, languageItalian.get(languageEnglish.indexOf("Price")), width / 2, y, leading, characterDimension, true);
            addLine(contentStream, languageItalian.get(languageEnglish.indexOf("Amount")), (width / 2) + (width / 4), y, leading, characterDimension, true);
        } else {
            addLine(contentStream, "Description", x, y, leading, characterDimension, true);
            addLine(contentStream, "Price", width / 2, y, leading, characterDimension, true);
            addLine(contentStream, "Amount", (width / 2) + (width / 4), y, leading, characterDimension, true);
        }
        writtenLines += characterDimension + leading + characterDimension / 2;
        //fill columns
        double total = 0;
        for (ServiceDetail service : quote.getServicesChosen()) {
            StringBuilder description = new StringBuilder();
            double price = service.getChosenService().getServicePrice();
            description.append(service.getChosenService().getServiceName());
            if (service.getChosenService().getServicePriceForTooth() != 0) {
                description.append(" (").append(service.showTeeth()).append(")");
                price = service.getChosenService().getServicePriceForTooth();
            }
            float descriptionHeight = addParagraph(contentStream, description.toString(), x, y - writtenLines, width / 2, leading, characterDimension);
            writtenLines += descriptionHeight;
            if (service.getChosenService().getServicePrice() > 0 && service.getTimeSelected() > 1) {
                addLine(contentStream, "(" + service.getTimeSelected() + "x) " + price, (width / 2) + (width / 4) - x, y - writtenLines, leading, characterDimension, false);
            } else {
                addLine(contentStream, Double.toString(price), (width / 2) + (width / 4) - x, y - writtenLines, leading, characterDimension, false);
            }
            if (service.getChosenService().getServicePriceForTooth() != 0) {
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
        String totalString = "Total:";
        int index = languageEnglish.indexOf(totalString);
        if (getLanguage().equalsIgnoreCase("Italian"))
            totalString = languageItalian.get(index);
        addLine(contentStream, totalString, (width / 2) + (width / 4), y - writtenLines, leading, characterDimension, true);
        addLine(contentStream, Double.toString(total), width - x, y - writtenLines, leading, characterDimension, false);
        return writtenLines;
    }

    private double round(double price) {
        price *= 100;
        price = Math.round(price);
        return price / 100;
    }

    private float addPaymentDescription(PDPageContentStream contentStream, float x, float y, float leading, float characterDimension) throws IOException {
        float writtenLines = 0;
        String payment = "Payment:";
        int index = languageEnglish.indexOf(payment);
        if (getLanguage().equalsIgnoreCase("Italian"))
            payment = languageItalian.get(index);
        addLine(contentStream, payment, x, y, leading, characterDimension, true);
        if (getPdfPaymentList() == null || getPdfPayment().equals(""))
            setPdfPayment("(Missing payment description)");
        for (String string : getPdfPaymentList()) {
            addLine(contentStream, string, x + stringWidth(payment + "    ", characterDimension, getFont()), y - writtenLines, leading, characterDimension, true);
            writtenLines += characterDimension + leading;
        }
        return writtenLines;
    }

    private float addSign(PDPageContentStream contentStream, float x, float y, float leading, float characterDimension, float width) throws IOException {
        float writtenLines = 0;
        int index = languageEnglish.indexOf("Signature for acceptance");
        if (getLanguage().equalsIgnoreCase("Italian")) {
            addLine(contentStream, languageItalian.get(index), x, y, leading, characterDimension, true);
        } else {
            addLine(contentStream, languageEnglish.get(index), x, y, leading, characterDimension, true);
        }
        writtenLines += characterDimension + leading;
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.addRect(getSpaceSideLong(), y - writtenLines - characterDimension * 2, width / 2 - x, 1f);
        contentStream.fill();
        writtenLines += 1 + characterDimension * 2;
        return writtenLines;
    }
}
