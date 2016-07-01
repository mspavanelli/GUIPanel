import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mspavanelli on 30/06/16.
 */
public class Pesquisa {
    private String pais;
    private String estacao;
    private String medida;
    private String agrupamento;
    private Date dataInicial;
    private Date dataFinal;
    private boolean media;
    private boolean regressao;

    public Pesquisa(String pais, String estacao, String medida, String agrupamento, Date dataInicial, Date dataFinal, boolean media, boolean regressao) {
        this.pais = pais;
        this.estacao = estacao;
        this.medida = medida;
        this.agrupamento = agrupamento;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.media = media;
        this.regressao = regressao;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstacao() {
        return estacao;
    }

    public void setEstacao(String estacao) {
        this.estacao = estacao;
    }

    public String getMedida() {
        return medida;
    }

    public String getMedida(boolean simplificado) {
        Map<String, String> medida = new HashMap<>();
        medida.put("Temperatura Média", "TEMP");
        medida.put("Ponto de Orvalho", "DEWP");
        medida.put("Pressão no mar", "SLP");
        medida.put("Pressão na estação", "STP");
        medida.put("Visibilidade", "VISIB");
        medida.put("Velocidade do Vento", "WDSP");
        medida.put("Máxima velocidade do vento sustentada", "MXSPD");
        medida.put("Máxima rajada de vento", "GUST");
        medida.put("Temperatura Máxima", "MAX");
        medida.put("Temperatura Mínima", "MIN");
        medida.put("Precipitação", "PRCP");
        medida.put("Profundidade da neve", "SNDP");

        return medida.get(getMedida());
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getAgrupamento() {
        return agrupamento;
    }

    public void setAgrupamento(String agrupamento) {
        this.agrupamento = agrupamento;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public boolean getMedia() {
        return media;
    }

    public boolean getRegressao() {
        return regressao;
    }

    @Override
    public String toString() {
        return "Pesquisa{" +
                "pais='" + pais + '\'' +
                ", estacao='" + estacao + '\'' +
                ", medida='" + medida + '\'' +
                ", agrupamento='" + agrupamento + '\'' +
                ", dataInicial=" + dataInicial +
                ", dataFinal=" + dataFinal +
                ", media=" + media +
                ", regressao=" + regressao +
                '}';
    }
}
