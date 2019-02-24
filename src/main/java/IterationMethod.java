import java.util.ArrayList;
import java.util.Arrays;

public class IterationMethod {

    public static MixedStrategy[] findMixedStrategies(double[][] matrix, double e, int initialA, int maxIterationsNumber) {
        double vPrev;
        double vNew;

        ArrayList<Iteration> iterations = new ArrayList<Iteration>();
        Iteration firstIteration = new Iteration();
        firstIteration.setN(1);
        firstIteration.setI(initialA);
        setBs(null, firstIteration, matrix);
        setJ(firstIteration);
        setAs(null, firstIteration, matrix);
        setVMin(firstIteration);
        setVMax(firstIteration);
        setV(firstIteration);
        iterations.add(firstIteration);

        int counter = 0;
        do {
            counter++;
            vPrev = iterations.get(counter-1).getV();
            Iteration iteration = new Iteration();
            iteration.setN(counter+1);
            setI(iteration, iterations.get(counter-1).getAs());
            setBs(iterations.get(counter-1), iteration, matrix);
            setJ(iteration);
            setAs(iterations.get(counter-1), iteration, matrix);
            setVMin(iteration);
            setVMax(iteration);
            setV(iteration);
            iterations.add(iteration);
            vNew = iteration.getV();
        } while ((Math.abs(vPrev - vNew) > e) && counter <= maxIterationsNumber);

        for (Iteration iteration : iterations) {
            System.out.println(iteration.toString());
        }

        System.out.println("\nЦіна гри = " + vNew);
        System.out.println();

        int[] paf = new int[matrix.length];
        int[] pbf = new int[matrix[0].length];
        for (Iteration iteration : iterations) {
            paf[iteration.getI()] += 1;
            pbf[iteration.getJ()] += 1;
        }

        double[] pa = new double[paf.length];
        for (int i = 0; i < pa.length; i++) {
            pa[i] = (double)paf[i]/(double)counter;
        }
        double[] pb = new double[pbf.length];
        for (int i = 0; i < pb.length; i++) {
            pb[i] = (double)pbf[i]/(double)counter;
        }

        return new MixedStrategy[] {new MixedStrategy('A', pa), new MixedStrategy('B', pb)};
    }

    private static void setV(Iteration curIter) {
        curIter.setV((curIter.getvMax()+curIter.getvMin())/2d);
    }

    private static void setVMax(Iteration curIter) {
        double vMax = curIter.getAs()[0];
        for (int i = 1; i < curIter.getAs().length; i++) {
            if (curIter.getAs()[i] > vMax) {
                vMax = curIter.getAs()[i];
            }
        }
        curIter.setvMax(vMax/(double)curIter.getN());
    }

    private static void setVMin(Iteration curIter) {
        double vMin = curIter.getBs()[0];
        for (int i = 1; i < curIter.getBs().length; i++) {
            if (curIter.getBs()[i] < vMin) {
                vMin = curIter.getBs()[i];
            }
        }
        curIter.setvMin(vMin/(double)curIter.getN());
    }

    private static void setI(Iteration iteration, double[] as) {
        int maxIndex = 0;
        for (int i = 1; i < as.length; i++) {
            if (as[i] > as[maxIndex]) {
                maxIndex = i;
            }
        }
        iteration.setI(maxIndex);
    }

    private static void setJ(Iteration curIter) {
        int minIndex = 0;
        for (int j = 1; j < curIter.getBs().length; j++) {
            if (curIter.getBs()[j] < curIter.getBs()[minIndex]) {
                minIndex = j;
            }
        }
        curIter.setJ(minIndex);
    }

    private static void setBs(Iteration prevIter, Iteration curIter, double[][] matrix) {
        double[] bs = new double[matrix[0].length];
        for (int i = 0; i < bs.length; i++) {
            bs[i] = matrix[curIter.getI()][i];
        }
        if (prevIter != null) {
            for (int i = 0; i < bs.length; i++) {
                bs[i] += prevIter.getBs()[i];
            }
        }
        curIter.setBs(bs);
    }

    private static void setAs(Iteration prevIter, Iteration curIter, double[][] matrix) {
        double[] as = new double[matrix.length];
        for (int i = 0; i < as.length; i++) {
            as[i] = matrix[i][curIter.getJ()];
        }
        if (prevIter != null) {
            for (int i = 0; i < as.length; i++) {
                as[i] += prevIter.getAs()[i];
            }
        }
        curIter.setAs(as);
    }
}

class Iteration {

    int n;
    int i;
    double[] bs;
    int j;
    double [] as;
    double vMin;
    double vMax;
    double v;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public double[] getBs() {
        return bs;
    }

    public void setBs(double[] bs) {
        this.bs = bs;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public double[] getAs() {
        return as;
    }

    public void setAs(double[] as) {
        this.as = as;
    }

    public double getvMin() {
        return vMin;
    }

    public void setvMin(double vMin) {
        this.vMin = vMin;
    }

    public double getvMax() {
        return vMax;
    }

    public void setvMax(double vMax) {
        this.vMax = vMax;
    }

    public double getV() {
        return v;
    }

    public void setV(double v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "Iteration{" +
                "n=" + n +
                ", i=" + i +
                ", bs=" + Arrays.toString(bs) +
                ", j=" + j +
                ", as=" + Arrays.toString(as) +
                ", vMin=" + vMin +
                ", vMax=" + vMax +
                ", v=" + v +
                '}';
    }
}

class MixedStrategy {

    char player;
    double[] p;

    public MixedStrategy(char player, double[] p) {
        this.player = player;
        this.p = p;
    }

    public char getPlayer() {
        return player;
    }

    public void setPlayer(char player) {
        this.player = player;
    }

    public double[] getP() {
        return p;
    }

    public void setP(double[] p) {
        this.p = p;
    }

    public void print(){
        for (int i = 0; i < this.p.length; i++) {
            System.out.println("Частота для стратегії " + player + i + " = " + this.p[i]);
        }
        System.out.println();
    }
}
