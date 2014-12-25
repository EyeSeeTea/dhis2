package org.hisp.dhis.user;

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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Chau Thu Tran
 */
public interface UserService
{
    String ID = UserService.class.getName();

    // -------------------------------------------------------------------------
    // User
    // -------------------------------------------------------------------------

    /**
     * Adds a User.
     *
     * @param user the User to add.
     * @return the generated identifier.
     */
    int addUser( User user );

    /**
     * Updates a User.
     *
     * @param user the User to update.
     */
    void updateUser( User user );

    /**
     * Retrieves the User with the given identifier.
     *
     * @param id the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser( int id );

    /**
     * Retrieves the User with the given unique identifier.
     *
     * @param uid the identifier of the User to retrieve.
     * @return the User.
     */
    User getUser( String uid );

    /**
     * Returns a Collection of all Users.
     *
     * @return a Collection of Users.
     */
    Collection<User> getAllUsers();

    List<User> getAllUsersBetween( int first, int max );

    List<User> getAllUsersBetweenByName( String name, int first, int max );

    Collection<User> getUsersByLastUpdated( Date lastUpdated );

    /**
     * Returns a Collection of the Users which are not associated with any
     * OrganisationUnits.
     *
     * @return a Collection of Users.
     */
    Collection<User> getUsersWithoutOrganisationUnit();

    /**
     * Returns a Collection of Users which are having given Phone number.
     *
     * @param phoneNumber
     * @return a Collection of Users.
     */
    Collection<User> getUsersByPhoneNumber( String phoneNumber );

    /**
     * Deletes a User.
     *
     * @param user the User to delete.
     */
    void deleteUser( User user );

    int getUserCount();

    int getUserCountByName( String name );

    int getUsersWithoutOrganisationUnitCount();

    int getUsersWithoutOrganisationUnitCountByName( String name );

    int getUsersByOrganisationUnitCount( OrganisationUnit orgUnit );

    int getUsersByOrganisationUnitCountByName( OrganisationUnit orgUnit, String name );

    List<User> getUsersByUid( List<String> uids );

    User searchForUser( String query );

    List<User> queryForUsers( String query );

    /**
     * Returns a set of CategoryOptionGroups that may be seen by the current
     * user, if the current user has any CategoryOptionGroupSet constraint(s).
     *
     * @param userCredentials User credentials to check restrictions for.
     * @return Set of CategoryOptionGroups if constrained, else null.
     */
    public Set<CategoryOptionGroup> getCogDimensionConstraints( UserCredentials userCredentials );

    /**
     * Returns a set of CategoryOptions that may be seen by the current
     * user, if the current user has any Category constraint(s).
     *
     * @param userCredentials User credentials to check restrictions for.
     * @return Set of CategoryOptions if constrained, else null.
     */
    public Set<DataElementCategoryOption> getCoDimensionConstraints( UserCredentials userCredentials );

    boolean isSuperUser( UserCredentials userCredentials );

    boolean isLastSuperUser( UserCredentials userCredentials );

    boolean isSuperRole( UserAuthorityGroup userAuthorityGroup );

    boolean isLastSuperRole( UserAuthorityGroup userAuthorityGroup );

    Collection<User> getUsersByName( String name );

    Collection<String> getUsernames( String query, Integer max );

    int countDataSetUserAuthorityGroups( DataSet dataSet );

    /**
     * Tests whether the current user is allowed to create a user associated
     * with the given user group identifiers. Returns true if current user has 
     * the F_USER_ADD authority. Returns true if the current user has the 
     * F_USER_ADD_WITHIN_MANAGED_GROUP authority and can manage any of the given
     * user groups. Returns false otherwise.
     * 
     * @param userGroups the user group identifiers.
     * @return true if the current user can create user, false if not.
     */
    boolean canAddOrUpdateUser( Collection<String> userGroups );
    
    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    /**
     * Adds a UserCredentials.
     *
     * @param userCredentials the UserCredentials to add.
     * @return the User which the UserCredentials is associated with.
     */
    int addUserCredentials( UserCredentials userCredentials );

    /**
     * Updates a UserCredentials.
     *
     * @param userCredentials the UserCredentials to update.
     */
    void updateUserCredentials( UserCredentials userCredentials );

    /**
     * Retrieves the UserCredentials of the given User.
     *
     * @param user the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentials( User user );

    /**
     * Retrieves the UserCredentials associated with the User with the given
     * name.
     *
     * @param username the name of the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentialsByUsername( String username );

    /**
     * Retrieves the UserCredentials associated with the User with the given
     * OpenID.
     *
     * @param openId the openId of the User.
     * @return the UserCredentials.
     */
    UserCredentials getUserCredentialsByOpenID( String openId );

    /**
     * Retrieves all UserCredentials.
     *
     * @return a Collection of UserCredentials.
     */
    Collection<UserCredentials> getAllUserCredentials();

