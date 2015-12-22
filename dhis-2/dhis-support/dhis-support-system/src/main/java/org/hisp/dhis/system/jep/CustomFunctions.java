package org.hisp.dhis.system.jep;

import java.util.List;

import org.hisp.dhis.system.jep.ArithmeticMean;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;

public class CustomFunctions {
	public static void addFunctions(JEP parser)
	{
        parser.addFunction("AVG",new ArithmeticMean());
        parser.addFunction("STDDEV",new StandardDeviation());
        parser.addFunction("MEDIAN",new MedianValue());
        parser.addFunction("MAX",new MaxValue());
        parser.addFunction("MIN",new MinValue());
        parser.addFunction("COUNT",new Count());
        parser.addFunction("VSUM", new VectorSum());
	}
	
	@SuppressWarnings("unchecked") 
	public static List<Double> checkVector(Object param) throws ParseException
	{
		if (param instanceof List) {
			List<?> vals=(List<?>) param;
			for (Object val: vals) {
				if (!(val instanceof Double))
					throw new ParseException("Non numeric vector");
			}
			return (List<Double>) param;
		}
		else throw new ParseException("Invalid vector argument");
	}

}
