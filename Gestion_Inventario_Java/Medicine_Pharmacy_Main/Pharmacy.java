import java.util.LinkedList;
import java.util.Queue;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Pharmacy {

    private Queue<Medicine> medicineStock = new LinkedList<>();

    public void registerMedicine(String name, String lot, Date expirationDate) {
        Medicine newMedicine = new Medicine(name, lot, expirationDate);
        medicineStock.add(newMedicine);
        System.out.println("\nMedicamento " + newMedicine.getName() + " registrado con éxito.");
    }

    public void deliverMedicine() {
        if (!medicineStock.isEmpty()) {
            Medicine delivered = medicineStock.poll();
            System.out.println("\nMedicamento entregado: " + delivered);
        } else {
            System.out.println("\nEl stock está vacío.");
        }
    }

    public void showStock() {
        if (!medicineStock.isEmpty()) {
            System.out.println("\n--- Stock Disponible ---");
            for (Medicine medicine : medicineStock) {
                System.out.println(medicine);
            }
            System.out.println("---------------------");
        } else {
            System.out.println("\nEl stock de medicamentos está vacío.");
        }
    }

    
    public void checkUpcomingExpirations(int days) {
        if (!medicineStock.isEmpty()) {
            System.out.println("\n--- Medicamentos que vencen en los próximos " + days + " días ---");
            boolean found = false;
            Date now = new Date();

            for (Medicine medicine : medicineStock) {
                long diffInMillis = medicine.getExpirationDate().getTime() - now.getTime();
                long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                if (diffInDays <= days && diffInDays >= 0) {
                    System.out.println(medicine);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No hay medicamentos que venzan en los próximos " + days + " días.");
            }
            System.out.println("-----------------------------------------------------------------");
        } else {
            System.out.println("\nEl stock de medicamentos está vacío.");
        }
    }

    public void removeExpiredMedicines() {
        int removedCount = 0;
        Date now = new Date();
        Queue<Medicine> tempQueue = new LinkedList<>();

        while (!medicineStock.isEmpty()) {
            Medicine current = medicineStock.poll();
            if (current.getExpirationDate().before(now)) {
                System.out.println("Eliminando vencido: " + current.getName() + " (Lote: " + current.getLot() + ")");
                removedCount++;
            } else {
                tempQueue.add(current);
            }
        }

        medicineStock = tempQueue;

        if (removedCount > 0) {
            System.out.println("\nSe eliminaron " + removedCount + " medicamento(s) vencido(s).");
        } else {
            System.out.println("\nNo se encontraron medicamentos vencidos.");
        }
    }
}

