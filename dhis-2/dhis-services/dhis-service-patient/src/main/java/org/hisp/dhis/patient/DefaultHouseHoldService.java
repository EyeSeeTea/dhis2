/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultHouseHoldService
    implements HouseHoldService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HouseHoldStore houseHoldStore;

    public void setHouseHoldStore( HouseHoldStore houseHoldStore )
    {
        this.houseHoldStore = houseHoldStore;
    }

    // -------------------------------------------------------------------------
    // HouseHold
    // -------------------------------------------------------------------------

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.household.HouseHoldService#addHouseHold(org.hisp.dhis
     * .chis.household.HouseHold)
     */
    public int addHouseHold( HouseHold houseHold )
    {
        // TODO Auto-generated method stub

        return houseHoldStore.addHouseHold( houseHold );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.household.HouseHoldService#deleteHouseHold(org.hisp
     * .dhis.chis.household.HouseHold)
     */
    public void deleteHouseHold( HouseHold houseHold )
    {
        // TODO Auto-generated method stub

        houseHoldStore.deleteHouseHold( houseHold );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.chis.household.HouseHoldService#getAllHouseHolds()
     */
    public Collection<HouseHold> getAllHouseHolds()
    {
        // TODO Auto-generated method stub

        return houseHoldStore.getAllHouseHolds();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hisp.dhis.chis.household.HouseHoldService#getHouseHold(int)
     */
    public HouseHold getHouseHold( int id )
    {
        // TODO Auto-generated method stub

        return houseHoldStore.getHouseHold( id );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.household.HouseHoldService#getHouseHoldsForAReportingUnit
     * (org.hisp.dhis.organisationunit.OrganisationUnit)
     */
    public Collection<HouseHold> getHouseHoldsForOrgUnit( OrganisationUnit organisationUnit )
    {
        // TODO Auto-generated method stub

        return houseHoldStore.getHouseHoldsForOrgUnit( organisationUnit );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hisp.dhis.chis.household.HouseHoldService#updateHouseHold(org.hisp
     * .dhis.chis.household.HouseHold)
     */
    public void updateHouseHold( HouseHold houseHold )
    {
        // TODO Auto-generated method stub

        houseHoldStore.updateHouseHold( houseHold );
    }

    public String getNextHouseHoldNumber( OrganisationUnit registeringUnit )
    {
        // TODO Auto-generated method stub

        Collection<HouseHold> houseHolds = houseHoldStore.getHouseHoldsForOrgUnit( registeringUnit );

        List<String> sortedHouseHoldNumbers = new ArrayList<String>();

        String lastAssignedHouseNumber = null;

        String nextHouseNumber = null;

        for ( HouseHold houseHold : houseHolds )
        {
            sortedHouseHoldNumbers.add( houseHold.getHouseNumber() );
        }

        Collections.sort( sortedHouseHoldNumbers, Collections.reverseOrder() );

        if ( sortedHouseHoldNumbers.size() > 0 )
        {
            lastAssignedHouseNumber = sortedHouseHoldNumbers.get( 0 );

            lastAssignedHouseNumber = lastAssignedHouseNumber.substring( registeringUnit.getShortName().length() + 1 );
        }

        if ( lastAssignedHouseNumber == null )
        {

            nextHouseNumber = registeringUnit.getShortName() + HouseHold.HOUSE_NUMBER_FIRST_INDEX;

            return nextHouseNumber;
        }

        Integer nextIndex = Integer.parseInt( lastAssignedHouseNumber ) + 1;

        String nextIdentifierIndex = nextIndex.toString();

        if ( nextIdentifierIndex.length() < HouseHold.HOUSE_NUMBER_INDEX_LENGTH )
        {
            String prefix = "0";

            for ( int i = 1; i < HouseHold.HOUSE_NUMBER_INDEX_LENGTH - nextIdentifierIndex.length(); i++ )
            {
                prefix += "0";
            }

            nextHouseNumber = registeringUnit.getShortName() + "." + prefix + nextIdentifierIndex;
        }

        return nextHouseNumber;
    }

}
