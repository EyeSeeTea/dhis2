package org.hisp.dhis.system.jep;

import java.util.List;

import org.hisp.dhis.system.jep.ArithmeticMean;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;

public class CustomFunctions {
	public static void addFunctions(JEP parser)
	{
        parser.addFunction("mean",new ArithmeticMean());
        parser.addFunction("stdev",new StandardDeviation());
        parser.addFunction("median",new MedianValue());
        parser.addFunction("maxval",new MaxValue());
        parser.addFunction("minval",new MinValue());
        parser.addFunction("count",new Count());
	}
	
	public static List<Double> checkVector(Object param) throws ParseException
	{
		if (param instanceof List) {
			List<Object> vals=(List<Object>) param;
			for (Object val: vals) {
				if (!(val instanceof Double))
					throw new ParseException("Non numeric vector");
			}
			return (List<Double>) param;
		}
		else throw new ParseException("Invalid vector argument");
	}

}
