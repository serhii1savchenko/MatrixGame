import java.util.*;

public class Main {

    public static void main(String[] args)  {

//        double[][] matrix = getMatrix();
//        double[][] matrix = {{0.9, 0.4, 0.2},
//                {0.3, 0.6, 0.8},
//                {0.3, 0.4, 0.8},
//                {0.5, 0.7, 0.2}};
        double[][] matrix = {{0.9, 0.4, 0.2, 1, 0.7, 0.4, 0.4},
                             {0.3, 0.6, 0.8, 0.1, 1, 0.2, 0.2},
                             {0.3, 0.6, 0.8, 0.1, 1, 0.2, 0.2}};
//        double[][] matrix = {{8, 2, 4},
//                             {4, 5, 6},
//                             {1, 7, 3}};

        matrix = removeDuplicateRowsAndCols(matrix);
        System.out.println("Матриця після видалення дублюючих рядків та стовпців");
        printMatrix(matrix);
        System.out.println();

        matrix = optimizeMatrix(matrix);
        System.out.println("Матриця після оптимізації");
        printMatrix(matrix);
        System.out.println();

        double[] optimalA = getOptimalStrategyForA(matrix);
        double[] optimalB = getOptimalStrategyForB(matrix);

        double lowGamePrice = optimalA[1];
        double maxGamePrice = optimalB[1];
        int optimalStrategyA = (int)optimalA[0];
        int optimalStrategyB = (int)optimalB[0];

        if (lowGamePrice == maxGamePrice) {
            System.out.println("Матрична гра має сідлову точку (" + maxGamePrice + ")");
            System.out.println("Ціна гри = " + maxGamePrice);
        } else {
            System.out.println("Нижня ціна гри (максимінний виграш) = " + lowGamePrice);
            System.out.println("Верхня ціна гри (мінімаксний виграш) = " + maxGamePrice);
            System.out.println("Максимінна стратегія для гравця A = " + optimalStrategyA);
            System.out.println("Мінімаксна стратегія для гравця B = " + optimalStrategyB);
            System.out.println();

            // Пошук змішаних стратегій графічним методом
            if (matrix.length == 2) {
                System.out.println("======================================== Графічний метод ========================================");
                buildGraphicInterpretation(matrix);
            }
            System.out.println();
        }

        // Ітераційний метод
        System.out.println("======================================== Ітераційний метод ========================================");
        MixedStrategy[] mixedStrategies = IterationMethod.findMixedStrategies(matrix, 0.001d, 0, 1000);
        for (MixedStrategy strategy : mixedStrategies) {
            strategy.print();
        }

        // Метод лінійного програмування
        System.out.println("======================================== Метод лінійного програмування ========================================");
        double[] pA = Simplex.getPsForA(matrix);
        for (int i = 0; i < pA.length; i++) {
            System.out.println("P[A" + i + "] = " + pA[i]);
        }
    }

    private static double[][] removeDuplicateRowsAndCols(double[][] input) {
        List<Integer> uniqueRows = new ArrayList<Integer>();
        for (int i = 0; i < input.length; i++) {
            boolean isUnique = true;
            for (Integer unique : uniqueRows){
                boolean isDifferent = false;
                for (int j = 0; j < input[i].length; j++){
                    if (input[i][j] != input[unique][j]){
                        isDifferent = true;
                    }
                }
                if (!isDifferent){
                    isUnique = false;
                    break;
                }
            }
            if (isUnique){
                uniqueRows.add(i);
            }
        }

        List<Integer> uniqueCols = new ArrayList<Integer>();
        for (int i = 0; i < input[0].length; i++) {
            boolean isUnique = true;
            for (Integer unique : uniqueCols){
                boolean isDifferent = false;
                for (int j = 0; j < input.length; j++){
                    if (input[j][i] != input[j][unique]){
                        isDifferent = true;
                    }
                }
                if (!isDifferent){
                    isUnique = false;
                    break;
                }
            }
            if (isUnique){
                uniqueCols.add(i);
            }
        }

        double[][] newMatrix = new double[uniqueRows.size()][uniqueCols.size()];

        for (int i = 0; i < uniqueRows.size(); i++){
            for (int j = 0; j < uniqueCols.size(); j++){
                newMatrix[i][j] = input[uniqueRows.get(i)][uniqueCols.get(j)];
            }
        }

        return newMatrix;
    }

