package org.hisp.dhis.webapi.controller;

import org.bouncycastle.ocsp.Req;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.dxf2.webmessage.WebMessage;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.keyjsonvalue.KeyJsonValue;
import org.hisp.dhis.keyjsonvalue.KeyJsonValueService;
import org.hisp.dhis.webapi.utils.WebMessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Stian Sandvold on 27.09.2015.
 */
@Controller
@RequestMapping( "/dataStore" )
public class KeyJsonValueController
    extends AbstractCrudController<KeyJsonValue>
{
    @Autowired
    private KeyJsonValueService keyJsonValueService;

    @Autowired
    private RenderService renderService;

    @RequestMapping( value = "/{namespace}/{key}", method = RequestMethod.GET, produces = "application/json" )
    public void getNamespace(
        @PathVariable String namespace,
        @PathVariable String key,
        HttpServletResponse response )
        throws IOException, WebMessageException
    {
        KeyJsonValue keyJsonValue = keyJsonValueService.getKeyJsonValue( namespace, key );

        if ( keyJsonValue == null )
        {
            throw new WebMessageException( WebMessageUtils
                .notFound( "The key '" + key + "' was not found in the namespace '" + namespace + "'." ) );
        }

        renderService.toJson( response.getOutputStream(), keyJsonValue );
    }

    @RequestMapping( value = "/{namespace}", method = RequestMethod.POST, produces = "application/json" )
    public void addKey(
        @PathVariable String namespace,
        @RequestParam String key,
        @RequestParam String value,
        HttpServletResponse response )
        throws IOException, WebMessageException
    {
        // Check uniqueness of the key
        if ( keyJsonValueService.getKeyJsonValue( namespace, key ) != null )
        {
            throw new WebMessageException( WebMessageUtils
                .conflict( "The key '" + key + "' already exists on the namespace '" + namespace + "'." ) );
        }

        // Consider making sure "value" has a valid json format?


        KeyJsonValue keyJsonValue = new KeyJsonValue();

        keyJsonValue.setKey( key );
        keyJsonValue.setNamespace( namespace );
        keyJsonValue.setValue( value );

        keyJsonValueService.addKeyJsonValue( keyJsonValue );

        renderService.toJson( response.getOutputStream(), keyJsonValue );
    }

    @RequestMapping( value = "/{namespace}/{key}", method = RequestMethod.PUT, produces = "application/json")
    public void updateKeyJsonValue(
        @PathVariable String namespace,
        @PathVariable String key,
        @RequestParam String value,
        HttpServletResponse response
    )
        throws WebMessageException, IOException
    {
        KeyJsonValue keyJsonValue = keyJsonValueService.getKeyJsonValue( namespace, key );

        if(keyJsonValue == null)
        {
            throw new WebMessageException( WebMessageUtils
                .notFound( "The key '" + key + "' was not found in the namespace '" + namespace + "'." ) );
        }

        keyJsonValue.setValue( value );

        keyJsonValueService.updateKeyJsonValue( keyJsonValue );

        renderService.toJson( response.getOutputStream(), keyJsonValue );
    }

    @RequestMapping( value = "/{namespace}/{key}", method = RequestMethod.DELETE, produces = "application/json" )
    public void deleteKeyJsonValue(
        @PathVariable String namespace,
        @PathVariable String key,
        HttpServletResponse response
    )
        throws WebMessageException
    {
        KeyJsonValue keyJsonValue = keyJsonValueService.getKeyJsonValue( namespace, key );

        if(keyJsonValue == null)
        {
            throw new WebMessageException( WebMessageUtils
                .notFound( "The key '" + key + "' was not found in the namespace '" + namespace + "'." ) );
        }

        keyJsonValueService.deleteKeyJsonValue( keyJsonValue );

        throw new WebMessageException( WebMessageUtils.ok( "Key '"+key+"' deleted from namespace '"+namespace+"'." ) );
    }
}
