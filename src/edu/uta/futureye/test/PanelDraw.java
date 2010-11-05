package edu.uta.futureye.test;

import java.awt.Graphics;

import javax.swing.JPanel;

import edu.uta.futureye.function.Variable;
import edu.uta.futureye.function.basic.FConstant;
import edu.uta.futureye.function.intf.FunctionDerivable;
import edu.uta.futureye.function.operator.FOBasicDerivable;
import edu.uta.futureye.function.shape.SFBilinearLocal2D;

public class PanelDraw extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FunctionDerivable fx;
	public FunctionDerivable fy;
	
	
	public void paintComponent(Graphics g){
		super.paintComponent(g); 
		
		int xs[] = {100,600,500,100};
		int ys[] = {100,100,500,600};
		
		int n = 4;
		SFBilinearLocal2D[] shapeFun = new SFBilinearLocal2D[4];
		for(int i=1;i<=n;i++)
			shapeFun[i-1] = new SFBilinearLocal2D(i);
		
		fx = new FConstant(0.0);
		fy = new FConstant(0.0);
		
		for(int i=0;i<n;i++) {
			fx = FOBasicDerivable.Plus(fx, 
					FOBasicDerivable.Mult(new FConstant(xs[i]), shapeFun[i]));
			fy = FOBasicDerivable.Plus(fy, 
					FOBasicDerivable.Mult(new FConstant(ys[i]), shapeFun[i]));
		}
		
		Variable p1 = new Variable();
		Variable p2 = new Variable();
		Variable p3 = new Variable();
		Variable p4 = new Variable();
		p1.set("r", -1); p1.set("s", -1);
		p2.set("r", -1); p2.set("s", 1);
		p3.set("r", 1); p3.set("s", 1);
		p4.set("r", 1); p4.set("s", -1);
		int xs2[] = {(int)fx.value(p1),(int)fx.value(p2),(int)fx.value(p3),(int)fx.value(p4)};
		int ys2[] = {(int)fy.value(p1),(int)fy.value(p2),(int)fy.value(p3),(int)fy.value(p4)};
		
		//Should be the same :)
		g.drawPolygon(xs, ys, 4);
		g.drawPolygon(xs2, ys2, 4);
		
		
		int ref_xs[] = {600,800,800,600};
		int ref_ys[] = {100,100,300,300};
		g.drawPolygon(ref_xs, ref_ys, 4);

	}
}
