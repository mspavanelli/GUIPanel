import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.io.FileReader;
import java.util.HashSet;

import com.opencsv.*;

/**
 * Created by mspavanelli on 29/06/16.
 */
public class Dashboard extends JFrame {

    private static int WIDTH = 800;
    private static int HEIGHT = 600;
    private JPanel rootPanel;
    private JComboBox comboBoxPais;
    private JComboBox comboBoxEstacao;
    private JSlider anoInicial;
    private JComboBox comboBoxMesInicial;
    private JComboBox comboBoxDiaInicial;
    private JSlider sliderAnoFinal;
    private JComboBox comboBoxMesFinal;
    private JComboBox comboBoxDiaFinal;
    private JComboBox comboBoxAgrupamento;
    private JButton btnVisualizar;
    private JComboBox comboBoxMedida;
    private JLabel lblAnoInicial;
    private JLabel lblAnoFinal;
    private JButton btnGerarComando;
    private JCheckBox checkBoxMedia;
    private JCheckBox checkBoxRegresssao;
    private JTextArea txtComandos;
    private JTextField txtCaminhoEntrada;
    private JTextField txtCaminhoSaida;

    public Dashboard() {
        super("Dashboard");
        setWindow();
        setData();
        createListeners();
    }

    public static <T> void  dadosParaComboBox(T [] dados, JComboBox comboBox) {
        for (T dado : dados)
            comboBox.addItem(dado.toString());
    }

    public void setWindow() {
        setContentPane(rootPanel);
        pack();
        setSize(WIDTH, HEIGHT);
        setMaximumSize(new Dimension(1280, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width/2, dim.height / 2 - this.getSize().height / 2);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void setData() {
        String [] paises = readCountries();
        String [] estacao = readStationNumbers();
        String [] medidas = {
                "Temperatura Média",
                "Ponto de Orvalho",
                "Pressão no mar",
                "Pressão na estação",
                "Visibilidade",
                "Velocidade do Vento",
                "Máxima velocidade do vento sustentada",
                "Máxima rajada de vento",
                "Temperatura Máxima",
                "Temperatura Mínima",
                "Precipitação",
                "Profundidade da Neve"
        };

        String [] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        Integer [] dias = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
        String [] agrupamento = {"Por Ano", "Por Mês", "Por Semana", "Por Dia"};

        dadosParaComboBox(paises, comboBoxPais);
        dadosParaComboBox(estacao, comboBoxEstacao);
        dadosParaComboBox(medidas, comboBoxMedida);

        dadosParaComboBox(meses, comboBoxMesInicial);
        dadosParaComboBox(meses, comboBoxMesFinal);

        dadosParaComboBox(dias, comboBoxDiaInicial);
        dadosParaComboBox(dias, comboBoxDiaFinal);

        dadosParaComboBox(agrupamento, comboBoxAgrupamento);
    }

    public String[] readStationNumbers() {
        ArrayList<String> estacoesLista = new ArrayList<>();
        CSVReader reader;
        String [] nextLine;
        try {
            reader = new CSVReader(new FileReader("resources/isd-history.csv"));
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                    estacoesLista.add(nextLine[0] + nextLine[1]);
            }
        }
        catch (IOException io) {
            System.out.println("Erro de leitura");
        }
        catch (Exception e ) {
            System.out.println("Exception on CSVReader");
        }

//        String [] estacoesArray = new String[estacoesLista.size()];
        return estacoesLista.toArray(new String[estacoesLista.size()]);

    }

    public String[] readCountries() {
        HashSet<String> paises = new HashSet<>();
        CSVReader reader;
        String [] nextLine;
        try {
            reader = new CSVReader(new FileReader("resources/isd-history.csv"));
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                paises.add(nextLine[3]);
            }
        }
        catch (IOException io) {
            System.out.println("Erro de leitura");
        }
        catch (Exception e ) {
            System.out.println("Exception on CSVReader");
        }

        return paises.toArray(new String[paises.size()]);
    }

