package org.hisp.dhis.system.jep;

import org.hisp.dhis.system.jep.ArithmeticMean;
import org.nfunk.jep.JEP;

public class CustomFunctions {
	public static void addFunctions(JEP parser)
	{
        parser.addFunction("mean",new ArithmeticMean());

	}

}
