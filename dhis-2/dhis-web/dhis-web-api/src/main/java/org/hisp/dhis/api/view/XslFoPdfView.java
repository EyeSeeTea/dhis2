package org.hisp.dhis.api.view;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.URIResolver;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class XslFoPdfView extends AbstractUrlBasedView
{
    public static final String DEFAULT_CONTENT_TYPE = "application/pdf";

    private URIResolver uriResolver;

    public XslFoPdfView()
    {
        setContentType( DEFAULT_CONTENT_TYPE );
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
    protected void renderMergedOutputModel( Map<String, Object> model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        response.setContentType( getContentType() );

        PrintWriter pw = response.getWriter();
        pw.write( XslFoPdfView.class.toString() );
        pw.write( "\nURL: " + getUrl() );
    }
}
