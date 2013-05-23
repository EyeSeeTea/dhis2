package org.hisp.dhis.dxf2.pdfform;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

public interface PdfDataEntryFormService
{

    void generatePDFDataEntryForm( Document document, PdfWriter writer, String inputUid, int typeId,
        Rectangle pageSize, PdfFormFontSettings pdfFormFontSettings );

}
