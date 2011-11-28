package org.hisp.dhis.api.view;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class XsltHtmlView extends AbstractUrlBasedView
{
    public static final String DEFAULT_CONTENT_TYPE = "text/html";

    public XsltHtmlView()
    {
        setContentType( DEFAULT_CONTENT_TYPE );
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        response.setContentType( getContentType() );
        model = ViewUtils.filterModel( model );
        Object domainModel = model.get( "model" );

        if ( domainModel == null )
        {
            // throw exception
        }

        JAXBContext context = JAXBContext.newInstance( domainModel.getClass() );
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

        ClassPathResource classPathResource = new ClassPathResource(getUrl());

        if(classPathResource.exists()) {
            System.out.println("Hooray");
        } else {
            System.err.println("Path " + classPathResource.getPath() + " does not exist");
        }

        Source xmlSource = new JAXBSource( context, domainModel );
        Source xsltSource = new StreamSource( XsltHtmlView.class.getResourceAsStream( getUrl() ) );

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer( xsltSource );

        OutputStream output = response.getOutputStream();
        transformer.transform( xmlSource, new StreamResult( output ) );
    }
}
