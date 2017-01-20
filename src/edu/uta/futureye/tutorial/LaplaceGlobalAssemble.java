package edu.uta.futureye.tutorial;

import static edu.uta.futureye.function.FMath.C0;
import static edu.uta.futureye.function.FMath.grad;
import static edu.uta.futureye.function.FMath.x;
import static edu.uta.futureye.function.FMath.y;

import java.util.HashMap;

import edu.uta.futureye.algebra.intf.Matrix;
import edu.uta.futureye.algebra.intf.Vector;
import edu.uta.futureye.algebra.solver.external.SolverJBLAS;
import edu.uta.futureye.core.Mesh;
import edu.uta.futureye.core.NodeType;
import edu.uta.futureye.core.intf.LHSExpr;
import edu.uta.futureye.core.intf.RHSExpr;
import edu.uta.futureye.function.intf.MathFunc;
import edu.uta.futureye.io.MeshReader;
import edu.uta.futureye.io.MeshWriter;
import edu.uta.futureye.lib.assembler.AssemblerJIT;
import edu.uta.futureye.lib.element.FELinearTriangleJIT;
import edu.uta.futureye.lib.weakform.WeakFormJIT;
import edu.uta.futureye.util.Utils;
import edu.uta.futureye.util.container.ElementList;

/**
 * Use assembleGlobal() for simple case
 * 
 * <blockquote>
 * 
 * <pre>
 * Problem:
 *   -\Delta{u} = f
 *   u(x,y)=0, (x,y) \in \partial{\Omega}
 * where
 *   \Omega = [-3,3]*[-3,3]
 *   f = 2 * pi * pi * sin ( pi * x ) * sin ( pi * y )
 * Solution:
 *   u = (x^2-9)*(y^2-9)
 * </blockquote>
 * </pre>
 * 
 * @author liuyueming
 */

public class LaplaceGlobalAssemble {
	public Mesh mesh; // mesh object
	public Vector u; // solution vector

	public void run() {
		// 1.Generate mesh
		Mesh mesh = null;
		MeshReader reader = new MeshReader("triangle.grd");
		mesh = reader.read2DMesh();

		// Compute geometry relationship between nodes and elements
		mesh.computeNodeBelongsToElements();

		// 2.Mark border types
		HashMap<NodeType, MathFunc> mapNTF = new HashMap<NodeType, MathFunc>();
		mapNTF.put(NodeType.Dirichlet, null);
		mesh.markBorderNode(mapNTF);

		// 3.Use finite element library to assign degrees of
		// freedom (DOF) to element
		ElementList eList = mesh.getElementList();
		FELinearTriangleJIT fet = new FELinearTriangleJIT();
		for (int i = 1; i <= eList.size(); i++)
			fet.assignTo(eList.at(i));

		//4. Weak form
		//Right hand side(RHS):
		final MathFunc f = -2 * (x * x + y * y) + 36;
		WeakFormJIT wf = new WeakFormJIT(
				fet, 
				(u,v) -> grad(u, "x", "y").dot(grad(v, "x", "y")), 
				v -> f * v
			);

		long startCompile = System.currentTimeMillis();
		wf.compile();
		System.out.println("Compile time: "
				+ (System.currentTimeMillis() - startCompile));

		// 5.Assembly process
		AssemblerJIT assembler = new AssemblerJIT(wf);
		Matrix stiff = null;
		Vector load = null;

		assembler.assembleGlobal(mesh);
		stiff = assembler.getGlobalStiffMatrix();
		load = assembler.getGlobalLoadVector();

		// Boundary condition
		Utils.imposeDirichletCondition(stiff, load, mesh, C0);

		// 6.Solve linear system
		SolverJBLAS solver = new SolverJBLAS();
		Vector u = solver.solveDGESV(stiff, load);
		System.out.println("u=");
		for (int i = 1; i <= u.getDim(); i++)
			System.out.println(String.format("%.3f ", u.get(i)));

		// 7.Output results to an Techplot format file
		MeshWriter writer = new MeshWriter(mesh);
		writer.writeTechplot("./tutorial/Laplace2D.dat", u);

		this.mesh = mesh;
		this.u = u;
	}

	public static void main(String[] args) {
		LaplaceGlobalAssemble ex1 = new LaplaceGlobalAssemble();
		ex1.run();
	}
}