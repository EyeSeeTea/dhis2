package org.hisp.dhis.api.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.PdfDataEntryFormImportUtil;
import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormService;
import org.hisp.dhis.dxf2.pdfform.PdfDataEntryFormUtil;
import org.hisp.dhis.dxf2.pdfform.PdfFormFontSettings;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author James Chang <jamesbchang@gmail.com>
 */

@Controller
@RequestMapping( value = "/pdfForm" )
public class PDFFormController
{
    private static final String MEDIA_TYPE_PDF = "application/pdf";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private Notifier notifier;

    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private PdfDataEntryFormService pdfDataEntryFormService;
   
    @Autowired
    private DataSetService dataSetService;

    // -------------------------------------------------------------------------
    // GET / POST
    // -------------------------------------------------------------------------

    // --------------------- Data Set Related ---------------------

    @RequestMapping( value = "/DataSet/{dataSetUid}", method = RequestMethod.GET )
    public void getFormPDF_DataSet( HttpServletRequest request, HttpServletResponse response, @PathVariable
    String dataSetUid )
        throws IOException, DocumentException, ParseException
    {

        // STEP 1. - Create Document and PdfWriter - with OutputStream and
        // document tie.
        Document document = new Document(); // TODO: can specify the size of
                                            // document - like letter or A4
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance( document, baos );

        // STEP 2. Generate PDF Document Contents
        PdfFormFontSettings pdfFormFontSettings = new PdfFormFontSettings ();
        
        PdfDataEntryFormUtil.setDefaultFooterOnDocument( document, request.getServerName(), pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_FOOTER ) );
        
        pdfDataEntryFormService.generatePDFDataEntryForm( document, writer
            , dataSetUid, PdfDataEntryFormUtil.DATATYPE_DATASET
            , PdfDataEntryFormUtil.getDefaultPageSize( PdfDataEntryFormUtil.DATATYPE_DATASET )
            , new PdfFormFontSettings ()
            );

        // STEP 3. - Response Header/Content Type Set
        setResponse( response, baos.size() );

        // STEP 4. - Output the data into Stream and close the stream.
        // write ByteArrayOutputStream to the ServletOutputStream
        writeToOutputStream( baos, response );

    }

    @RequestMapping( value = "/DataSet", method = RequestMethod.POST )
    // , consumes = MEDIA_TYPE_PDF)
    public void sendFormPDF_DataSet( HttpServletRequest request, HttpServletResponse response )
        throws IOException, Exception
    {

        // Step 1. Set up Import Option
        ImportStrategy strategy = ImportStrategy.NEW_AND_UPDATES;
        IdentifiableProperty dataElementIdScheme = IdentifiableProperty.UID;
        IdentifiableProperty orgUnitIdScheme = IdentifiableProperty.UID;
        boolean dryRun = false;
        boolean skipExistingCheck = false; // THIS IS THE DEFAULT CHOICE. DO WE
                                           // USE THIS?

        ImportOptions options = new ImportOptions( dataElementIdScheme, orgUnitIdScheme, dryRun, strategy,
            skipExistingCheck );

        // Step 2. Generate Task ID
        TaskId taskId = new TaskId( TaskCategory.DATAVALUE_IMPORT, currentUserService.getCurrentUser() );

        notifier.clear( taskId );

        // Step 3. Input Stream Check
        InputStream in = request.getInputStream();

        in = StreamUtils.wrapAndCheckCompressionFormat( in );

        // No logging?
        // log.info( options );

        // Step 4. Save (Import) the data values.
        dataValueSetService.saveDataValueSetPdf( in, options, taskId );

        // Step 5. Set the response - just simple OK response.
        response.setContentType( MediaType.TEXT_PLAIN_VALUE );
        response.setStatus( HttpServletResponse.SC_OK );
        // response.getWriter().println("Success");

    }

    // --------------------- Program Stage Related ---------------------

    @RequestMapping( value = "/ProgramStage/{programStageUid}", method = RequestMethod.GET )
    public void getFormPDF_ProgramStage( HttpServletRequest request, HttpServletResponse response, @PathVariable
    String programStageUid )
        throws IOException, DocumentException, ParseException
    {

        // STEP 1. - Create Document and PdfWriter - with OutputStream and
        // document tie.
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance( document, baos );

        // STEP 2. Generate PDF Document Contents
        PdfFormFontSettings pdfFormFontSettings = new PdfFormFontSettings ();
        
        PdfDataEntryFormUtil.setDefaultFooterOnDocument( document, request.getServerName(), pdfFormFontSettings.getFont( PdfFormFontSettings.FONTTYPE_FOOTER ) );
        
        pdfDataEntryFormService.generatePDFDataEntryForm( document, writer
            , programStageUid, PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE
            , new Rectangle( PageSize.A4.getLeft(),
                PageSize.A4.getBottom(), PageSize.A4.getTop(), PageSize.A4.getRight() )          
//            PdfDataEntryFormUtil.getDefaultPageSize( PdfDataEntryFormUtil.DATATYPE_PROGRAMSTAGE )
            , new PdfFormFontSettings ()
            );

        // STEP 3. - Response Header/Content Type Set
        setResponse( response, baos.size() );

        // STEP 4. - write ByteArrayOutputStream to the ServletOutputStream
        writeToOutputStream( baos, response );
    }

    @RequestMapping( value = "/ProgramStage", method = RequestMethod.POST )
    // , consumes = MEDIA_TYPE_PDF)
    public void sendFormPDF_ProgramStage( HttpServletRequest request, HttpServletResponse response )
        throws IOException, Exception
    {

        InputStream in = request.getInputStream();

        // Temporarily using Util class from same project.
        PdfDataEntryFormImportUtil pdfDataEntryFormImportUtil = new PdfDataEntryFormImportUtil();

        pdfDataEntryFormImportUtil.ImportProgramStage( in, i18nManager.getI18nFormat() );

        response.setContentType( MediaType.TEXT_PLAIN_VALUE );
        response.setStatus( HttpServletResponse.SC_OK );
        // response.getWriter().println(strInfo);

    }

    // -----------------------------------------------------------------------------
    // ---------------------- Helper Class Related [START]
    // ----------------------

    private void setResponse( HttpServletResponse response, int contentLength )
    {
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate, post-check=0, pre-check=0" );
        response.setHeader( "Pragma", "public" );

        // setting the content type
        response.setContentType( MEDIA_TYPE_PDF );

        // the contentlength
        response.setContentLength( contentLength );
    }

    private void writeToOutputStream( ByteArrayOutputStream baos, HttpServletResponse response )
        throws IOException
    {
        OutputStream os = null;

        try
        {
            os = response.getOutputStream();
            baos.writeTo( os );
        }
        finally
        {
            os.flush();
            os.close();
        }

    }

    // ---------------------- Helper Class Related [END] ----------------------
    // -----------------------------------------------------------------------------

}