    /**
     * Encodes and sets the password of the User.
     * Due to business logic required on password updates the password for a user
     * should only be changed using this method or {@link #encodeAndSetPassword(UserCredentials, String) encodeAndSetPassword}
     * and not directly on the User or UserCredentials object.
     *
     * Note that the changes made to the User object are not persisted.
     *
     * @param user the User.
     * @param rawPassword the raw password.
     */
    void encodeAndSetPassword( User user, String rawPassword );

    /**
     * Encodes and sets the password of the UserCredentials.
     * Due to business logic required on password updates the password for a user
     * should only be changed using this method or {@link #encodeAndSetPassword(User, String) encodeAndSetPassword}
     * and not directly on the User or UserCredentials object.
     *
     * Note that the changes made to the UserCredentials object are not persisted.
     *
     * @param userCredentials the UserCredentials.
     * @param rawPassword the raw password.
     */
    void encodeAndSetPassword( UserCredentials userCredentials, String rawPassword );

    /**
     * Updates the last login date of UserCredentials with the given username
     * with the current date.
     *
     * @param username the username of the UserCredentials.
     */
    void setLastLogin( String username );

    Collection<UserCredentials> searchUsersByName( String key );

    Collection<UserCredentials> searchUsersByName( String name, int first, int max );

    Collection<UserCredentials> getUsersBetween( int first, int max );

    Collection<UserCredentials> getUsersBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetween( int first, int max );

    Collection<UserCredentials> getUsersWithoutOrganisationUnitBetweenByName( String name, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetween( OrganisationUnit orgUnit, int first, int max );

    Collection<UserCredentials> getUsersByOrganisationUnitBetweenByName( OrganisationUnit orgUnit, String name, int first, int max );

    Collection<UserCredentials> getSelfRegisteredUserCredentials( int first, int max );

    int getSelfRegisteredUserCredentialsCount();

    Collection<UserCredentials> getInactiveUsers( int months );

    Collection<UserCredentials> getInactiveUsers( int months, int first, int max );

    int getInactiveUsersCount( int months );

    int getActiveUsersCount( int days );

    int getActiveUsersCount( Date since );

    /**
     * Filters the given list of users based on whether the current
     * user is allowed to update.
     *
     * @param users the list of users.
     */
    void canUpdateUsersFilter( Collection<User> users );

    /**
     * Filters the given list of user credentials based on whether the current
     * user is allowed to update.
     *
     * @param userCredentials the list of user credentials.
     */
    void canUpdateFilter( Collection<UserCredentials> userCredentials );

    /**
     * Is the current user allowed to update this user?
     *
     * @param userCredentials credentials to check for allowing update.
     * @return true if current user can update this user, else false.
     */
    boolean canUpdate( UserCredentials userCredentials );
    
    boolean credentialsNonExpired( UserCredentials credentials );
    
    // -------------------------------------------------------------------------
    // UserAuthorityGroup
    // -------------------------------------------------------------------------

    /**
     * Adds a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup.
     * @return the generated identifier.
     */
    int addUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Updates a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup.
     */
    void updateUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     *
     * @param id the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup( int id );

    /**
     * Retrieves the UserAuthorityGroup with the given identifier.
     *
     * @param uid the identifier of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroup( String uid );

    /**
     * Retrieves the UserAuthorityGroup with the given name.
     *
     * @param name the name of the UserAuthorityGroup to retrieve.
     * @return the UserAuthorityGroup.
     */
    UserAuthorityGroup getUserAuthorityGroupByName( String name );

    /**
     * Deletes a UserAuthorityGroup.
     *
     * @param userAuthorityGroup the UserAuthorityGroup to delete.
     */
    void deleteUserAuthorityGroup( UserAuthorityGroup userAuthorityGroup );

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a Collection of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getAllUserAuthorityGroups();
    
    /**
     * Retrieves UserAuthorityGroups with the given UIDs.
     * 
     * @param uids the UIDs.
     * @return a List of UserAuthorityGroups.
     */
    List<UserAuthorityGroup> getUserRolesByUid( Collection<String> uids );

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a Collection of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getUserRolesBetween( int first, int max );

    /**
     * Retrieves all UserAuthorityGroups.
     *
     * @return a Collection of UserAuthorityGroups.
     */
    Collection<UserAuthorityGroup> getUserRolesBetweenByName( String name, int first, int max );

    void assignDataSetToUserRole( DataSet dataSet );

    int getUserRoleCount();

    int getUserRoleCountByName( String name );

    /**
     * Filters the given collection of user roles based on whether the current user
     * is allowed to issue it.
     * 
     * @param userRoles the collection of user roles.
     */
    void canIssueFilter( Collection<UserAuthorityGroup> userRoles );
}
