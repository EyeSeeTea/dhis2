package org.hisp.dhis.api.view;

import org.amplecode.staxwax.transformer.LoggingErrorListener;
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
import javax.xml.transform.URIResolver;
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

    private URIResolver uriResolver;

    public XsltHtmlView()
    {
        setContentType( DEFAULT_CONTENT_TYPE );

        URIResolver uriResolver = new ClassPathUriResolver( "/templates/xslt/" );
        setUriResolver( uriResolver );
    }

    public URIResolver getUriResolver()
    {
        return uriResolver;
    }

    public void setUriResolver( URIResolver uriResolver )
    {
        this.uriResolver = uriResolver;
    }

    @Override
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {
        response.setContentType( getContentType() );
        model = ViewUtils.filterModel( model );

        Object domainModel = model.get( "model" );

        if ( domainModel == null )
        {
            // TODO throw exception
        }

        JAXBContext context = JAXBContext.newInstance( domainModel.getClass() );
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, false );
        marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

        ClassPathResource classPathResource = new ClassPathResource( getUrl() );

        Source xmlSource = new JAXBSource( context, domainModel );
        Source xsltSource = new StreamSource( classPathResource.getInputStream() );

        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver( uriResolver );
        factory.setErrorListener( new LoggingErrorListener() );

        Transformer transformer = factory.newTransformer( xsltSource );

        OutputStream output = response.getOutputStream();

        // pass on any parameters set in xslt-params
        Map<String, String> params = (Map<String, String>) model.get( "xslt-params" );
        if ( params != null )
        {
            for ( Map.Entry<String, String> entry : params.entrySet() )
            {
                transformer.setParameter( entry.getKey(), entry.getValue() );
            }
        }

        transformer.transform( xmlSource, new StreamResult( output ) );

    }
}
