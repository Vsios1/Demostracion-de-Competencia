import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * SimuladorCompleto.java
 * Simulador de planificación de CPU y visualización de memoria con múltiples algoritmos,
 * diagrama de Gantt animado, agregar procesos, guardar/cargar CSV y exportar resultados.
 *
 * Compilar:
 *   javac SimuladorCompleto.java
 * Ejecutar:
 *   java SimuladorCompleto
 */
public class SimuladorCompleto extends JFrame {

    // Modelos y tablas
    private final DefaultTableModel modeloProcesos;
    private final DefaultTableModel modeloResultados;
    private final JTable tablaProcesos, tablaResultados;

    // Controles
    private final JComboBox<String> comboAlg;
    private final JTextField txtQuantum;
    private final JButton btnPreparar, btnAgregar, btnAplicar, btnIniciar, btnPausar, btnPaso, btnReset;
    private final JButton btnGuardarCSV, btnCargarCSV, btnExportResultados;
    private final JTextField txtPromEspera, txtPromRetorno, txtPromRespuesta;

    // Panels visuales
    private final PanelMemoria panelMemoria;
    private final PanelGantt panelGantt;

    // Datos
    private final List<Proceso> procesos = new ArrayList<>();
    private final List<GanttEntry> secuenciaGantt = new ArrayList<>();

    // Animación (javax.swing.Timer)
    private javax.swing.Timer timer;
    private int ganttIndex = 0;
    private int ganttTick = 0;
    private boolean animando = false;
    private int tickMs = 600; // velocidad en ms por unidad (ajustable)

    public SimuladorCompleto() {
        setTitle("Simulador Completo de Planificación y Memoria");
        setSize(1100, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializar componentes de UI
        comboAlg = new JComboBox<>(new String[]{"FIFO", "SJF", "Prioridad", "Round Robin"});
        txtQuantum = new JTextField(4);
        btnPreparar = new JButton("Preparar Ejemplo");
        btnAgregar = new JButton("Agregar Proceso");
        btnAplicar = new JButton("Aplicar Algoritmo");
        btnIniciar = new JButton("Iniciar");
        btnPausar = new JButton("Pausar");
        btnPaso = new JButton("Paso");
        btnReset = new JButton("Reset");
        btnGuardarCSV = new JButton("Guardar CSV");
        btnCargarCSV = new JButton("Cargar CSV");
        btnExportResultados = new JButton("Exportar Resultados CSV");

        modeloProcesos = new DefaultTableModel(new String[]{"Nombre", "Ráfaga", "Llegada", "Prioridad", "Estado"}, 0);
        modeloResultados = new DefaultTableModel(new String[]{"Nombre", "T. Espera", "T. Retorno", "T. Respuesta"}, 0);
        tablaProcesos = new JTable(modeloProcesos);
        tablaResultados = new JTable(modeloResultados);

        panelMemoria = new PanelMemoria();
        panelGantt = new PanelGantt();

        txtPromEspera = new JTextField(6);
        txtPromEspera.setEditable(false);
        txtPromRetorno = new JTextField(6);
        txtPromRetorno.setEditable(false);
        txtPromRespuesta = new JTextField(6);
        txtPromRespuesta.setEditable(false);

        initUI();
        crearTimer();
    }

    private void initUI() {
        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Algoritmo:"));
        top.add(comboAlg);
        top.add(new JLabel("Quantum:"));
        top.add(txtQuantum);
        top.add(btnPreparar);
        top.add(btnAgregar);
        top.add(btnAplicar);
        top.add(btnIniciar);
        top.add(btnPausar);
        top.add(btnPaso);
        top.add(btnReset);
        top.add(btnCargarCSV);
        top.add(btnGuardarCSV);
        top.add(btnExportResultados);
        add(top, BorderLayout.NORTH);

        // Center split pane
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.45);

        // Top part of split: tablas
        JPanel panelTablas = new JPanel(new BorderLayout());
        JScrollPane spProcesos = new JScrollPane(tablaProcesos);
        spProcesos.setBorder(BorderFactory.createTitledBorder("Procesos"));
        JScrollPane spResultados = new JScrollPane(tablaResultados);
        spResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        panelTablas.add(spProcesos, BorderLayout.CENTER);
        panelTablas.add(spResultados, BorderLayout.SOUTH);
        splitVertical.setTopComponent(panelTablas);

        // Bottom part: Gantt + Memoria
        JPanel abajo = new JPanel(new BorderLayout());
        panelGantt.setPreferredSize(new Dimension(1000, 260));
        panelGantt.setBorder(BorderFactory.createTitledBorder("Diagrama de Gantt"));
        panelMemoria.setPreferredSize(new Dimension(1000, 160));
        panelMemoria.setBorder(BorderFactory.createTitledBorder("Memoria RAM (bloques de procesos)"));
        abajo.add(panelGantt, BorderLayout.CENTER);
        abajo.add(panelMemoria, BorderLayout.SOUTH);
        splitVertical.setBottomComponent(abajo);

        add(splitVertical, BorderLayout.CENTER);

        // Bottom averages
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Promedio Espera:"));
        bottom.add(txtPromEspera);
        bottom.add(new JLabel("Promedio Retorno:"));
        bottom.add(txtPromRetorno);
        bottom.add(new JLabel("Promedio Respuesta:"));
        bottom.add(txtPromRespuesta);
        add(bottom, BorderLayout.SOUTH);

        // Eventos
        btnPreparar.addActionListener(e -> prepararEjemplo());
        btnAgregar.addActionListener(e -> dialogoAgregarProceso());
        btnAplicar.addActionListener(e -> generarSecuenciaSegunAlgoritmo());
        btnIniciar.addActionListener(e -> iniciarAnimacion());
        btnPausar.addActionListener(e -> pausarAnimacion());
        btnPaso.addActionListener(e -> pasoAnimacion());
        btnReset.addActionListener(e -> resetSimulacion());
        btnGuardarCSV.addActionListener(e -> guardarCSV());
        btnCargarCSV.addActionListener(e -> cargarCSV());
        btnExportResultados.addActionListener(e -> exportarResultadosCSV());
    }

