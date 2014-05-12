package org.hisp.dhis.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_JSON;

/**
 * Created by kprakash on 05/05/14.
 */

@Controller
@RequestMapping( value = TranslationController.RESOURCE_PATH )
public class TranslationController
{
    public static final String RESOURCE_PATH = "/translations";

    @Autowired
    private ContextUtils contextUtils;


    @Autowired
    private TranslationService translationService;


    @RequestMapping( produces = CONTENT_TYPE_JSON, method = RequestMethod.GET )
    public void exportJson( HttpServletResponse response ) throws IOException
    {
        Collection<Translation> allTranslations = translationService.getAllTranslations();
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, ContextUtils.CacheStrategy.NO_CACHE, "translations.json", false );
        JacksonUtils.toJson( response.getOutputStream(), allTranslations );
    }

    @RequestMapping( method = RequestMethod.POST, consumes = CONTENT_TYPE_JSON )
    public void importJson( HttpServletRequest request ) throws IOException
    {
        TypeReference<Collection<Translation>> typeReference = new TypeReference<Collection<Translation>>(){};
        Collection<Translation> translations = JacksonUtils.fromJson( request.getInputStream(), typeReference );
        translationService.createOrUpdate( translations );
    }

}