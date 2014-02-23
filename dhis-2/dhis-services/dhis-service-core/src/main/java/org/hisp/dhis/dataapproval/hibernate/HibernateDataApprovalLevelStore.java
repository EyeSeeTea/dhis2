package org.hisp.dhis.dataapproval.hibernate;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.hibernate.criterion.Order;
import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.period.PeriodService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Jim Grace
 * @version $Id$
 */
@Transactional
public class HibernateDataApprovalLevelStore
        extends HibernateGenericStore<DataApprovalLevel>
        implements DataApprovalLevelStore
{
    private static final boolean LOG = false;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataApprovalLevelStore dataApprovalLevelStore;

    public void setDataApprovalLevelStore( DataApprovalLevelStore dataApprovalLevelStore )
    {
        this.dataApprovalLevelStore = dataApprovalLevelStore;
    }

    // -------------------------------------------------------------------------
    // DataApprovalLevel
    // -------------------------------------------------------------------------

    public List<DataApprovalLevel> getAllDataApprovalLevels()
    {
        if (LOG) log( "get all data approval levels" );

        return getCriteria().addOrder( Order.asc( "level" ) ).list();
    }

    public void addDataApproval( DataApprovalLevel dataApprovalLevel )
    {
        if (LOG) log( "save " + dataApprovalLevel.getLevel() + " " + dataApprovalLevel.getName() );

        save( dataApprovalLevel );
    }

    public void updateDataApprovalLevel( DataApprovalLevel dataApprovalLevel )
    {
        if (LOG) log( "update " + dataApprovalLevel.getLevel() + " " + dataApprovalLevel.getName() );

        update( dataApprovalLevel );
    }

    public void deleteDataApprovalLevel( DataApprovalLevel dataApprovalLevel )
    {
        if (LOG) log( "delete " + dataApprovalLevel.getLevel() + " " + dataApprovalLevel.getName() );

        delete( dataApprovalLevel );
    }
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void log(String s)
    {
        System.out.println( s );
    }
}