    private static double[][] optimizeMatrix(double[][] matrix) {
        Set<Integer> inefficientRows = new HashSet<Integer>();
        main:
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix.length; j++){
                if (i != j){
                    boolean isEffective = false;
                    for (int k = 0; k < matrix[i].length; k++){
                        if (matrix[i][k] > matrix[j][k]){
                            isEffective = true;
                        }
                    }
                    if (!isEffective){
                        inefficientRows.add(i);
                        continue main;
                    }
                }
            }
        }

        Set<Integer> inefficientCols = new HashSet<Integer>();
        main:
        for (int i = 0; i < matrix[0].length; i++){
            for (int j = 0; j < matrix[0].length; j++){
                if (i != j){
                    boolean isEffective = false;
                    for (int k = 0; k < matrix.length; k++){
                        if (matrix[k][i] < matrix[k][j]){
                            isEffective = true;
                        }
                    }
                    if (!isEffective){
                        inefficientCols.add(i);
                        continue main;
                    }
                }
            }
        }

        double[][] newMatrix = new double[matrix.length - inefficientRows.size()][matrix[0].length - inefficientCols.size()];
        int rowsIterator = 0;
        for (int i = 0; i < matrix.length; i++){
            if (!inefficientRows.contains(i)){
                int colsIterator = 0;
                for (int j = 0; j < matrix[i].length; j++){
                    if (!inefficientCols.contains(j)){
                        newMatrix[rowsIterator][colsIterator] = matrix[i][j];
                        colsIterator++;
                    }
                }
                rowsIterator++;
            }
        }

        return newMatrix;
    }



    private static double[] getOptimalStrategyForA(double[][] matrix) {         // максимінна стратегія
        double[] rowMin = new double[matrix.length];                            // number of rows
        for (int i = 0; i < matrix.length; i++) {
            double tempRowMin = matrix[i][0];
            for (int j = 1; j < matrix[i].length; j++) {
                if (matrix[i][j] < tempRowMin) {
                    tempRowMin = matrix[i][j];
                }
            }
            rowMin[i] = tempRowMin;
        }
        double lowGamePrice = rowMin[0];
        int maxElementIndex = 0;
        for (int k = 1; k < rowMin.length; k++) {
            if (rowMin[k] > rowMin[maxElementIndex]) {
                maxElementIndex = k;
                lowGamePrice = rowMin[k];
            }
        }
        return new double[] {maxElementIndex, lowGamePrice};
    }

    private static double[] getOptimalStrategyForB(double[][] matrix) {         // мінісаксна стратегія
        double[] colMax = new double[matrix[0].length];                         // number of cols
        for (int j = 0; j < matrix[0].length; j++) {
            double tempColMax = matrix[0][j];
            for (int i = 1; i < matrix.length; i++) {
                if (matrix[i][j] > tempColMax) {
                    tempColMax = matrix[i][j];
                }
            }
            colMax[j] = tempColMax;
        }
        double maxGamePrice = colMax[0];
        int minElementIndex = 0;
        for (int k = 1; k < colMax.length; k++) {
            if (colMax[k] < colMax[minElementIndex]) {
                minElementIndex = k;
                maxGamePrice = colMax[k];
            }
        }
        return new double[] {minElementIndex, maxGamePrice};
    }

    private static void buildGraphicInterpretation(double[][] matrix) {
        GRALUtil frame = new GRALUtil(matrix);
        frame.setVisible(true);
    }

    private static double[][] getMatrix() {
        Scanner in = new Scanner(System.in);
        System.out.print("Number of rows: ");
        int rows = in.nextInt();
        System.out.print("Number of cols: ");
        int cols = in.nextInt();
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print("a[" + (i) + "][" + (j) + "] = ");
                double a = in.nextDouble();
                matrix[i][j] = a;
            }
        }
        return matrix;
    }

    protected static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < matrix[i].length; j++) {
                row.append(matrix[i][j] + "  ");
            }
            System.out.println(row.toString());
        }
    }
}
