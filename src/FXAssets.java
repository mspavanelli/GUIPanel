import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import static java.lang.String.*;

public class FXAssets {

    private static final int GRAFICO_LARGURA = 1000;
    private static final int GRAFICO_ALTURA = 500;
    private static Pesquisa pesquisa;

    public static void initAndShowGUI(Pesquisa p) {
        pesquisa = p;
        String title = format("%s em %s (Estação: %s)", p.getMedida(), p.getPais(), p.getEstacao());

        JFrame frame = new JFrame(title);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setSize(1020, 530);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    public static void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    public static Scene createScene() {
        Group root = new Group();
        Scene scene = new Scene(root, Color.WHITE);
        try {
            root.getChildren().add(createChart());
        }
        catch (Exception exception) {
            System.out.println("Exception on createScene()");
        }

        return (scene);
    }

    public static LineChart createChart() {
        LineChart<String, Number> graficoLinha = new LineChart<>(new CategoryAxis(), new NumberAxis());

        /* Carregar os dados */
        // período no eixo x
        ArrayList<String> valoresEixoX = new ArrayList<>();
        for ( int i = pesquisa.getDataInicial().getYear(); i <= pesquisa.getDataFinal().getYear(); i++ )
            valoresEixoX.add(Integer.toString(i+1900));

        // valores hipotéticos
        Random r = new Random();
        int [] valores = new int[valoresEixoX.size()];
        for ( int i = 0; i < valoresEixoX.size(); i++ )
            valores[i] = r.nextInt(50) + 30;

        XYChart.Series medida = createLine(valoresEixoX, valores);
        medida.setName(pesquisa.getMedida());

        int [] valores2 = new int[valoresEixoX.size()];
        for ( int i = 0; i < valoresEixoX.size(); i++ )
            valores2[i] = 56;

        XYChart.Series medida2 = createLine(valoresEixoX, valores2);
        medida2.setName("Média");

        graficoLinha.getData().addAll(medida, medida2);
        graficoLinha.setPrefSize(GRAFICO_LARGURA, GRAFICO_ALTURA);

        return graficoLinha;
    }

    public static XYChart.Series createLine(ArrayList<String> eixoX, int [] valores) {
        XYChart.Series line = new XYChart.Series();
        Iterator<String> x = eixoX.iterator();
        int i = 0;
        try {
            while (x.hasNext())
                line.getData().add(new XYChart.Data(x.next(), valores[i++]));
        }
        catch (IndexOutOfBoundsException ioob) {
            System.out.println(ioob.getClass().getName());
        }
        catch (Exception e) {
            System.out.println(e.getClass().getName());
        }


        return line;
    }
}