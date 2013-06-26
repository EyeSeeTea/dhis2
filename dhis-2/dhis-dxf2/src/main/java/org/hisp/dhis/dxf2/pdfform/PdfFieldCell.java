package org.hisp.dhis.dxf2.pdfform;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PushbuttonField;
import com.lowagie.text.pdf.RadioCheckField;

/**
 * @author James Chang
 */

public class PdfFieldCell
    implements PdfPCellEvent
{
    public final static int TYPE_DEFAULT = 0;

    public final static int TYPE_BUTTON = 1;

    public final static int TYPE_TEXT_ORGUNIT = 2;

    public final static int TYPE_TEXT_NUMBER = 3;

    public final static int TYPE_CHECKBOX = 4;

    public final static int TYPE_RADIOBUTTON = 5;

    public final static int TPYE_LABEL = 6;

    private final static float RADIOBUTTON_WIDTH = 10.0f;

    private final static float RADIOBUTTON_TEXTOFFSET = 3.0f;

    private PdfFormField parent;

    private PdfFormField formField;

    private PdfWriter writer;

    private float width;

    private int type;

    private String jsAction;

    private String[] values;

    private String[] texts;

    private String checkValue;

    private String text;

    private String name;

    public PdfFieldCell( PdfFormField formField, int width, PdfWriter writer )
    {
        this.formField = formField;
        this.width = width;
        this.writer = writer;
        this.type = TYPE_DEFAULT;
    }

    public PdfFieldCell( PdfFormField formField, int width, int type, PdfWriter writer )
    {
        this.formField = formField;
        this.width = width;
        this.writer = writer;
        this.type = type;
    }

    public PdfFieldCell( PdfFormField formField, String jsAction, String name, String text, int type, PdfWriter writer )
    {
        this.formField = formField;
        this.writer = writer;
        this.type = type;
        this.name = name;
        this.text = text;
        this.jsAction = jsAction;
    }

    public PdfFieldCell( PdfFormField parent, String[] texts, String[] values, String checkValue, float width,
        int type, PdfWriter writer )
    {
        this.writer = writer;
        this.type = type;
        this.parent = parent;
        this.texts = texts;
        this.values = values;
        this.checkValue = checkValue;
        this.width = width;
    }

    public void cellLayout( PdfPCell cell, Rectangle rect, PdfContentByte[] canvases )
    {
        try
        {

            PdfContentByte canvasText = canvases[PdfPTable.TEXTCANVAS];
            
            // PENDING LOGIC
            // PdfContentByte canvasLine = canvases[PdfPTable.LINECANVAS];
            //
            // float margin = 2;
            //
            // float x1 = rect.getLeft() + margin;
            // float x2 = rect.getRight() - margin;
            // float y1 = rect.getTop() - margin;
            // float y2 = rect.getBottom() + margin;
            //
            // canvasLine.rectangle( x1, y1, x2 - x1, y2 - y1 );

            if ( type == TYPE_RADIOBUTTON )
            {
                if ( parent != null )
                {
                    float leftLoc = rect.getLeft();
                    float rightLoc = rect.getLeft() + RADIOBUTTON_WIDTH;

                    try
                    {
                        String text;
                        String value;

                        for ( int i = 0; i < texts.length; i++ )
                        {

                            text = texts[i];
                            value = values[i];

                            Rectangle radioRec = new Rectangle( leftLoc, rect.getBottom(), rightLoc, rect.getTop() );

                            RadioCheckField rf = new RadioCheckField( writer, radioRec, "RDBtn_" + text, value );

                            if ( value == checkValue )
                                rf.setChecked( true );

                            rf.setBorderColor( GrayColor.GRAYBLACK );
                            rf.setBackgroundColor( GrayColor.GRAYWHITE );
                            rf.setCheckType( RadioCheckField.TYPE_CIRCLE );

                            parent.addKid( rf.getRadioField() );

                            leftLoc = rightLoc;
                            rightLoc += width;

                            ColumnText.showTextAligned( canvasText, Element.ALIGN_LEFT, new Phrase( text ), leftLoc
                                + RADIOBUTTON_TEXTOFFSET, (radioRec.getBottom() + radioRec.getTop()) / 2, 0 );

                            leftLoc = rightLoc;
                            rightLoc += RADIOBUTTON_WIDTH;
                        }
                    }
                    catch ( Exception ex )
                    {
                        throw new RuntimeException( ex.getMessage() );
                    }

                    writer.addAnnotation( parent );
                }
            }
            else if ( type == TYPE_BUTTON )
            {
                // Add the push button
                PushbuttonField button = new PushbuttonField( writer, rect, name );
                button.setBackgroundColor( new GrayColor( 0.75f ) );
                button.setBorderColor( GrayColor.GRAYBLACK );
                button.setBorderWidth( 1 );
                button.setBorderStyle( PdfBorderDictionary.STYLE_BEVELED );
                button.setTextColor( GrayColor.GRAYBLACK );
                button.setFontSize( PdfDataEntryFormUtil.UNITSIZE_DEFAULT );
                button.setText( text );
                button.setLayout( PushbuttonField.LAYOUT_ICON_LEFT_LABEL_RIGHT );
                button.setScaleIcon( PushbuttonField.SCALE_ICON_ALWAYS );
                button.setProportionalIcon( true );
                button.setIconHorizontalAdjustment( 0 );

                formField = button.getField();
                formField.setAction( PdfAction.javaScript( jsAction, writer ) );
            }
            else if ( type == TYPE_CHECKBOX )
            {
                // Start from the middle of the cell width.
                float startingPoint = rect.getLeft() + ((rect.getWidth() + width) / 2.0f);

                formField.setWidget(
                    new Rectangle( startingPoint, rect.getBottom(), startingPoint + width, rect.getTop() ),
                    PdfAnnotation.HIGHLIGHT_NONE );
            }
            else
            {

                if ( type == TYPE_TEXT_ORGUNIT )
                {
                    formField.setAdditionalActions( PdfName.BL, PdfAction.javaScript(
                        "if(event.value == '') app.alert('Warning! Please Enter The Org ID.');", writer ) );
                }

                // TYPE_TEXT_NUMBER case included as well here.

                // PENDING LOGIC
                // Add -1, +1 to create cellpadding effect - spacing between
                // rows/cells
                // formField.setWidget(
                // new Rectangle( rect.getLeft() + 1, rect.getBottom() + 1,
                // rect.getLeft() + width - 1, rect.getTop() - 1 ),
                // PdfAnnotation.HIGHLIGHT_NONE );

                formField.setWidget(
                    new Rectangle( rect.getLeft(), rect.getBottom(), rect.getLeft() + width, rect.getTop() ),
                    PdfAnnotation.HIGHLIGHT_NONE );

            }

            writer.addAnnotation( formField );

        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex.getMessage() );
        }
    }
}