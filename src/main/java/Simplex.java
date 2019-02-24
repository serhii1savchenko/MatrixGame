import org.apache.commons.math4.optim.PointValuePair;
import org.apache.commons.math4.optim.linear.*;
import org.apache.commons.math4.optim.nonlinear.scalar.GoalType;

import java.util.ArrayList;
import java.util.Arrays;

public class Simplex {

    public static double[] getPsForA(double[][] matrix) {
        double[][] table = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = matrix[j][i];
            }
        }

        System.out.println("Інвертована матриця для пошуку змішаної стратегії для гравця А симплекс методом: ");
        Main.printMatrix(table);
        System.out.println();

        double[] ksi = new double[table[0].length];
        for (int i = 0; i < ksi.length; i++)
            ksi[i] = 1;

        LinearObjectiveFunction f = new LinearObjectiveFunction(ksi, 0);
        ArrayList<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        for (int j = 0; j < table.length; j++) {
            constraints.add(new LinearConstraint(table[j], Relationship.GEQ, 1));
        }

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize(f, new LinearConstraintSet(constraints), GoalType.MINIMIZE,
                new NonNegativeConstraint(true), PivotSelectionRule.BLAND);

        double[] ksiResult = solution.getPoint();
        System.out.println(Arrays.toString(ksiResult));
        double min = solution.getValue();
        double v = (double)1/min;
        System.out.println("Ціна гри = " + v);

        double[] p = new double[ksiResult.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = ksiResult[i]*v;
        }

        return p;
    }

}
