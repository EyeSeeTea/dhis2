/**
 * 
 */
package org.hisp.dhis.activityplan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

/**
 * @author abyotag_adm
 * 
 */
public class DefaultActivityPlanService
    implements ActivityPlanService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    // -------------------------------------------------------------------------
    // ActivityPlan
    // -------------------------------------------------------------------------

    @Override
    public Collection<Activity> getActivitiesByBeneficiary( Patient beneficiary )
    {

        // ---------------------------------------------------------------------
        // Get any active program for the beneficiary ( completed = false )
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( beneficiary, false );

        // ---------------------------------------------------------------------
        // Get next activities for the active programInstances
        // ---------------------------------------------------------------------

        return getActivties( programInstances );

    }

    public Collection<Activity> getActivitiesByProvider( OrganisationUnit organisationUnit )
    {
        Collection<Activity> activities = new ArrayList<Activity>();

        Collection<Program> programs = programService.getPrograms( organisationUnit );

        if ( programs.size() > 0 )
        {
            // -----------------------------------------------------------------
            // For all the programs a facility is servicing get active programs,
            // those with active instances (completed = false)
            // -----------------------------------------------------------------

            Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs, false );

            // -----------------------------------------------------------------
            // Get next activities for the active programInstances
            // -----------------------------------------------------------------

            activities = getActivties( programInstances );
        }

        return activities;
    }

    public Collection<Activity> getActivitiesByProvider( OrganisationUnit organisationUnit, Collection<Program> programs )
    {

        Collection<Activity> activities = new ArrayList<Activity>();

        if ( programService.getPrograms( organisationUnit ).containsAll( programs ) )
        {
            Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs, false );

            // -----------------------------------------------------------------
            // Get next activities for the active programInstances
            // -----------------------------------------------------------------

            activities = getActivties( programInstances );
        }

        return activities;
    }

    public Collection<Activity> getActivitiesByProgram( Collection<Program> programs )
    {

        Collection<Activity> activities = new ArrayList<Activity>();

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs, false );

        // -----------------------------------------------------------------
        // Get next activities for the active programInstances
        // -----------------------------------------------------------------

        activities = getActivties( programInstances );

        return activities;
    }

    @Override
    public Collection<Activity> getActivitiesByTask( ProgramStageInstance task )
    {
        // ---------------------------------------------------------------------
        // Get the parent program for the given program stage
        // ---------------------------------------------------------------------

        Program program = task.getProgramInstance().getProgram();

        // ---------------------------------------------------------------------
        // Pick only those active instances for the identified program
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( program, false );

        // ---------------------------------------------------------------------
        // Get next activities for the active programInstances
        // ---------------------------------------------------------------------

        return getActivties( programInstances );
    }

    @Override
    public Collection<Activity> getActivitiesByDueDate( Date dueDate )
    {
        // ---------------------------------------------------------------------
        // Get all active stageInstances within the given due date
        // ---------------------------------------------------------------------

        Collection<ProgramStageInstance> programStageInstances = programStageInstanceService.getProgramStageInstances(
            dueDate, false );

        Collection<Activity> activities = new ArrayList<Activity>();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {

            Activity activity = new Activity();
            activity.setBeneficiary( programStageInstance.getProgramInstance().getPatient() );
            activity.setTask( programStageInstance );
            activity.setDueDate( programStageInstance.getDueDate() );
            activities.add( activity );
        }

        return activities;
    }

    @Override
    public Collection<Activity> getActivitiesWithInDate( Date startDate, Date endDate )
    {
        // ---------------------------------------------------------------------
        // Get all active stageInstances within the given time frame
        // ---------------------------------------------------------------------

        Collection<ProgramStageInstance> programStageInstances = programStageInstanceService.getProgramStageInstances(
            startDate, endDate, false );

        Collection<Activity> activities = new ArrayList<Activity>();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {

            Activity activity = new Activity();
            activity.setBeneficiary( programStageInstance.getProgramInstance().getPatient() );
            activity.setTask( programStageInstance );
            activity.setDueDate( programStageInstance.getDueDate() );
            activities.add( activity );
        }

        return activities;

    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    Collection<Activity> getActivties( Collection<ProgramInstance> programInstances )
    {
        Collection<Activity> activities = new ArrayList<Activity>();

        // ---------------------------------------------------------------------
        // Get all stageInstances for the give programInstances
        // ---------------------------------------------------------------------

        Collection<ProgramStageInstance> programStageInstances = programStageInstanceService
            .getProgramStageInstances( programInstances );

        Map<String, ProgramStageInstance> mappedStageInstance = new HashMap<String, ProgramStageInstance>();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            mappedStageInstance.put( programStageInstance.getProgramInstance().getId() + "_"
                + programStageInstance.getProgramStage().getId(), programStageInstance );

        }

        // -----------------------------------------------------------------
        // Initially assume to have a first visit for all programInstances
        // -----------------------------------------------------------------

        Map<Integer, Integer> visitsByProgramInstances = new HashMap<Integer, Integer>();

        for ( ProgramInstance programInstance : programInstances )
        {
            programStageInstances.addAll( programInstance.getProgramStageInstances() );

            visitsByProgramInstances.put( programInstance.getId(), 0 );
        }

        // ---------------------------------------------------------------------
        // For each of these active instances, see at which stage they are
        // actually (may not necessarily be at the first stage)
        // ---------------------------------------------------------------------

        Collection<PatientDataValue> patientDataValues = patientDataValueService
            .getPatientDataValues( programStageInstances );

        for ( PatientDataValue patientDataValue : patientDataValues )
        {
            if ( visitsByProgramInstances.get( patientDataValue.getProgramStageInstance().getProgramInstance().getId() ) < patientDataValue
                .getProgramStageInstance().getProgramStage().getStageInProgram() )
            {
                visitsByProgramInstances.put( patientDataValue.getProgramStageInstance().getProgramInstance().getId(),
                    patientDataValue.getProgramStageInstance().getProgramStage().getStageInProgram() );
            }
        }

        // ---------------------------------------------------------------------
        // For each of these active instances, based on the current stage
        // determine the next stage
        // ---------------------------------------------------------------------

        for ( ProgramInstance programInstance : programInstances )
        {

            Program program = programInstance.getProgram();

            ProgramStage nextStage = program.getProgramStageByStage( visitsByProgramInstances.get( programInstance
                .getId() ) + 1 );

            if ( nextStage != null )
            {

                ProgramStageInstance nextStageInstance = mappedStageInstance.get( programInstance.getId() + "_"
                    + nextStage.getId() );

                Activity activity = new Activity();
                activity.setBeneficiary( programInstance.getPatient() );
                activity.setTask( nextStageInstance );
                activity.setDueDate( nextStageInstance.getDueDate() );

                activities.add( activity );
            }
        }

        return activities;

    }

}
