package edu.uta.futureye.function.basic;

import java.util.List;

import edu.uta.futureye.function.DerivativeIndicator;
import edu.uta.futureye.function.Variable;
import edu.uta.futureye.function.intf.FunctionDerivable;

/**
 * f(x) = an*x^n + an_1*x^(n-1) + ... + a1*x + a0
 * @author liuyueming
 *
 */
public class FPolynomial1D extends FAbstract {
	List<Double> coefList;
	
	/**
	 * 构造一个多项式
	 * @param coefList
	 * a0 = coefList.get(0)
	 * ...
	 * an = coefList.get(coefList.size()-1)
	 */
	public FPolynomial1D(List<Double> coefList) {
		this.coefList = coefList;
	}
	
	@Override
	public FunctionDerivable derivative(DerivativeIndicator di) {
		int degree = di.get().values().iterator().next();
		return derivative1(degree,degree);
	}
	
	protected FPolynomial1D derivative1(int degree,int maxDegree) {
		int d = degree;
		if(d >= coefList.size()) {
			coefList.clear();
			return this;
		}
		if(0<d && d <= maxDegree) {
			coefList.remove(0);
			for(int i=0;i<coefList.size();i++) {
				coefList.set(i, coefList.get(i)*(i+1));
			}
			derivative1(--d,maxDegree);
		}
		return this;
	}
	
	@Override
	public double value(Variable v) {
		double x = v.get(varNames().get(0));
		double f = 0.0;
		for(int i=0;i<coefList.size();i++) {
			f += coefList.get(i)*Math.pow(x, i);
		}
		return f;
	}
}
