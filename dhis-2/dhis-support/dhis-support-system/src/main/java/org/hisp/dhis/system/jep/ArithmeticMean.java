package org.hisp.dhis.system.jep;

import java.util.List;
import java.util.Stack;
import org.nfunk.jep.ParseException;

import org.nfunk.jep.function.PostfixMathCommand;

public class ArithmeticMean extends PostfixMathCommand {
	public ArithmeticMean() {
		numberOfParameters = 1;
	}
	
	public void run(Stack inStack) throws ParseException {
		// check the stack
		checkStack(inStack);
		
		Object param= inStack.pop();
		if (param instanceof Double) {
			inStack.push(param);
		}
		else if (param instanceof List) {
			List<Double> vals=(List<Double>) param;
			double sum=0; for (Double v: vals) {
				sum=sum+v;}
			inStack.push(new Double(sum/(vals.size())));
			}
		}
	}

