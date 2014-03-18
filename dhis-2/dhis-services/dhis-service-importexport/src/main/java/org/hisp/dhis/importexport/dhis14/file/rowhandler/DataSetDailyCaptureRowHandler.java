package org.hisp.dhis.importexport.dhis14.file.rowhandler;

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

import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.importexport.ImportObjectService;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.analysis.ImportAnalyser;
import org.hisp.dhis.importexport.importer.DataSetImporter;
import org.hisp.dhis.period.DailyPeriodType;

import com.ibatis.sqlmap.client.event.RowHandler;

public class DataSetDailyCaptureRowHandler
    extends DataSetImporter
    implements RowHandler
{
    private ImportParams params;

    private Map<String, Integer> periodTypeMapping;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DataSetDailyCaptureRowHandler( BatchHandler<DataSet> batchHandler, ImportObjectService importObjectService,
        DataSetService dataSetService, Map<String, Integer> periodTypeMapping, ImportParams params,
        ImportAnalyser importAnalyser )
    {
        this.batchHandler = batchHandler;
        this.importObjectService = importObjectService;
        this.dataSetService = dataSetService;
        this.periodTypeMapping = periodTypeMapping;
        this.params = params;
        this.importAnalyser = importAnalyser;
    }

    // -------------------------------------------------------------------------
    // RowHandler implementation
    // -------------------------------------------------------------------------

    public void handleRow( Object object )
    {
        final DataSet dataSet = (DataSet) object;

        Integer dailyPeriodTypeId = periodTypeMapping.get( DailyPeriodType.NAME );

        dataSet.getPeriodType().setId( dailyPeriodTypeId );

        importObject( dataSet, params );
    }
}
