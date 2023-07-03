package com.preventivoapp.appproject_preventivo.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class PdfDeserializer extends StdDeserializer<Pdf> {
    public PdfDeserializer() {
        this(null);
    }

    public PdfDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Pdf deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        //get the pdf detail from the file
        double characterDimensionDouble = (Double) node.get("characterDimension").numberValue();
        double spaceSideShortDouble = (Double) node.get("spaceSideShort").numberValue();
        double spaceSideLongDouble = (Double) node.get("spaceSideLong").numberValue();
        double spaceTopDouble = (Double) node.get("spaceTop").numberValue();
        double leadingDouble = (Double) node.get("leading").numberValue();
        String font = node.get("font").asText();
        String pathFile = node.get("pathFile").asText();
        String language = node.get("language").asText();
        String pdfHead = node.get("pdfHead").asText();
        String pdfDescription = node.get("pdfDescription").asText();
        String pdfPayment = node.get("pdfPayment").asText();
        //convert if necessary into the correct type
        float characterDimension = Float.parseFloat(String.valueOf(characterDimensionDouble));
        float spaceSideShort = Float.parseFloat(String.valueOf(spaceSideShortDouble));
        float spaceSideLong = Float.parseFloat(String.valueOf(spaceSideLongDouble));
        float spaceTop = Float.parseFloat(String.valueOf(spaceTopDouble));
        float leading = Float.parseFloat(String.valueOf(leadingDouble));
        PDFont toFont = switch (font.toUpperCase()) {
            case "COURIER" -> PDType1Font.COURIER;
            case "TIMES_ROMAN" -> PDType1Font.TIMES_ROMAN;
            default -> PDType1Font.HELVETICA;
        };
        //create the pdf setting
        Pdf pdf = new Pdf(characterDimension, leading, spaceTop, spaceSideLong, spaceSideShort, toFont, pathFile, language);
        pdf.setPdfHead(pdfHead);
        pdf.setPdfDescription(pdfDescription);
        pdf.setPdfPayment(pdfPayment);
        return pdf;
    }
}
