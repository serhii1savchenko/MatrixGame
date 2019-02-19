import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Insets2D;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

public class GRALUtil extends JFrame {

    public GRALUtil(double[][] matrix) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        ArrayList<DataTable> dataTables = new ArrayList<DataTable>();
        ArrayList<LineRenderer> lineRenderers = new ArrayList<LineRenderer>();
        ArrayList<Color> colors = new ArrayList<Color>();
        float blue = 0.3f;

        for (int k = 0; k < matrix[0].length; k++) {
            DataTable dataTable = new DataTable(Double.class, Double.class);
            dataTable.add(0d, matrix[0][k]);
            dataTable.add(1d, matrix[1][k]);
            //dataTable.setName("Strategy " + k);
            dataTables.add(dataTable);
            lineRenderers.add(new DefaultLineRenderer2D());
            colors.add(new Color(0.0f, 0.0f, blue));
            blue += 0.2f;
        }

        XYPlot plot = new XYPlot(dataTables.toArray(new DataTable[dataTables.size()]));
        double insetsTop = 20.0,
                insetsLeft = 60.0,
                insetsBottom = 60.0,
                insetsRight = 40.0;
        plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));
        //plot.setLegendVisible(true);
        getContentPane().add(new InteractivePanel(plot));

        for (int k = 0; k < dataTables.size(); k++) {
            plot.setLineRenderers(dataTables.get(k), lineRenderers.get(k));
        }

        for (int k = 0; k < dataTables.size(); k++) {
            plot.getPointRenderers(dataTables.get(k)).get(0).setColor(colors.get(k));
            plot.getLineRenderers(dataTables.get(k)).get(0).setColor(colors.get(k));
        }
    }

}
