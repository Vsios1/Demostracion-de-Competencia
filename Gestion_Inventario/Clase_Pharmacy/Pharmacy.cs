using System;
using System.Collections.Generic;
using System.Linq;

/// <summary>
/// Clase que gestiona el inventario de la farmacia utilizando una cola (Queue).
/// </summary>
public class Pharmacy
{
    private Queue<Medicine> medicineStock = new Queue<Medicine>();

    /// <summary>
    /// Registra un nuevo medicamento en la cola.
    /// </summary>
    public void RegisterMedicine(string name, string lot, DateTime expirationDate)
    {
        Medicine newMedicine = new Medicine(name, lot, expirationDate);
        medicineStock.Enqueue(newMedicine);
        Console.WriteLine($"\n✅ Medicamento '{newMedicine.Name}' registrado con éxito.");
    }

    /// <summary>
    /// Entrega el medicamento que ha estado más tiempo en la cola (FIFO).
    /// </summary>
    public void DeliverMedicine()
    {
        if (medicineStock.Count > 0)
        {
            Medicine deliveredMedicine = medicineStock.Dequeue();
            Console.WriteLine($"\n✅ Medicamento entregado (FIFO): {deliveredMedicine}");
        }
        else
        {
            Console.WriteLine("\n❌ No hay medicamentos en stock para entregar.");
        }
    }

    /// <summary>
    /// Muestra el stock completo de medicamentos sin modificar la cola.
    /// </summary>
    public void ShowStock()
    {
        if (medicineStock.Count > 0)
        {
            Console.WriteLine("\n--- Stock Disponible ---");
            foreach (var medicine in medicineStock)
            {
                Console.WriteLine(medicine);
            }
            Console.WriteLine("------------------------");
        }
        else
        {
            Console.WriteLine("\n❌ El stock de medicamentos está vacío.");
        }
    }

    /// <summary>
    /// Muestra los medicamentos que vencerán en un número de días determinado.
    /// </summary>
    public void CheckUpcomingExpirations(int days)
    {
        if (medicineStock.Count > 0)
        {
            var now = DateTime.Today;
            var expirationThreshold = now.AddDays(days);
            
            // Usamos LINQ para filtrar y ordenar los medicamentos por su fecha de vencimiento
            var upcomingExpirations = medicineStock
                .Where(m => m.ExpirationDate.Date <= expirationThreshold.Date && m.ExpirationDate.Date >= now.Date)
                .OrderBy(m => m.ExpirationDate); // Ordenamos la vista por fecha de vencimiento

            if (upcomingExpirations.Any())
            {
                Console.WriteLine($"\n--- Medicamentos que vencen en los próximos {days} días ---");
                foreach (var medicine in upcomingExpirations)
                {
                    Console.WriteLine(medicine);
                }
                Console.WriteLine("---------------------------------------------------------");
            }
            else
            {
                Console.WriteLine($"\n✅ No hay medicamentos que venzan en los próximos {days} días.");
            }
        }
        else
        {
            Console.WriteLine("\n❌ El stock de medicamentos está vacío.");
        }
    }

    /// <summary>
    /// Elimina automáticamente los medicamentos que ya han vencido.
    /// </summary>
    public void RemoveExpiredMedicines()
    {
        int removedCount = 0;
        var now = DateTime.Today;
        
        // Se usa una lista temporal para reconstruir la cola
        var tempQueue = new Queue<Medicine>();
        
        // Se recorre la cola original
        while (medicineStock.Count > 0)
        {
            var current = medicineStock.Dequeue();
            // Se verifica si el medicamento ha vencido
            if (current.ExpirationDate.Date < now.Date)
            {
                Console.WriteLine($"⚠️ Eliminando medicamento vencido: {current}");
                removedCount++;
            }
            else
            {
                // Si no está vencido, se añade a la cola temporal
                tempQueue.Enqueue(current);
            }
        }
        
        // Se reemplaza la cola de stock con la cola temporal
        medicineStock = tempQueue;
        
        if (removedCount > 0)
        {
            Console.WriteLine($"\n✅ {removedCount} medicamento(s) vencido(s) eliminado(s) del stock.");
        }
        else
        {
            Console.WriteLine("\n✅ No se encontraron medicamentos vencidos para eliminar.");
        }
    }
}
