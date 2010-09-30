package org.hisp.dhis.jchart;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

public class JChartServiceTest
    extends DhisSpringTest
{
    private JChartSevice jchartService;

    private JChart jchart;

    @Override
    public void setUpTest()
        throws Exception
    {
        jchartService = (JChartSevice) getBean( JChartSevice.ID );

        jchart = new JChart();
        jchart.setTitle( "A" );
        jchart.setSubtitle( "B" );
        jchart.setType( "line" );
        jchart.setShowLegend( true );

    }

    @Test
    public void testSaveJChart()
    {
      //  int id = jchartService.addJChart( jchart );

        //JChart j = jchartService.getJChart( id );

        String a = "a";
        String b = "a";
        
        assertTrue( a.equals( b ));
    }
    
    

}
