package com.preventivoapp.appproject_preventivo.classes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PdfSerializer extends StdSerializer<Pdf> {
    public PdfSerializer() {
        this(null);
    }

    public PdfSerializer(Class<Pdf> t) {
        super(t);
    }

    @Override
    public void serialize(Pdf value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        //write on the file
        gen.writeNumberField("characterDimension", value.getCharacterDimension());
        gen.writeNumberField("spaceSideShort", value.getSpaceSideShort());
        gen.writeNumberField("spaceSideLong", value.getSpaceSideLong());
        gen.writeNumberField("spaceTop", value.getSpaceTop());
        gen.writeNumberField("leading", value.getLeading());
        gen.writeStringField("font", value.getFontString());
        gen.writeStringField("pathFile", value.getPathFile());
        gen.writeStringField("language", value.getLanguage());
        gen.writeStringField("pdfHead", value.getPdfHead());
        gen.writeStringField("pdfDescription", value.getPdfDescription());
        gen.writeStringField("pdfPayment", value.getPdfPayment());
        gen.writeEndObject();
    }
}
