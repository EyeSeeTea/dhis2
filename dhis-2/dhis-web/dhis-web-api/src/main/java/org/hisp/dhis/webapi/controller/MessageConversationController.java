package org.hisp.dhis.webapi.controller;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.message.Message;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.node.exception.InvalidTypeException;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.schema.descriptors.MessageConversationSchemaDescriptor;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = MessageConversationSchemaDescriptor.API_ENDPOINT )
public class MessageConversationController
    extends AbstractCrudController<MessageConversation>
{
    @Autowired
    private MessageService messageService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private CurrentUserService currentUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void postProcessEntity( MessageConversation entity, WebOptions options, Map<String, String> parameters ) throws Exception
    {
        Boolean markRead = Boolean.parseBoolean( parameters.get( "markRead" ) );

        if ( markRead )
        {
            entity.markRead( currentUserService.getCurrentUser() );
            manager.update( entity );
        }
    }

    @Override
    protected List<MessageConversation> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<MessageConversation> entityList;

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else if ( options.hasPaging() )
        {
            int count = manager.getCount( getEntityClass() );

            Pager pager = new Pager( options.getPage(), count, options.getPageSize() );
            metaData.setPager( pager );

            entityList = new ArrayList<MessageConversation>( messageService.getMessageConversations( pager.getOffset(), pager.getPageSize() ) );
        }
        else
        {
            entityList = new ArrayList<MessageConversation>( manager.getAll( getEntityClass() ) );
        }

        return entityList;
    }

    //--------------------------------------------------------------------------
    // POST for new MessageConversation
    //--------------------------------------------------------------------------

    @Override
    public void postXmlObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Message message = JacksonUtils.fromXml( input, Message.class );
        postObject( response, request, message );
    }

    @Override
    public void postJsonObject( HttpServletResponse response, HttpServletRequest request, InputStream input ) throws Exception
    {
        Message message = JacksonUtils.fromJson( input, Message.class );
        postObject( response, request, message );
    }

    public void postObject( HttpServletResponse response, HttpServletRequest request, Message message )
    {
        List<User> users = new ArrayList<User>( message.getUsers() );
        message.getUsers().clear();

        for ( OrganisationUnit ou : message.getOrganisationUnits() )
        {
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou.getUid() );

            if ( organisationUnit == null )
            {
                ContextUtils.conflictResponse( response, "Organisation Unit does not exist: " + ou.getUid() );
                return;
            }

            message.getUsers().addAll( organisationUnit.getUsers() );
        }

        for ( User u : users )
        {
            User user = userService.getUser( u.getUid() );

            if ( user == null )
            {
                ContextUtils.conflictResponse( response, "User does not exist: " + u.getUid() );
                return;
            }

            message.getUsers().add( user );
        }

        for ( UserGroup ug : message.getUserGroups() )
        {
            UserGroup userGroup = userGroupService.getUserGroup( ug.getUid() );

            if ( userGroup == null )
            {
                ContextUtils.conflictResponse( response, "User Group does not exist: " + ug.getUid() );
                return;
            }

            message.getUsers().addAll( userGroup.getMembers() );
        }

        if ( message.getUsers().isEmpty() )
        {
            ContextUtils.conflictResponse( response, "No recipients selected." );
            return;
        }

        String metaData = MessageService.META_USER_AGENT + request.getHeader( ContextUtils.HEADER_USER_AGENT );

        int id = messageService.sendMessage( message.getSubject(), message.getText(), metaData, message.getUsers() );

        MessageConversation conversation = messageService.getMessageConversation( id );

        ContextUtils.createdResponse( response, "Message conversation created", MessageConversationSchemaDescriptor.API_ENDPOINT + "/" + conversation.getUid() );
    }

    //--------------------------------------------------------------------------
    // POST for reply on existing MessageConversation
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.POST )
    public void postMessageConversationReply( @PathVariable( "uid" ) String uid, @RequestBody String body,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        String metaData = MessageService.META_USER_AGENT + request.getHeader( ContextUtils.HEADER_USER_AGENT );

        MessageConversation conversation = messageService.getMessageConversation( uid );

        if ( conversation == null )
        {
            ContextUtils.conflictResponse( response, "Message conversation does not exist: " + uid );
            return;
        }

        messageService.sendReply( conversation, body, metaData );

        ContextUtils.createdResponse( response, "Message conversation created", MessageConversationSchemaDescriptor.API_ENDPOINT + "/" + conversation.getUid() );
    }

    //--------------------------------------------------------------------------
    // POST for feedback
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/feedback", method = RequestMethod.POST )
    public void postMessageConversationFeedback( @RequestParam( "subject" ) String subject, @RequestBody String body,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        String metaData = MessageService.META_USER_AGENT + request.getHeader( ContextUtils.HEADER_USER_AGENT );

        messageService.sendFeedback( subject, body, metaData );

        ContextUtils.createdResponse( response, "Feedback created", null );
    }

    //--------------------------------------------------------------------------
    // PUT to mark MessageConversations as read for the current user
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/read", method = RequestMethod.PUT, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE } )
    public @ResponseBody RootNode markMessageConversationsRead( @RequestBody String[] uids, HttpServletResponse response )
        throws IOException, InvalidTypeException
    {
        RootNode rootNode = new RootNode( "reply" );

        List<String> uidList = Arrays.asList( uids ); // TODO Use array instead

        if( uidList.isEmpty() )
        {
            rootNode.addChild( new SimpleNode( "response", "error" ) );
            rootNode.addChild( new SimpleNode( "message", "No messages given" ) );

            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );

            return rootNode;

            //result.put( "response", "error" );
            //result.put( "message", "No message conversations given" );
            //ContextUtils.badRequestResponse( response, objectMapper.writeValueAsString( result ) );
            //return;
        }

        Collection<MessageConversation> messageConversations = messageService.getMessageConversations( uidList );

        if( messageConversations.isEmpty() )
        {
            rootNode.addChild( new SimpleNode( "response", "error" ) );
            rootNode.addChild( new SimpleNode( "message", "Invalid UIDs given" ) );

            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );

            return rootNode;

            //result.put( "response", "error" );
            //result.put( "message", "No message conversations found for the given UIDs" );
            //ContextUtils.badRequestResponse( response, objectMapper.writeValueAsString( result ) );
            //return;
        }

        User currentUser = currentUserService.getCurrentUser();

        CollectionNode marked = rootNode.addChild( new CollectionNode( "markedRead" ) );
        marked.setWrapping( false );
        //Set<String> marked = new HashSet<String>();

        for( MessageConversation conversation : messageConversations )
        {
            if( conversation.markRead( currentUser ) )
            {
                //marked.add( conversation.getUid() );
                marked.addChild( new SimpleNode( "uid", conversation.getUid() ) );
                messageService.updateMessageConversation( conversation );
            }
        }

        //result.put("response", "success" );
        //result.put("markedRead", marked );

        //ContextUtils.okResponse( response, objectMapper.writeValueAsString( result ) );

        rootNode.addChild( new SimpleNode( "response", "success" ) );

        response.setStatus( HttpServletResponse.SC_OK );

        return rootNode;
    }

    //--------------------------------------------------------------------------
    // PUT to mark MessageConversations as unread for the current user
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/unread", method = RequestMethod.PUT )
    public void markMessageConversationsUnread( @RequestBody String[] uids, HttpServletResponse response )
    {
        Map<String, Object> result = new HashMap<String, Object>();

        List<String> uidList = Arrays.asList( uids );

        if( uidList.isEmpty() )
        {
            result.put( "response", "error" );
            ContextUtils.badRequestResponse( response, "No message conversations given" );
            return;
        }

        Collection<MessageConversation> messageConversations = messageService.getMessageConversations( uidList );

        if( messageConversations.isEmpty() )
        {
            ContextUtils.conflictResponse( response, "No message conversations found for the given UIDs" );
            return;
        }

        User currentUser = currentUserService.getCurrentUser();

        for( MessageConversation conversation : messageConversations )
        {
            conversation.markUnread( currentUser );
            messageService.updateMessageConversation( conversation );
        }

        ContextUtils.okResponse( response, "" );
    }

    //--------------------------------------------------------------------------
    // DELETE messageConversation for the current user
    //--------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.DELETE, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE } )
    public @ResponseBody RootNode removeMessageConversations(
        @RequestParam("uid") String[] uids, HttpServletResponse response )
    {
        RootNode rootNode = new RootNode( "reply" );

        List<String> uidList = Arrays.asList( uids );

        if( uidList.isEmpty() )
        {
            rootNode.addChild( new SimpleNode( "message", "No messages given" ) );
            response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
            //ContextUtils.badRequestResponse( response, "No message conversations given" );
            return rootNode;
        }

        Collection<MessageConversation> messageConversations = messageService.getMessageConversations( uidList );

        if( messageConversations.isEmpty() )
        {
            rootNode.addChild( new SimpleNode( "message", "No messages found for the given UIDs" ) );
            response.setStatus( HttpServletResponse.SC_CONFLICT );
            //ContextUtils.conflictResponse( response, "No message conversations found for the given UIDs" );
            return rootNode;
        }

        CollectionNode removed = rootNode.addChild( new CollectionNode( "removed" ) );

        User currentUser = currentUserService.getCurrentUser();

        for( MessageConversation conversation : messageConversations )
        {
            if( conversation.remove( currentUser ) )
            {
                removed.addChild( new SimpleNode( "uid", conversation.getUid() ) );
                messageService.updateMessageConversation( conversation );
            }
        }

        //ContextUtils.okResponse( response, uidList.size() > 1 ? "Messages were deleted" : "Message was deleted");

        response.setStatus( HttpServletResponse.SC_OK );

        return rootNode;
    }
}
