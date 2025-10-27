import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.io.*;

/**
 * SimuladorAvanzado.java
 * Simulador avanzado que integra:
 * - Multiprogramación con N CPUs (hilos reales)
 * - Memoria dinámica (First Fit / Best Fit / Worst Fit)
 * - Interfaz Swing (tabla procesos, panel memoria, Gantt simplificado)
 *
 * Compilar:
 *   javac SimuladorAvanzado.java
 * Ejecutar:
 *   java SimuladorAvanzado
 *
 * Nota: Es una base didáctica. Puedes adaptarla y mejorar performance/estética.
 */
public class SimuladorAvanzado extends JFrame {
    // UI models
    private DefaultTableModel modeloProcesos;
    private JTable tablaProcesos;
    private DefaultTableModel modeloResultados;
    private JTable tablaResultados;

    // Controls
    private JComboBox<String> comboAllocMethod;
    private JTextField txtMemSize, txtCPUs;
    private JButton btnAgregar, btnIniciar, btnStop, btnReset, btnAsignarMem, btnLiberarMem;

    // Visual panels
    private PanelMemoria panelMemoria;
    private PanelGantt panelGantt;

    // Core data
    private MemoriaDinamica memoria;
    private List<Proceso> procesos = Collections.synchronizedList(new ArrayList<>());
    private BlockingQueue<Proceso> readyQueue = new LinkedBlockingQueue<>();
    private List<CPU> cpus = new ArrayList<>();
    private volatile boolean running = false;

    // Results
    private DefaultListModel<String> logModel = new DefaultListModel<>();

    public SimuladorAvanzado() {
        super("Simulador Avanzado - Multiprogramación y Memoria Dinámica");
        setSize(1100, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        // Top controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Memoria (unidades):"));
        txtMemSize = new JTextField("64", 5);
        top.add(txtMemSize);

        top.add(new JLabel("CPUs:"));
        txtCPUs = new JTextField("2", 3);
        top.add(txtCPUs);

        top.add(new JLabel("Alloc:"));
        comboAllocMethod = new JComboBox<>(new String[]{"First Fit", "Best Fit", "Worst Fit"});
        top.add(comboAllocMethod);

        btnAsignarMem = new JButton("Inicializar Memoria");
        top.add(btnAsignarMem);

        btnAgregar = new JButton("Agregar Proceso");
        top.add(btnAgregar);

        btnIniciar = new JButton("Iniciar Simulación");
        top.add(btnIniciar);

        btnStop = new JButton("Detener");
        top.add(btnStop);

        btnReset = new JButton("Reset");
        top.add(btnReset);

        btnLiberarMem = new JButton("Liberar Memoria (manual)");
        top.add(btnLiberarMem);

        add(top, BorderLayout.NORTH);

        // Center: tables and visuals
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.45);

        // Procesos table
        modeloProcesos = new DefaultTableModel(new String[]{"Nombre", "Tamaño", "Llegada", "Rafaga", "Prioridad", "Estado"}, 0);
        tablaProcesos = new JTable(modeloProcesos);
        JScrollPane spProc = new JScrollPane(tablaProcesos);

        // Results table
        modeloResultados = new DefaultTableModel(new String[]{"Nombre", "Espera", "Retorno", "Respuesta"}, 0);
        tablaResultados = new JTable(modeloResultados);
        JScrollPane spRes = new JScrollPane(tablaResultados);
        spRes.setPreferredSize(new Dimension(400, 120));

        JPanel panelTables = new JPanel(new BorderLayout());
        panelTables.add(spProc, BorderLayout.CENTER);
        panelTables.add(spRes, BorderLayout.SOUTH);

        split.setTopComponent(panelTables);

        // Bottom visuals: Gantt and Memoria
        JPanel bottom = new JPanel(new BorderLayout());
        panelGantt = new PanelGantt();
        panelGantt.setPreferredSize(new Dimension(1000, 250));
        panelMemoria = new PanelMemoria();
        panelMemoria.setPreferredSize(new Dimension(1000, 160));
        bottom.add(panelGantt, BorderLayout.CENTER);
        bottom.add(panelMemoria, BorderLayout.SOUTH);

        split.setBottomComponent(bottom);

        add(split, BorderLayout.CENTER);

        // Side log
        JList<String> listLog = new JList<>(logModel);
        JScrollPane spLog = new JScrollPane(listLog);
        spLog.setPreferredSize(new Dimension(250, 0));
        add(spLog, BorderLayout.EAST);

        // Events
        btnAsignarMem.addActionListener(e -> inicializarMemoria());
        btnAgregar.addActionListener(e -> dialogoAgregarProceso());
        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnStop.addActionListener(e -> detenerSimulacion());
        btnReset.addActionListener(e -> resetTotal());
        btnLiberarMem.addActionListener(e -> dialogoLiberarMemoria());

        // default init
        inicializarMemoria();
    }

