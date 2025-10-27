import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MetricasSimulacion {
    private String algoritmo;
    private int numProcesos;
    private int memoriaTotal;
    private List<EventoSimulacion> eventos;
    private Map<String, EstadisticasProceso> estadisticasProcesos;
    
    public MetricasSimulacion(String algoritmo, int numProcesos, int memoriaTotal) {
        this.algoritmo = algoritmo;
        this.numProcesos = numProcesos;
        this.memoriaTotal = memoriaTotal;
        this.eventos = new ArrayList<>();
        this.estadisticasProcesos = new HashMap<>();
    }
    
    public void registrarEvento(String proceso, String tipo, int tiempoEjecucion, int memoriaUsada) {
        eventos.add(new EventoSimulacion(proceso, tipo, tiempoEjecucion, memoriaUsada));
        
        if (!estadisticasProcesos.containsKey(proceso)) {
            estadisticasProcesos.put(proceso, new EstadisticasProceso(proceso));
        }
        
        EstadisticasProceso stats = estadisticasProcesos.get(proceso);
        if (tipo.equals("INICIO")) {
            stats.tiempoInicio = tiempoEjecucion;
        } else if (tipo.equals("FIN")) {
            stats.tiempoFin = tiempoEjecucion;
            stats.memoriaUsada = memoriaUsada;
        }
    }
    
    public void exportarCSV(String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            // Encabezado general
            writer.println("Fecha,Algoritmo,NumProcesos,MemoriaTotal");
            writer.printf("%s,%s,%d,%d%n", 
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                algoritmo, numProcesos, memoriaTotal);
            writer.println();
            
            // Eventos
            writer.println("Eventos:");
            writer.println("Timestamp,Proceso,Tipo,TiempoEjecucion,MemoriaUsada");
            for (EventoSimulacion evento : eventos) {
                writer.printf("%s,%s,%s,%d,%d%n",
                    evento.timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    evento.proceso, evento.tipo, evento.tiempoEjecucion, evento.memoriaUsada);
            }
            writer.println();
            
            // Estadísticas por proceso
            writer.println("Estadisticas por Proceso:");
            writer.println("Proceso,TiempoInicio,TiempoFin,TiempoTotal,MemoriaUsada");
            for (EstadisticasProceso stats : estadisticasProcesos.values()) {
                writer.printf("%s,%d,%d,%d,%d%n",
                    stats.proceso, stats.tiempoInicio, stats.tiempoFin,
                    (stats.tiempoFin - stats.tiempoInicio), stats.memoriaUsada);
            }
        }
    }
    
    private static class EventoSimulacion {
        LocalDateTime timestamp;
        String proceso;
        String tipo;
        int tiempoEjecucion;
        int memoriaUsada;
        
        EventoSimulacion(String proceso, String tipo, int tiempoEjecucion, int memoriaUsada) {
            this.timestamp = LocalDateTime.now();
            this.proceso = proceso;
            this.tipo = tipo;
            this.tiempoEjecucion = tiempoEjecucion;
            this.memoriaUsada = memoriaUsada;
        }
    }
    
    private static class EstadisticasProceso {
        String proceso;
        int tiempoInicio;
        int tiempoFin;
        int memoriaUsada;
        
        EstadisticasProceso(String proceso) {
            this.proceso = proceso;
        }
    }
}