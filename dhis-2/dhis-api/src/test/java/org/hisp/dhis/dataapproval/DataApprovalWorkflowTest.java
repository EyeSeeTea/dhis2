package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import static com.google.common.collect.Sets.newHashSet;

import static org.junit.Assert.assertArrayEquals;

import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

import java.util.List;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DataApprovalWorkflowTest
{
    @Test
    public void testGetMembersSortedByLevel()
    {
        DataApprovalLevel level1 = new DataApprovalLevel( "These", 1, 1, null, null, null );
        DataApprovalLevel level2 = new DataApprovalLevel( "names", 2, 2, null, null, null );
        DataApprovalLevel level3 = new DataApprovalLevel( "are", 3, 3, null, null, null );
        DataApprovalLevel level4 = new DataApprovalLevel( "not", 4, 4, null, null, null );
        DataApprovalLevel level5 = new DataApprovalLevel( "in", 5, 5, null, null, null );
        DataApprovalLevel level6 = new DataApprovalLevel( "any", 6, 6, null, null, null );
        DataApprovalLevel level7 = new DataApprovalLevel( "particular", 7, 7, null, null, null );
        DataApprovalLevel level8 = new DataApprovalLevel( "alphabetical", 8, 8, null, null, null );
        DataApprovalLevel level9 = new DataApprovalLevel( "order", 9, 9, null, null, null );

        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );

        DataApprovalWorkflow workflow = new DataApprovalWorkflow( "Test", periodType,
            newHashSet(level9, level8, level7, level6, level5, level4, level3, level2, level1 ) );

        List<DataApprovalLevel> membersSortedByLevel = workflow.getSortedLevels();

        DataApprovalLevel[] arrayMembersSortedByLevel = membersSortedByLevel.toArray( new DataApprovalLevel[ membersSortedByLevel.size() ] );

        DataApprovalLevel[] expectedSortedByLevel = { level1, level2, level3, level4, level5, level6, level7, level8, level9 };

        assertArrayEquals( expectedSortedByLevel, arrayMembersSortedByLevel );
    }
}
