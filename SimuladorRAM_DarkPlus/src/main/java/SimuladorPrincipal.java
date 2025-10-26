import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class SimuladorPrincipal extends JFrame {

    private PanelEstadisticasConGraficas panelEst;
    private JTextField txtNumProcesses;
    private JComboBox<String> comboAlg;
    private JButton btnRun, btnStop, btnPrepare;
    private JCheckBox chkSound;
    private SoundManager soundManager;

    private volatile boolean running = false;

    // Simulation data
    private List<Proc> processes = Collections.synchronizedList(new ArrayList<>());
    private ExecutorService executor;
    private int totalMemoryMB = 2048;
    private volatile int memoriaUsadaMB = 0;

    public SimuladorPrincipal() {
        super("SimuladorRAM - Dark+ Edition");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLayout(new BorderLayout());
        EstiloDarkConsole.aplicarEstilo(this);

        soundManager = new SoundManager();

        // Top toolbar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setBackground(EstiloDarkConsole.PANEL);
        top.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JLabel logo = new JLabel("SimuladorRAM");
        logo.setForeground(EstiloDarkConsole.ACCENT);
        logo.setFont(new Font("Consolas", Font.BOLD, 18));
        top.add(logo);

        top.add(Box.createHorizontalStrut(12));
        top.add(new JLabel("Algoritmo:")).setForeground(EstiloDarkConsole.TEXT);
        comboAlg = new JComboBox<>(new String[]{"FIFO", "SJF", "Prioridad", "Round Robin"});
        comboAlg.setBackground(EstiloDarkConsole.PANEL);
        comboAlg.setForeground(EstiloDarkConsole.TEXT);
        top.add(comboAlg);

        top.add(new JLabel("Procesos:")).setForeground(EstiloDarkConsole.TEXT);
        txtNumProcesses = new JTextField("6", 4);
        top.add(txtNumProcesses);

        btnPrepare = EstiloDarkConsole.styledButton("Preparar", new Color(0x2D2D2D));
        btnRun = EstiloDarkConsole.styledButton("Ejecutar ▶", EstiloDarkConsole.ACCENT);
        btnStop = EstiloDarkConsole.styledButton("Detener ⏹", new Color(0xB33A3A));
        top.add(btnPrepare);
        top.add(btnRun);
        top.add(btnStop);

        chkSound = new JCheckBox("Sonido");
        chkSound.setSelected(true);
        chkSound.setBackground(EstiloDarkConsole.PANEL);
        chkSound.setForeground(EstiloDarkConsole.TEXT);
        top.add(chkSound);

        add(top, BorderLayout.NORTH);

        // Center panel: charts and small log
        panelEst = new PanelEstadisticasConGraficas();
        panelEst.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(panelEst, BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(320, 0));
        right.setBackground(EstiloDarkConsole.PANEL);
        right.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JTextArea txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(0x0F1417));
        txtLog.setForeground(EstiloDarkConsole.LOG);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        right.add(new JLabel(" LOG "), BorderLayout.NORTH);
        right.add(new JScrollPane(txtLog), BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        // Bottom status
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(EstiloDarkConsole.PANEL);
        bottom.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        JLabel memLabel = new JLabel("Memoria total: " + totalMemoryMB + " MB");
        memLabel.setForeground(EstiloDarkConsole.TEXT);
        bottom.add(memLabel);
        add(bottom, BorderLayout.SOUTH);

        // Events
        btnPrepare.addActionListener(e -> {
            prepareProcesses();
            txtLog.append(timestamp() + " Procesos preparados: " + processes.size() + "\n");
            panelEst.setDatosReales(processes.size(), 0, totalMemoryMB, memoriaUsadaMB);
            soundManager.setEnabled(chkSound.isSelected());
        });

        btnRun.addActionListener(e -> {
            if (running) return;
            soundManager.setEnabled(chkSound.isSelected());
            running = true;
            txtLog.append(timestamp() + " Ejecución iniciada: " + comboAlg.getSelectedItem() + "\n");
            if (soundManager.isEnabled()) soundManager.beep();
            executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> runScheduling((String)comboAlg.getSelectedItem(), txtLog));
        });

        btnStop.addActionListener(e -> {
            running = false;
            if (executor != null) executor.shutdownNow();
            txtLog.append(timestamp() + " Simulación detenida\n");
            if (soundManager.isEnabled()) soundManager.beep();
        });

        chkSound.addActionListener(e -> soundManager.setEnabled(chkSound.isSelected()));

        setLocationRelativeTo(null);
    }

    private String timestamp() {
        return "[" + java.time.LocalTime.now().withNano(0).toString() + "]";
    }

    private void prepareProcesses() {
        processes.clear();
        memoriaUsadaMB = 0;
        Random rnd = new Random();
        int n = 6;
        try { n = Integer.parseInt(txtNumProcesses.getText().trim()); } catch(Exception e){}
        for (int i=1;i<=n;i++){
            int raf = 1 + rnd.nextInt(8);
            int mem = 80 + rnd.nextInt(200);
            int prio = 1 + rnd.nextInt(5);
            Proc p = new Proc("P"+i, raf, mem, prio, 0);
            processes.add(p);
            memoriaUsadaMB += mem;
        }
    }

    private void runScheduling(String alg, JTextArea log) {
        List<Proc> list = new ArrayList<>(processes);
        if (alg.equals("SJF")) list.sort(Comparator.comparingInt(a->a.rafaga));
        if (alg.equals("Prioridad")) list.sort(Comparator.comparingInt(a->a.prio));
        int quantum = 2;

        int time = 0;
        Queue<Proc> queue = new LinkedList<>(list);
        while (running && (!queue.isEmpty())) {
            Proc p = queue.poll();
            if (p==null) break;
            int execute = alg.equals("Round Robin") ? Math.min(quantum, p.rafaga) : p.rafaga;
            for (int i=0;i<execute && running;i++){
                try { Thread.sleep(700); } catch (InterruptedException e){ running=false; break;}
                p.rafaga--;
                time++;
                int runningNow = 1;
                int memUsed = memoriaUsadaMB;
                panelEst.setDatosReales(processes.size(), runningNow, totalMemoryMB, memUsed);
            }
            if (p.rafaga>0 && alg.equals("Round Robin")) {
                queue.add(p);
            } else {
                SwingUtilities.invokeLater(() -> log.append(timestamp() + " ✔ Proceso finalizado: " + p.name + "\\n"));
                memoriaUsadaMB -= p.mem;
                if (soundManager.isEnabled()) soundManager.beep();
            }
        }
        running=false;
        SwingUtilities.invokeLater(() -> log.append(timestamp() + " ▶ Ejecución finalizada\\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorPrincipal app = new SimuladorPrincipal();
            app.setVisible(true);
        });
    }

    static class Proc {
        String name;
        int rafaga;
        int mem;
        int prio;
        int llegada;
        Proc(String n,int r,int m,int p,int l){ name=n; rafaga=r; mem=m; prio=p; llegada=l;}
    }
}