    public void createListeners() {
        ListenForSlider lForSlider = new ListenForSlider();
        this.anoInicial.addChangeListener(lForSlider);
        this.sliderAnoFinal.addChangeListener(lForSlider);
        this.sliderAnoFinal.setValue(this.sliderAnoFinal.getMaximum());

        btnVisualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Carregando dados...");

                Pesquisa pesquisa = carregaPesquisa();

                // FIX-IT: erro na hora de gerar o gráfico na primeira vez
                try {
                    System.out.println(pesquisa);
                    FXAssets.initAndShowGUI(pesquisa);   // gera com problema
//                    FXAssets.initAndShowGUI(pesquisa);   // gera corretamente
                }
                catch (Exception excetion) {
                    System.out.println("Exception on first call");
                    JOptionPane.showMessageDialog(null, "Problema ao carregar gráfico. Por favor, reexecute o programa.",
                            "Falha", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnGerarComando.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pesquisa pesquisa = carregaPesquisa();
                StringBuilder comando = new StringBuilder("bin/hadoop jar <caminho do jar> br.com.mapreduce.Main ");
                if ( pesquisa.getMedia() ) {
                    comando.append(geraComando(pesquisa, "mean"));
//                    txtComandos.setText(geraComando(pesquisa, "stddev"));
                }
                if ( pesquisa.getRegressao())
                    comando.append(geraComando(pesquisa, "least"));
                txtComandos.setText(comando.toString());
            }
        });
    }

    public String geraComando(Pesquisa p, String tipo) {
        SimpleDateFormat formatadorDeDatas = new SimpleDateFormat("yyyyMMdd");
        String entrada = txtCaminhoEntrada.getText();
        String saida = txtCaminhoSaida.getText();
        if ( tipo.equals("mean") )
            return String.format("mean %s %s %s %s %s %s", entrada, saida, p.getEstacao(), formatadorDeDatas.format(getInitialDate()), formatadorDeDatas.format(getFinalDate()), p.getMedida(true));
        if ( tipo.equals("stddev") )
            return String.format("stddev %s %s %s %s %s %s", entrada, saida, p.getEstacao(), formatadorDeDatas.format(getInitialDate()), formatadorDeDatas.format(getFinalDate()), p.getMedida(true));
        if ( tipo.equals("least") )
            return String.format("least %s %s %s %s %s %s %s", entrada, saida, p.getEstacao(), formatadorDeDatas.format(getInitialDate()), formatadorDeDatas.format(getFinalDate()), p.getMedida(true), p.getAgrupamento());
        return null;
    }

    public Pesquisa carregaPesquisa() {
        String pais = this.comboBoxPais.getSelectedItem().toString();
        String estacao = this.comboBoxEstacao.getSelectedItem().toString();
        String medida = this.comboBoxMedida.getSelectedItem().toString();
        String agrupamento = this.comboBoxAgrupamento.getSelectedItem().toString();

        return new Pesquisa(pais, estacao, medida, agrupamento, getInitialDate(), getFinalDate(), checkBoxMedia.isSelected(), checkBoxRegresssao.isSelected());
    }

    public Date getInitialDate() {
        Calendar calendar = Calendar.getInstance();

        int mesInicial = comboBoxMesInicial.getSelectedIndex();
        int diaInicial = Integer.parseInt(comboBoxDiaInicial.getSelectedItem().toString());

        calendar.set(anoInicial.getValue(), mesInicial, diaInicial);
        return calendar.getTime();
    }

    public Date getFinalDate() {
        Calendar calendar = Calendar.getInstance();
        int mesFinal = comboBoxMesFinal.getSelectedIndex();
        int diaFinal = Integer.parseInt(comboBoxDiaFinal.getSelectedItem().toString());

        calendar.set(sliderAnoFinal.getValue(), mesFinal, diaFinal);
        return calendar.getTime();
    }

    private class ListenForSlider implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            if ( e.getSource() == anoInicial) {
                lblAnoInicial.setText(Integer.toString(anoInicial.getValue()));
                if (anoInicial.getValue() >= sliderAnoFinal.getValue()) {
                    lblAnoFinal.setText(Integer.toString(anoInicial.getValue()));
                    sliderAnoFinal.setValue(anoInicial.getValue());
                }
            }

            if ( e.getSource() == sliderAnoFinal ) {
                lblAnoFinal.setText(Integer.toString(sliderAnoFinal.getValue()));
                if (sliderAnoFinal.getValue() <= anoInicial.getValue()) {
                    lblAnoInicial.setText(Integer.toString(sliderAnoFinal.getValue()));
                    anoInicial.setValue(sliderAnoFinal.getValue());
                }
            }
        }
    }
}
