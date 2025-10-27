import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimuladorCLI {
    private static final int MEMORIA_TOTAL = 2048; // MB
    private MetricasSimulacion metricas;
    private List<Proc> procesos;
    private volatile boolean running = true;
    private volatile int memoriaUsada = 0;
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java -cp target/simulador-ram-darkplus-1.0-SNAPSHOT-jar-with-dependencies.jar SimuladorCLI <algoritmo> <numProcesos>");
            System.out.println("Algoritmos disponibles: FIFO, SJF, PRIORIDAD, RR");
            System.exit(1);
        }
        
        String algoritmo = args[0].toUpperCase();
        int numProcesos = Integer.parseInt(args[1]);
        
        SimuladorCLI simulador = new SimuladorCLI();
        simulador.ejecutar(algoritmo, numProcesos);
    }
    
    public void ejecutar(String algoritmo, int numProcesos) {
        // Inicializar métricas
        metricas = new MetricasSimulacion(algoritmo, numProcesos, MEMORIA_TOTAL);
        
        // Generar procesos
        generarProcesos(numProcesos);
        
        // Ejecutar simulación
        ejecutarSimulacion(algoritmo);
        
        // Exportar resultados
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String rutaArchivo = String.format("simulacion_%s_%s.csv", algoritmo.toLowerCase(), timestamp);
        
        try {
            metricas.exportarCSV(rutaArchivo);
            System.out.println("Simulación completada. Resultados guardados en: " + rutaArchivo);
        } catch (Exception e) {
            System.err.println("Error al exportar resultados: " + e.getMessage());
        }
    }
    
    private void generarProcesos(int n) {
        procesos = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 1; i <= n; i++) {
            int rafaga = 1 + rnd.nextInt(8);
            int mem = 80 + rnd.nextInt(200);
            int prio = 1 + rnd.nextInt(5);
            Proc p = new Proc("P"+i, rafaga, mem, prio, 0);
            procesos.add(p);
            memoriaUsada += mem;
            
            System.out.printf("Proceso P%d: Rafaga=%d, Memoria=%d, Prioridad=%d%n", 
                            i, rafaga, mem, prio);
        }
    }
    
    private void ejecutarSimulacion(String algoritmo) {
        List<Proc> lista = new ArrayList<>(procesos);
        
        // Ordenar según algoritmo
        switch(algoritmo) {
            case "SJF":
                lista.sort(Comparator.comparingInt(a -> a.rafaga));
                break;
            case "PRIORIDAD":
                lista.sort(Comparator.comparingInt(a -> a.prio));
                break;
        }
        
        Queue<Proc> cola = new LinkedList<>(lista);
        int quantum = 2; // Para Round Robin
        int tiempo = 0;
        
        System.out.println("\nIniciando simulación con algoritmo: " + algoritmo);
        
        while (!cola.isEmpty() && running) {
            Proc p = cola.poll();
            if (p == null) break;
            
            int ejecutar = algoritmo.equals("RR") ? Math.min(quantum, p.rafaga) : p.rafaga;
            
            // Registrar inicio de proceso
            metricas.registrarEvento(p.name, "INICIO", tiempo, p.mem);
            
            // Simular ejecución
            for (int i = 0; i < ejecutar && running; i++) {
                try { 
                    Thread.sleep(100); // más rápido que la versión GUI
                } catch (InterruptedException e) {
                    running = false;
                    break;
                }
                p.rafaga--;
                tiempo++;
                
                System.out.printf("T=%d: Ejecutando %s (Rafaga restante: %d)%n", 
                                tiempo, p.name, p.rafaga);
            }
            
            if (p.rafaga > 0 && algoritmo.equals("RR")) {
                cola.add(p);
            } else {
                memoriaUsada -= p.mem;
                metricas.registrarEvento(p.name, "FIN", tiempo, p.mem);
                System.out.printf("T=%d: Finalizado %s%n", tiempo, p.name);
            }
        }
        
        System.out.println("Simulación completada en tiempo: " + tiempo);
    }
    
    // Clase interna para representar procesos
    static class Proc {
        String name;
        int rafaga;
        int mem;
        int prio;
        int llegada;
        
        Proc(String n, int r, int m, int p, int l) {
            name = n;
            rafaga = r;
            mem = m;
            prio = p;
            llegada = l;
        }
    }
}