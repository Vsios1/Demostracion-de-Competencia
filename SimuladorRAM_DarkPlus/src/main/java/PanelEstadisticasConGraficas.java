import org.jfree.chart.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class PanelEstadisticasConGraficas extends JPanel {

    private XYSeries serieCPU;
    private XYSeries serieRAM;
    private int tiempo = 0;

    // Data (updated by simulator)
    private volatile int totalProcesos = 10;
    private volatile int procesosEnEjecucion = 0;
    private volatile int memoriaTotal = 2048; // MB
    private volatile int memoriaUsada = 512;  // MB

    public PanelEstadisticasConGraficas() {
        setLayout(new GridLayout(2, 1, 10, 10));
        setBackground(EstiloDarkConsole.BG);

        // CPU chart
        serieCPU = new XYSeries("Uso CPU (%)");
        XYSeriesCollection datasetCPU = new XYSeriesCollection(serieCPU);
        JFreeChart graficoCPU = ChartFactory.createXYLineChart(
                "Rendimiento de CPU",
                "Tiempo (s)",
                "Uso (%)",
                datasetCPU
        );
        configurarEstilo(graficoCPU, EstiloDarkConsole.CPU);
        ChartPanel panelCPU = new ChartPanel(graficoCPU);
        panelCPU.setPreferredSize(new Dimension(600,200));
        panelCPU.setBackground(EstiloDarkConsole.PANEL);

        // RAM chart
        serieRAM = new XYSeries("Uso RAM (%)");
        XYSeriesCollection datasetRAM = new XYSeriesCollection(serieRAM);
        JFreeChart graficoRAM = ChartFactory.createXYLineChart(
                "Uso de Memoria RAM",
                "Tiempo (s)",
                "Uso (%)",
                datasetRAM
        );
        configurarEstilo(graficoRAM, EstiloDarkConsole.RAM);
        ChartPanel panelRAM = new ChartPanel(graficoRAM);
        panelRAM.setPreferredSize(new Dimension(600,200));
        panelRAM.setBackground(EstiloDarkConsole.PANEL);

        add(panelCPU);
        add(panelRAM);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                actualizarDatos();
            }
        }, 0, 1000);
    }

    private void configurarEstilo(JFreeChart grafico, Color colorLinea) {
        grafico.setBackgroundPaint(EstiloDarkConsole.BG);
        XYPlot plot = grafico.getXYPlot();
        plot.setBackgroundPaint(EstiloDarkConsole.PANEL);
        plot.setDomainGridlinePaint(Color.DARK_GRAY);
        plot.setRangeGridlinePaint(Color.DARK_GRAY);
        plot.getRenderer().setSeriesPaint(0, colorLinea);
        grafico.getTitle().setPaint(EstiloDarkConsole.TEXT);
        plot.getDomainAxis().setTickLabelPaint(EstiloDarkConsole.TEXT);
        plot.getRangeAxis().setTickLabelPaint(EstiloDarkConsole.TEXT);
    }

    private void actualizarDatos() {
        tiempo++;
        int usoCPU = (totalProcesos==0)?0:(procesosEnEjecucion * 100) / totalProcesos;
        int usoRAM = (memoriaUsada * 100) / Math.max(1, memoriaTotal);

        if (serieCPU.getItemCount() > 120) serieCPU.remove(0);
        if (serieRAM.getItemCount() > 120) serieRAM.remove(0);

        serieCPU.add(tiempo, usoCPU);
        serieRAM.add(tiempo, usoRAM);
    }

    // Method called by the simulator to update real values
    public void setDatosReales(int totalProc, int procEjec, int memTotal, int memUsada) {
        this.totalProcesos = totalProc;
        this.procesosEnEjecucion = procEjec;
        this.memoriaTotal = memTotal;
        this.memoriaUsada = memUsada;
    }
}
