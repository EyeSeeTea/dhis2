package org.hisp.dhis.dataapproval;

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

/**
 * Current state of data approval for a selected combination of data set, period,
 * organisation unit, and category options or category group options.
 *
 * @author Jim Grace
 */
public enum DataApprovalState
{
    /**
     * Data approval does not apply to this selection. (Data is neither
     * "approved" nor "unapproved".)
     * <p>
     * approved=false unapproved=false approvable=false accepted=false ready=false
     */
    UNAPPROVABLE ( false, false, false, false, false ),

    /**
     * Data is unapproved, and is waiting for some lower-level approval.
     * <p>
     * approved=false unapproved=true approvable=true accepted=false ready=false
     */
    UNAPPROVED_WAITING ( false, true, true, false, false ),

    /**
     * Data is unapproved, and is waiting for approval at a higher organisation
     * unit level (not approvable here.)
     * <p>
     * approved=false unapproved=true approvable=false accepted=false ready=false
     */
    UNAPPROVED_ELSEWHERE ( false, true, false, false, false ),

    /**
     * Data is unapproved, and is ready to be approved for this selection.
     * <p>
     * approved=false unapproved=true approvable=true accepted=false ready=true
     */
    UNAPPROVED_READY ( false, true, true, false, true ),

    /**
     * Some periods within this multi-period selection are approved here
     * and some are not approved (but ready for approval here.)
     * <p>
     * approved=false unapproved=false approvable=true accepted=false ready=true
     */
    PARTIALLY_APPROVED_HERE( false, false, true, false, true ),

    /**
     * Data is approved, and was approved here (so could be unapproved here.)
     * <p>
     * approved=true unapproved=false approvable=true accepted=false ready=false
     */
    APPROVED_HERE ( true, false, true, false, false ),

    /**
     * Some periods within this multi-period selection are approved elsewhere
     * and some are not approved elsewhere (at a higher organisation unit level
     * -- not approvable here.)
     * <p>
     * approved=false unapproved=false approvable=false accepted=false ready=false
     */
    PARTIALLY_APPROVED_ELSEWHERE ( false, false, false, false, false ),

    /**
     * Data is approved, but at a higher organisation unit level
     * (so cannot be unapproved here.)
     * <p>
     * approved=true unapproved=false approvable=false accepted=false ready=false
     */
    APPROVED_ELSEWHERE( true, false, false, false, false ),

    /**
     * Some periods within this multi-period selection are accepted here
     * and some are not approved elsewhere (not approvable here.)
     * <p>
     * approved=true unapproved=false approvable=true accepted=false ready=false
     */
    PARTIALLY_ACCEPTED_HERE( true, false, true, false, false ),

    /**
     * Data is approved and accepted here (so could be unapproved here.)
     * <p>
     * approved=true unapproved=false approvable=true accepted=true ready=false
     */
    ACCEPTED_HERE ( true, false, true, true, false ),

    /**
     * Some periods within this multi-period selection are accepted elsewhere
     * and some are approved elsewhere (at a higher organisation unit level --
     * not approvable here.)
     * <p>
     * approved=false unapproved=false approvable=false accepted=false ready=false
     */
    PARTIALLY_ACCEPTED_ELSEWHERE ( false, false, false, false, false ),

    /**
     * Data is approved and accepted, but at a higher organisation unit level --
     * not approvable here.
     * <p>
     * approved=true unapproved=false approvable=false accepted=true ready=false
     */
    ACCEPTED_ELSEWHERE ( true, false, false, true, false );

    /**
     * Is this data approved (and therefore locked)?
     */
    private boolean approved;

    /**
     * Is this data unapproved (could be approved but is not)?
     */
    private boolean unapproved;

    /**
     * Is this data approvable for this selection?
     */
    private boolean approvable;

    /**
     * Is this data (approved and) accepted?
     */
    private boolean accepted;

    /**
     * Is this data ready to be approved in this combination of data set, etc.?
     */
    private boolean ready;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    DataApprovalState( boolean approved, boolean unapproved,
                       boolean approvable, boolean accepted, boolean ready )
    {
        this.approved = approved;
        this.unapproved = unapproved;
        this.approvable = approvable;
        this.accepted = accepted;
        this.ready = ready;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public boolean isApproved()
    {
        return approved;
    }

    public boolean isUnapproved()
    {
        return unapproved;
    }

    public boolean isApprovable()
    {
        return approvable;
    }

    public boolean isAccepted()
    {
        return accepted;
    }

    public boolean isReady()
    {
        return ready;
    }
}