    private void inicializarMemoria() {
        try {
            int size = Integer.parseInt(txtMemSize.getText().trim());
            memoria = new MemoriaDinamica(size);
            panelMemoria.setMemoria(memoria);
            log("Memoria inicializada con " + size + " unidades.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tamaño de memoria inválido");
        }
    }

    private void dialogoAgregarProceso() {
        JTextField nombre = new JTextField();
        JTextField tam = new JTextField();
        JTextField llegada = new JTextField("0");
        JTextField raf = new JTextField();
        JTextField prio = new JTextField("1");

        Object[] message = {
                "Nombre:", nombre,
                "Tamaño (unidades de memoria):", tam,
                "Tiempo de llegada (int):", llegada,
                "Ráfaga (tiempo CPU):", raf,
                "Prioridad (menor=nivel alto):", prio
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Proceso", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String n = nombre.getText().trim();
                int tamaño = Integer.parseInt(tam.getText().trim());
                int l = Integer.parseInt(llegada.getText().trim());
                int r = Integer.parseInt(raf.getText().trim());
                int p = Integer.parseInt(prio.getText().trim());
                Proceso proc = new Proceso(n, tamaño, r, l, p, randomColorForName(n));
                procesos.add(proc);
                modeloProcesos.addRow(new Object[]{n, tamaño, l, r, p, "Creado"});
                panelMemoria.setProcesos(procesos);

                log("Proceso agregado: " + n);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Entrada inválida para proceso");
            }
        }
    }

    private Color randomColorForName(String name) {
        int h = Math.abs(Objects.hashCode(name));
        int r = 100 + (h % 100);
        int g = 50 + ((h / 3) % 150);
        int b = 50 + ((h / 7) % 150);
        return new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
    }

    private void iniciarSimulacion() {
        if (memoria == null) {
            JOptionPane.showMessageDialog(this, "Inicializa la memoria primero");
            return;
        }

        if (running) {
            JOptionPane.showMessageDialog(this, "Simulación ya en ejecución");
            return;
        }

        // Asignar memoria a procesos que estén pendientes (opcional)
        for (Proceso p : procesos) {
            if (!p.asignado) {
                boolean ok = memoria.asignar(p, (String) comboAllocMethod.getSelectedItem());
                if (ok) {
                    p.asignado = true;
                    actualizarEstadoTabla(p.nombre, "En memoria");
                    log("Asignado memoria a: " + p.nombre);
                } else {
                    log("No hubo memoria para: " + p.nombre);
                }
            }
        }

        // Encolar procesos listos (respecto a llegada)
        procesos.stream().filter(p -> p.asignado).forEach(p -> {
            readyQueue.add(p);
            actualizarEstadoTabla(p.nombre, "Listo");
        });

        // Crear CPUs
        int nCPUs = 1;
        try {
            nCPUs = Integer.parseInt(txtCPUs.getText().trim());
            if (nCPUs < 1) nCPUs = 1;
        } catch (Exception ignored) {}

        running = true;
        cpus.clear();
        for (int i = 0; i < nCPUs; i++) {
            CPU cpu = new CPU("CPU-" + (i + 1));
            cpus.add(cpu);
            cpu.start();
        }

        log("Simulación iniciada con " + nCPUs + " CPUs.");

        // Start Gantt updater thread
        new Thread(() -> {
            while (running) {
                panelGantt.setSequence(getGanttSequenceSnapshot());
                panelGantt.repaint();
                panelMemoria.repaint();
                try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private void detenerSimulacion() {
        running = false;
        for (CPU cpu : cpus) cpu.interrupt();
        cpus.clear();
        log("Simulación detenida");
    }

    private void resetTotal() {
        detenerSimulacion();
        procesos.clear();
        modeloProcesos.setRowCount(0);
        modeloResultados.setRowCount(0);
        memoria = new MemoriaDinamica(64);
        panelMemoria.setMemoria(memoria);
        readyQueue.clear();
        logModel.clear();
        panelGantt.setSequence(new ArrayList<>());
        panelMemoria.repaint();
        log("Reset realizado");
    }

    private void dialogoLiberarMemoria() {
        String name = JOptionPane.showInputDialog(this, "Nombre del proceso a liberar:");
        if (name != null) {
            memoria.liberarPorNombre(name);
            panelMemoria.repaint();
            log("Memoria liberada para: " + name);
        }
    }

    private void actualizarEstadoTabla(String nombre, String estado) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < modeloProcesos.getRowCount(); i++) {
                if (modeloProcesos.getValueAt(i, 0).equals(nombre)) {
                    modeloProcesos.setValueAt(estado, i, 5);
                    break;
                }
            }
        });
    }

    private List<GanttEntry> getGanttSequenceSnapshot() {
        // Snapshot simple: show currently executing processes in CPUs and queued
        List<GanttEntry> entries = new ArrayList<>();
        // CPUs
        synchronized (cpus) {
            for (CPU cpu : cpus) {
                if (cpu.current != null) entries.add(new GanttEntry(cpu.current.nombre, 1, cpu.current.color));
            }
        }
        // Queue (up to 10)
        Object[] queued = readyQueue.toArray();
        for (int i = 0; i < Math.min(10, queued.length); i++) {
            Proceso p = (Proceso) queued[i];
            entries.add(new GanttEntry(p.nombre, 1, p.color));
        }
        return entries;
    }

    private void log(String s) {
        SwingUtilities.invokeLater(() -> {
            logModel.addElement(s);
        });
    }

    // CPU thread
    class CPU extends Thread {
        String id;
        Proceso current = null;

        public CPU(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            log(id + " arrancado");
            try {
                while (!isInterrupted()) {
                    Proceso p = readyQueue.take(); // blocks
                    current = p;
                    actualizarEstadoTabla(p.nombre, "Ejecutando");
                    log(id + " -> ejecutando: " + p.nombre);

                    // Simulate execution unit by unit
                    while (p.rafaga > 0) {
                        Thread.sleep(400); // unit time
                        p.rafaga--;

                        // update UI
                        panelMemoria.repaint();
                        panelGantt.repaint();
                    }

                    // Finished
                    log(id + " -> terminado: " + p.nombre);
                    actualizarEstadoTabla(p.nombre, "Terminado");
                    memoria.liberarPorNombre(p.nombre);
                    panelMemoria.repaint();

                    // record results (simple)
                    SwingUtilities.invokeLater(() -> modeloResultados.addRow(new Object[]{p.nombre, "-", "-", "-"}));

                    current = null;
                }
            } catch (InterruptedException e) {
                log(id + " interrumpido");
            }
        }
    }

    // ---------------- Memory classes ----------------
    static class BloqueMemoria {
        int inicio;
        int tamaño;
        boolean ocupado = false;
        String procesoNombre = null;

        BloqueMemoria(int inicio, int tamaño) {
            this.inicio = inicio;
            this.tamaño = tamaño;
        }
    }

    class MemoriaDinamica {
        List<BloqueMemoria> bloques = Collections.synchronizedList(new ArrayList<>());
        int total;

        MemoriaDinamica(int total) {
            this.total = total;
            bloques.clear();
            bloques.add(new BloqueMemoria(0, total));
        }

        // assign based on method
        boolean asignar(Proceso p, String metodo) {
            synchronized (bloques) {
                BloqueMemoria elegido = null;
                switch (metodo) {
                    case "First Fit":
                        for (BloqueMemoria b : bloques) if (!b.ocupado && b.tamaño >= p.tamaño) { elegido = b; break; }
                        break;
                    case "Best Fit":
                        int best = Integer.MAX_VALUE;
                        for (BloqueMemoria b : bloques) if (!b.ocupado && b.tamaño >= p.tamaño) {
                            if (b.tamaño < best) { best = b.tamaño; elegido = b; }
                        }
                        break;
                    case "Worst Fit":
                        int worst = -1;
                        for (BloqueMemoria b : bloques) if (!b.ocupado && b.tamaño >= p.tamaño) {
                            if (b.tamaño > worst) { worst = b.tamaño; elegido = b; }
                        }
                        break;
                }

                if (elegido == null) return false;

                // split if needed
                if (elegido.tamaño > p.tamaño) {
                    BloqueMemoria nuevo = new BloqueMemoria(elegido.inicio + p.tamaño, elegido.tamaño - p.tamaño);
                    int idx = bloques.indexOf(elegido);
                    elegido.tamaño = p.tamaño;
                    bloques.add(idx + 1, nuevo);
                }

                elegido.ocupado = true;
                elegido.procesoNombre = p.nombre;
                p.asignado = true;
                p.bloque = elegido;
                return true;
            }
        }

        void liberarPorNombre(String nombre) {
            synchronized (bloques) {
                for (BloqueMemoria b : bloques) {
                    if (nombre.equals(b.procesoNombre)) {
                        b.ocupado = false;
                        b.procesoNombre = null;
                    }
                }
                // merge adjacent free blocks
                mergeFreeBlocks();
            }
        }

        private void mergeFreeBlocks() {
            synchronized (bloques) {
                for (int i = 0; i < bloques.size() - 1; ) {
                    BloqueMemoria a = bloques.get(i);
                    BloqueMemoria b = bloques.get(i + 1);
                    if (!a.ocupado && !b.ocupado) {
                        a.tamaño += b.tamaño;
                        bloques.remove(i + 1);
                    } else i++;
                }
            }
        }

        List<BloqueMemoria> snapshot() {
            synchronized (bloques) {
                return new ArrayList<>(bloques);
            }
        }
    }

    // ---------------- Data classes ----------------
    static class Proceso {
        String nombre;
        int tamaño; // memory size required
        int rafaga; // remaining CPU time
        int rafagaOriginal;
        int llegada;
        int prioridad;
        boolean asignado = false;
        Color color;
        BloqueMemoria bloque = null;

        Proceso(String nombre, int tamaño, int rafaga, int llegada, int prioridad, Color color) {
            this.nombre = nombre;
            this.tamaño = tamaño;
            this.rafaga = rafaga;
            this.rafagaOriginal = rafaga;
            this.llegada = llegada;
            this.prioridad = prioridad;
            this.color = color;
        }
    }

    static class GanttEntry {
        String nombre;
        int dur;
        Color color;
        GanttEntry(String n, int d, Color c) { nombre = n; dur = d; color = c; }
    }

    // ---------------- Panels ----------------
    class PanelMemoria extends JPanel {
        MemoriaDinamica mem;
        List<Proceso> lista = Collections.emptyList();

        void setMemoria(MemoriaDinamica m) { this.mem = m; repaint(); }
        void setProcesos(List<Proceso> p) { this.lista = p; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(30, 30, 30));
            if (mem == null) return;
            List<BloqueMemoria> snap = mem.snapshot();
            int w = getWidth() - 40;
            int x = 20;
            int y = 20;
            int total = mem.total;
            for (BloqueMemoria b : snap) {
                int bw = Math.max(20, (int) ((b.tamaño / (double) total) * w));
                g.setColor(b.ocupado ? Color.ORANGE : Color.LIGHT_GRAY);
                g.fillRect(x, y, bw, 60);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, bw, 60);
                String label = (b.ocupado ? b.procesoNombre : "Libre") + " (" + b.tamaño + ")";
                g.setColor(Color.BLACK);
                g.drawString(label, x + 4, y + 30);
                x += bw + 4;
            }
        }
    }

    class PanelGantt extends JPanel {
        List<GanttEntry> sequence = new ArrayList<>();

        void setSequence(List<GanttEntry> s) { this.sequence = s; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.WHITE);
            int x = 10;
            int y = 30;
            int h = 30;
            int unit = 30;
            for (GanttEntry e : sequence) {
                g.setColor(e.color != null ? e.color : Color.GRAY);
                g.fillRect(x, y, unit * e.dur, h);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, unit * e.dur, h);
                g.setColor(Color.BLACK);
                g.drawString(e.nombre, x + 5, y + 20);
                x += unit * e.dur + 5;
            }
        }
    }

    // ---------------- Main ----------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorAvanzado app = new SimuladorAvanzado();
            app.setVisible(true);
        });
    }
}
