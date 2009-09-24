package org.hisp.dhis.program;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public interface ProgramStore
{

    String ID = ProgramService.class.getName();

    int addProgram( Program program );

    void deleteProgram( Program program );

    void updateProgram( Program program );

    Program getProgram( int id );

    Collection<Program> getAllPrograms();

    Collection<Program> getPrograms( OrganisationUnit organisationUnit );

    Collection<Program> getPrograms( DataSet dataSet );

    Collection<Program> getPrograms( OrganisationUnit organisationUnit, DataSet dataSet );

}
