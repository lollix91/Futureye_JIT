package edu.uta.futureye.function.basic;

import edu.uta.futureye.function.AbstractSimpleMathFunc;
import edu.uta.futureye.function.Variable;
import edu.uta.futureye.function.intf.MathFunc;
import edu.uta.futureye.util.Constant;

public class FPow extends AbstractSimpleMathFunc {
	protected double exponent;
	
	public FPow(double exponent) {
		super("pow", Constant.x);
		this.exponent = exponent;
	}
	
	public FPow(String varName, double exponent) {
		super("pow", varName);
		this.exponent = exponent;
	}

	@Override
	public MathFunc diff(String varName) {
		if(varName.equals(this.varName)) {
			if(exponent == 0.0)
				return FC.C0;
			else if(exponent == 1.0)
				return FC.C1;
			else {
				FPow ret = new FPow(this.varName, this.exponent-1);
				ret.argIdx = this.argIdx;
				ret.fName = this.fName;
				return ret.M(exponent);
			}
		} else {
			return FC.C0;
		}
	}
	
//	@Override
//	public MathFunc copy() {
//		FPow ret = new FPow(this.varName, this.exponent);
//		ret.argIdx = this.argIdx;
//		ret.fName = this.fName;
//		return ret;
//	}

	@Override
	public double apply(double... args) {
		return Math.pow(args[argIdx], exponent);
	}

	@Override
	public double apply(Variable v) {
		return Math.pow(v.get(varName), exponent);
	}
	
	@Override
	public String getExpr() {
		return fName + "(" + varName + ", "+this.exponent+")";
	}
	
	@Override
	public String toString() {
		return fName + "(" + varName + ", "+this.exponent+")";
	}

}