    private void crearTimer() {
        timer = new javax.swing.Timer(tickMs, e -> tickGantt());
        timer.setInitialDelay(0);
    }

    // ---------- Funcionalidades ----------
    private void prepararEjemplo() {
        procesos.clear();
        modeloProcesos.setRowCount(0);

        procesos.add(new Proceso("Word", 4, 1, 1, Color.decode("#8dd3c7")));
        procesos.add(new Proceso("Zoom", 2, 2, 3, Color.decode("#ffffb3")));
        procesos.add(new Proceso("Chrome", 1, 5, 2, Color.decode("#bebada")));
        procesos.add(new Proceso("Paint", 3, 8, 4, Color.decode("#fb8072")));

        for (Proceso p : procesos) {
            modeloProcesos.addRow(new Object[]{p.nombre, p.rafagaOriginal, p.llegada, p.prioridad, "Preparado"});
        }

        panelMemoria.setProcesos(procesos);
        panelGantt.setSecuencia(new ArrayList<>());
        modeloResultados.setRowCount(0);
        resetPromedios();
    }

    private void dialogoAgregarProceso() {
        JTextField nombre = new JTextField();
        JTextField rafaga = new JTextField();
        JTextField llegada = new JTextField();
        JTextField prioridad = new JTextField();

        Object[] message = {
                "Nombre:", nombre,
                "Ráfaga (int):", rafaga,
                "Llegada (int):", llegada,
                "Prioridad (int, menor=mayor prioridad):", prioridad
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Proceso", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String n = nombre.getText().trim();
                int r = Integer.parseInt(rafaga.getText().trim());
                int l = Integer.parseInt(llegada.getText().trim());
                int p = Integer.parseInt(prioridad.getText().trim());
                Color c = randomColorForName(n);
                Proceso nuevo = new Proceso(n, r, l, p, c);
                procesos.add(nuevo);
                modeloProcesos.addRow(new Object[]{n, r, l, p, "Preparado"});
                panelMemoria.setProcesos(procesos);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Entrada inválida. Revisa los valores.");
            }
        }
    }

    private Color randomColorForName(String name) {
        int h = Math.abs(name.hashCode());
        int r = 100 + (h % 155);
        int g = 100 + ((h / 3) % 155);
        int b = 100 + ((h / 7) % 155);
        return new Color(r, g, b);
    }

    // Genera la secuencia de Gantt según algoritmo seleccionado
    private void generarSecuenciaSegunAlgoritmo() {
        if (procesos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay procesos. Agrega o prepara ejemplo.");
            return;
        }
        String alg = (String) comboAlg.getSelectedItem();
        modeloResultados.setRowCount(0);
        secuenciaGantt.clear();

        switch (alg) {
            case "FIFO" -> aplicarFIFO();
            case "SJF" -> aplicarSJF();
            case "Prioridad" -> aplicarPrioridad();
            case "Round Robin" -> aplicarRR();
            default -> aplicarFIFO();
        }

        // actualizar visuales
        panelGantt.setSecuencia(secuenciaGantt);
        panelMemoria.setProcesos(procesos);
        calcularYMostrarTiempos(); // también poblará tabla resultados
        ganttIndex = 0;
        ganttTick = 0;
        panelGantt.setCursorIndex(0);
        panelGantt.repaint();
    }

    // ---------- Algoritmos ----------
    private List<Proceso> cloneProcesosOriginal() {
        List<Proceso> copy = new ArrayList<>();
        for (Proceso p : procesos) copy.add(new Proceso(p));
        return copy;
    }

    private void aplicarFIFO() {
        List<Proceso> lista = cloneProcesosOriginal();
        lista.sort(Comparator.comparingInt(p -> p.llegada));
        int tiempo = 0;
        for (Proceso p : lista) {
            if (tiempo < p.llegada) tiempo = p.llegada;
            secuenciaGantt.add(new GanttEntry(p, p.rafagaOriginal));
            tiempo += p.rafagaOriginal;
        }
    }

    private void aplicarSJF() {
        List<Proceso> lista = cloneProcesosOriginal();
        int tiempo = 0;
        List<Proceso> pendientes = new ArrayList<>(lista);
        pendientes.sort(Comparator.comparingInt(p -> p.llegada));
        List<Proceso> disponibles = new ArrayList<>();

        while (!pendientes.isEmpty() || !disponibles.isEmpty()) {
            while (!pendientes.isEmpty() && pendientes.get(0).llegada <= tiempo) {
                disponibles.add(pendientes.remove(0));
            }
            if (disponibles.isEmpty()) {
                tiempo = pendientes.get(0).llegada;
                continue;
            }
            disponibles.sort(Comparator.comparingInt(p -> p.rafagaOriginal));
            Proceso sel = disponibles.remove(0);
            secuenciaGantt.add(new GanttEntry(sel, sel.rafagaOriginal));
            tiempo += sel.rafagaOriginal;
        }
    }

    private void aplicarPrioridad() {
        List<Proceso> lista = cloneProcesosOriginal();
        int tiempo = 0;
        List<Proceso> pendientes = new ArrayList<>(lista);
        pendientes.sort(Comparator.comparingInt(p -> p.llegada));
        List<Proceso> disponibles = new ArrayList<>();

        while (!pendientes.isEmpty() || !disponibles.isEmpty()) {
            while (!pendientes.isEmpty() && pendientes.get(0).llegada <= tiempo) {
                disponibles.add(pendientes.remove(0));
            }
            if (disponibles.isEmpty()) {
                tiempo = pendientes.get(0).llegada;
                continue;
            }
            disponibles.sort(Comparator.comparingInt(p -> p.prioridad));
            Proceso sel = disponibles.remove(0);
            secuenciaGantt.add(new GanttEntry(sel, sel.rafagaOriginal));
            tiempo += sel.rafagaOriginal;
        }
    }

    private void aplicarRR() {
        int quantum;
        try {
            quantum = Integer.parseInt(txtQuantum.getText().trim());
            if (quantum <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ingresa un quantum entero > 0 para Round Robin.");
            return;
        }

        List<Proceso> lista = cloneProcesosOriginal();
        lista.sort(Comparator.comparingInt(p -> p.llegada));
        Queue<Proceso> cola = new LinkedList<>();
        int tiempo = 0;
        int idxPendientes = 0;
        while (idxPendientes < lista.size() || !cola.isEmpty()) {
            while (idxPendientes < lista.size() && lista.get(idxPendientes).llegada <= tiempo) {
                cola.add(lista.get(idxPendientes++));
            }
            if (cola.isEmpty()) {
                tiempo = lista.get(idxPendientes).llegada;
                continue;
            }
            Proceso p = cola.poll();
            int ejec = Math.min(p.rafagaOriginal, quantum);
            secuenciaGantt.add(new GanttEntry(new Proceso(p), ejec));
            tiempo += ejec;
            p.rafagaOriginal -= ejec;
            if (p.rafagaOriginal > 0) {
                while (idxPendientes < lista.size() && lista.get(idxPendientes).llegada <= tiempo) {
                    cola.add(lista.get(idxPendientes++));
                }
                cola.add(p);
            }
        }
    }

    // ---------- Cálculo de tiempos a partir de secuenciaGantt ----------
    private void calcularYMostrarTiempos() {
        Map<String, Integer> tiempoFin = new HashMap<>();
        Map<String, Integer> tiempoRespuesta = new HashMap<>();
        Map<String, Integer> sumaEjecutada = new HashMap<>();
        Map<String, Integer> llegadaMap = new HashMap<>();
        Map<String, Integer> rafagaOriginalMap = new HashMap<>();

        for (Proceso p : procesos) {
            llegadaMap.put(p.nombre, p.llegada);
            rafagaOriginalMap.put(p.nombre, p.rafagaOriginal);
            sumaEjecutada.put(p.nombre, 0);
        }

        int tiempo = 0;
        for (GanttEntry e : secuenciaGantt) {
            Proceso p = e.proceso;
            String name = p.nombre;
            if (!tiempoRespuesta.containsKey(name)) {
                int resp = Math.max(0, tiempo - llegadaMap.get(name));
                tiempoRespuesta.put(name, resp);
            }
            sumaEjecutada.put(name, sumaEjecutada.getOrDefault(name, 0) + e.duracion);
            tiempo += e.duracion;
            int totalneeded = rafagaOriginalMap.get(name);
            if (sumaEjecutada.get(name) >= totalneeded) {
                tiempoFin.put(name, tiempo);
            }
        }

        modeloResultados.setRowCount(0);
        double totalEspera = 0, totalRetorno = 0, totalRespuesta = 0;
        Set<String> nombres = new LinkedHashSet<>();
        for (Proceso p : procesos) nombres.add(p.nombre);

        for (String name : nombres) {
            int l = llegadaMap.get(name);
            int ret = tiempoFin.getOrDefault(name, 0) - l;
            int resp = tiempoRespuesta.getOrDefault(name, Math.max(0, 0 - l));
            int raf = rafagaOriginalMap.get(name);
            int espera = ret - raf;
            modeloResultados.addRow(new Object[]{name, espera, ret, resp});
            totalEspera += espera;
            totalRetorno += ret;
            totalRespuesta += resp;
        }

        int n = nombres.size();
        if (n > 0) {
            txtPromEspera.setText(String.format("%.2f", totalEspera / n));
            txtPromRetorno.setText(String.format("%.2f", totalRetorno / n));
            txtPromRespuesta.setText(String.format("%.2f", totalRespuesta / n));
        }
    }

    // ---------- Animación y control Gantt ----------
    private void iniciarAnimacion() {
        if (secuenciaGantt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Genera la secuencia aplicando un algoritmo primero.");
            return;
        }
        if (!animando) {
            animando = true;
            timer.start();
        }
    }

    private void pausarAnimacion() {
        animando = false;
        if (timer != null) timer.stop();
    }

    private void pasoAnimacion() {
        if (secuenciaGantt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Genera la secuencia aplicando un algoritmo primero.");
            return;
        }
        tickGantt();
    }

    private void tickGantt() {
        if (ganttIndex >= secuenciaGantt.size()) {
            pausarAnimacion();
            return;
        }
        panelGantt.setCursorIndex(ganttIndex);
        panelGantt.repaint();

        ganttTick++;
        GanttEntry current = secuenciaGantt.get(ganttIndex);
        if (ganttTick >= current.duracion) {
            ganttIndex++;
            ganttTick = 0;
        }
    }

    private void resetSimulacion() {
        pausarAnimacion();
        secuenciaGantt.clear();
        modeloResultados.setRowCount(0);
        ganttIndex = 0;
        ganttTick = 0;
        panelGantt.setSecuencia(new ArrayList<>());
        panelMemoria.setProcesos(procesos);
        resetPromedios();
    }

    private void resetPromedios() {
        txtPromEspera.setText("");
        txtPromRetorno.setText("");
        txtPromRespuesta.setText("");
    }

    // ---------- CSV: Guardar y Cargar procesos ----------
    private void guardarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar procesos CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int sel = chooser.showSaveDialog(this);
        if (sel == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println("Nombre,Rafaga,Llegada,Prioridad");
                for (Proceso p : procesos) {
                    pw.printf("%s,%d,%d,%d%n", p.nombre, p.rafagaOriginal, p.llegada, p.prioridad);
                }
                JOptionPane.showMessageDialog(this, "Guardado exitoso: " + f.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error guardando CSV: " + e.getMessage());
            }
        }
    }

    private void cargarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar procesos CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int sel = chooser.showOpenDialog(this);
        if (sel == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                procesos.clear();
                modeloProcesos.setRowCount(0);
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first && line.toLowerCase().startsWith("nombre")) { first = false; continue; }
                    String[] parts = line.split(",");
                    if (parts.length < 4) continue;
                    String n = parts[0].trim();
                    int r = Integer.parseInt(parts[1].trim());
                    int l = Integer.parseInt(parts[2].trim());
                    int p = Integer.parseInt(parts[3].trim());
                    Proceso proc = new Proceso(n, r, l, p, randomColorForName(n));
                    procesos.add(proc);
                    modeloProcesos.addRow(new Object[]{n, r, l, p, "Preparado"});
                }
                panelMemoria.setProcesos(procesos);
                JOptionPane.showMessageDialog(this, "Carga completada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error cargando CSV: " + ex.getMessage());
            }
        }
    }

    // ---------- Exportar resultados CSV ----------
    private void exportarResultadosCSV() {
        if (modeloResultados.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay resultados para exportar.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar Resultados CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int sel = chooser.showSaveDialog(this);
        if (sel == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".csv")) f = new File(f.getAbsolutePath() + ".csv");
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println("Nombre,TiempoEspera,TiempoRetorno,TiempoRespuesta");
                for (int i = 0; i < modeloResultados.getRowCount(); i++) {
                    String name = modeloResultados.getValueAt(i, 0).toString();
                    String e = modeloResultados.getValueAt(i, 1).toString();
                    String r = modeloResultados.getValueAt(i, 2).toString();
                    String resp = modeloResultados.getValueAt(i, 3).toString();
                    pw.printf("%s,%s,%s,%s%n", name, e, r, resp);
                }
                JOptionPane.showMessageDialog(this, "Exportación exitosa: " + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exportando CSV: " + ex.getMessage());
            }
        }
    }

    // ---------- Clases internas ----------
    static class Proceso {
        String nombre;
        int rafagaOriginal; // duración total esperada
        int rafaga;         // mutable (puede usarse en clones)
        int llegada;
        int prioridad;
        Color color;

        public Proceso(String n, int r, int l, int p, Color c) {
            nombre = n;
            rafagaOriginal = r;
            rafaga = r;
            llegada = l;
            prioridad = p;
            color = c;
        }

        // Clonador para simulaciones (preserva rafagaOriginal)
        public Proceso(Proceso otro) {
            nombre = otro.nombre;
            rafagaOriginal = otro.rafagaOriginal;
            rafaga = otro.rafagaOriginal;
            llegada = otro.llegada;
            prioridad = otro.prioridad;
            color = otro.color;
        }
    }

    static class GanttEntry {
        Proceso proceso;
        int duracion; // en unidades (entero)

        public GanttEntry(Proceso p, int d) {
            proceso = new Proceso(p); // store a copy to avoid mutating original
            duracion = d;
        }
    }

    // Panel Memoria
    class PanelMemoria extends JPanel {
        private List<Proceso> lista;

        public void setProcesos(List<Proceso> p) {
            this.lista = p;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(45, 45, 45));
            if (lista == null || lista.isEmpty()) return;
            int width = getWidth();
            int x = 20;
            int y = 20;
            int blockW = Math.max(80, (width - 40) / lista.size() - 10);
            for (Proceso p : lista) {
                g.setColor(p.color);
                g.fillRect(x, y, blockW, 60);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, blockW, 60);
                g.setColor(Color.BLACK);
                g.drawString(p.nombre, x + 8, y + 30);
                g.setColor(Color.DARK_GRAY);
                g.drawString("R:" + p.rafagaOriginal + " L:" + p.llegada, x + 8, y + 48);
                x += blockW + 10;
            }
        }
    }

    // Panel Gantt
    class PanelGantt extends JPanel {
        private List<GanttEntry> secuencia = new ArrayList<>();
        private int cursorIndex = 0;

        public void setSecuencia(List<GanttEntry> s) {
            this.secuencia = s;
            this.cursorIndex = 0;
            repaint();
        }

        public void setCursorIndex(int i) {
            this.cursorIndex = i;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            int x = 50;
            int y = 50;
            int height = 40;
            int unitW = 40; // ancho por unidad de duración
            for (int i = 0; i < secuencia.size(); i++) {
                GanttEntry e = secuencia.get(i);
                Color c = e.proceso.color;
                g.setColor(c);
                g.fillRect(x, y, unitW * e.duracion, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, unitW * e.duracion, height);
                g.setColor(Color.BLACK);
                g.drawString(e.proceso.nombre, x + 6, y + 24);
                g.setColor(Color.DARK_GRAY);
                String startLabel = String.valueOf(computeStartTimeUpToIndex(i));
                g.drawString(startLabel, x, y + height + 15);
                x += unitW * e.duracion;
            }
            // Cursor
            int cursorX = 50;
            for (int i = 0; i < cursorIndex && i < secuencia.size(); i++) {
                cursorX += unitW * secuencia.get(i).duracion;
            }
            cursorX += unitW * ganttTick;
            g.setColor(Color.RED);
            g.drawLine(cursorX, y - 10, cursorX, y + height + 10);
        }

        private int computeStartTimeUpToIndex(int idx) {
            int t = 0;
            for (int i = 0; i < idx; i++) t += secuencia.get(i).duracion;
            return t;
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorCompleto app = new SimuladorCompleto();
            app.setVisible(true);
        });
    }
}
