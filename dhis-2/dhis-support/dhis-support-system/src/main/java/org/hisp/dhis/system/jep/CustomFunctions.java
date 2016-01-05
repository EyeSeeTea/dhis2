package org.hisp.dhis.system.jep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.hisp.dhis.system.jep.ArithmeticMean;
import org.jfree.util.Log;
import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommandI;

public class CustomFunctions {
    public static Map<String,PostfixMathCommandI> aggregate_functions=
	new HashMap<String,PostfixMathCommandI>();

    public static void addFunctions(JEP parser)
    {
	for (Entry<String,PostfixMathCommandI> e: 
		 aggregate_functions.entrySet()) {
	    try {
		Class<PostfixMathCommandI> c=
		    (Class<PostfixMathCommandI>) 
		    (e.getValue().getClass());
		PostfixMathCommandI cmd=(PostfixMathCommandI) 
		    c.newInstance();
		parser.addFunction(e.getKey(),cmd);}
	    catch (Exception ex) {
		Log.warn(ex);

	    }
	}
    }

    public static Pattern aggregate_prefix=Pattern.compile("");
    private static void updateAggregatePattern(){
	StringBuffer s=new StringBuffer(); int i=0; s.append("(");
	for (String key: aggregate_functions.keySet()) {
	    if (i>0) s.append('|'); else i++; 
	    s.append(key);}
	s.append("\\s+\\(");
	aggregate_prefix=Pattern.compile(s.toString());}



    public static void addAggregateFunction(String name,PostfixMathCommandI fn){
	aggregate_functions.put(name,fn);
	updateAggregatePattern();
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

    void init(){
	addAggregateFunction("AVG",new ArithmeticMean());
	addAggregateFunction("STDDEV",new StandardDeviation());
	addAggregateFunction("MEDIAN",new MedianValue());
	addAggregateFunction("MAX",new MaxValue());
	addAggregateFunction("MIN",new MinValue());
	addAggregateFunction("COUNT",new Count());
	addAggregateFunction("VSUM",new VectorSum());
    }
}
