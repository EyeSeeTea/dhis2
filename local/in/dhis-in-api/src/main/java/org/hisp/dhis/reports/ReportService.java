package org.hisp.dhis.reports;

/*
 * Copyright (c) 2004-2005, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in element and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of element code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the <ORGANIZATION> nor the names of its contributors may
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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

public interface ReportService
{
    String ID = ReportService.class.getName();

    // -------------------------------------------------------------------------
    // Report_in
    // -------------------------------------------------------------------------

    int addReport( Report_in report );

    void updateReport( Report_in report );

    void deleteReport( Report_in report );

    Report_in getReport( int id );

    Report_in getReportByName( String name );

    Collection<Report_in> getReportBySource( Source source );

    Collection<Report_in> getAllReports();

    Collection<Report_in> getReportsByReportType( String reportType );

    Collection<Report_in> getReportsByPeriodType( PeriodType periodType );

    Collection<Report_in> getReportsByPeriodAndReportType( PeriodType periodType, String reportType );

    Collection<Report_in> getReportsByPeriodSourceAndReportType( PeriodType periodType, Source source, String reportType );

    // -------------------------------------------------------------------------
    // Report_in Design
    // -------------------------------------------------------------------------

    Collection<Report_inDesign> getReportDesign( Report_in report );

    // -------------------------------------------------------------------------
    // 
    // -------------------------------------------------------------------------

    List<Calendar> getStartingEndingPeriods( String deType, Date startDate, Date endDate );

    String getRAFolderName();

    List<Integer> getLinelistingRecordNos( OrganisationUnit organisationUnit, Period period, String lltype );

    // -------------------------------------------------------------------------
    // ReportService Design 
    // -------------------------------------------------------------------------

    List<Period> getMonthlyPeriods( Date start, Date end );

    PeriodType getPeriodTypeObject( String periodTypeName );

    Period getPeriodByMonth( int month, int year, PeriodType periodType );

    List<OrganisationUnit> getAllChildren( OrganisationUnit selecteOU );

    PeriodType getDataElementPeriodType( DataElement de );
    
    // -------------------------------------------------------------------------
    // ReportService for Report Result Action
    // -------------------------------------------------------------------------
    
    List<Report_inDesign> getReportDesign( String fileName );
    
    String getResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit , String reportModelTB );
    
    String getIndividualResultDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit, String reportModelTB );
    
    String getResultIndicatorValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit );
    
    String getIndividualResultIndicatorValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit );
    
    String getBooleanDataValue( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit , String reportModelTB );
    
    List<Calendar> getStartingEndingPeriods( String deType , Period selectedPeriod );
    
    Period getPreviousPeriod( Period selectedPeriod );
    
    String getResultSurveyValue( String formula, OrganisationUnit organisationUnit );

    String getSurveyDesc( String formula );

    String getResultDataValueFromAggregateTable( String formula, Collection<Integer> periodIds, OrganisationUnit organisationUnit , String reportModelTB );

    String getAggCountForTextData( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit );
    
    String getCountForTextData( String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit );
}
