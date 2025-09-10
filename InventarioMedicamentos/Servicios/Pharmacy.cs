using System;
using System.Collections.Generic;
using System.Linq;
using InventarioMedicamentos.Modelos;

namespace InventarioMedicamentos.Servicios
{
    public class Pharmacy
    {
        private Queue<Medicine> medicineStock = new Queue<Medicine>();

        public void RegisterMedicine(string name, string lot, DateTime expirationDate)
        {
            var newMedicine = new Medicine(name, lot, expirationDate);
            medicineStock.Enqueue(newMedicine);
            Console.WriteLine($"\nMedicamento '{newMedicine.Name}' registrado correctamente.");
        }

        public void DeliverMedicine()
        {
            if (medicineStock.Any())
            {
                var delivered = medicineStock.Dequeue();
                Console.WriteLine($"\nMedicamento entregado (FIFO): {delivered}");
            }
            else
            {
                Console.WriteLine("\nNo hay medicamentos en stock para entregar.");
            }
        }

        public void ShowStock()
        {
            if (medicineStock.Any())
            {
                Console.WriteLine("\n--- Stock Disponible ---");
                foreach (var med in medicineStock)
                {
                    Console.WriteLine(med);
                }
                Console.WriteLine("------------------------");
            }
            else
            {
                Console.WriteLine("\nEl stock de medicamentos está vacío.");
            }
        }

        public void CheckUpcomingExpirations(int days)
        {
            if (!medicineStock.Any())
            {
                Console.WriteLine("\nEl stock de medicamentos está vacío.");
                return;
            }

            var now = DateTime.Today;
            var threshold = now.AddDays(days);

            var upcoming = medicineStock
                .Where(m => m.ExpirationDate.Date >= now && m.ExpirationDate.Date <= threshold)
                .OrderBy(m => m.ExpirationDate);

            if (upcoming.Any())
            {
                Console.WriteLine($"\n--- Medicamentos que vencen en los próximos {days} días ---");
                foreach (var med in upcoming)
                    Console.WriteLine(med);
                Console.WriteLine("---------------------------------------------------------");
            }
            else
            {
                Console.WriteLine($"\nNo hay medicamentos que venzan en los próximos {days} días.");
            }
        }

        public void RemoveExpiredMedicines()
        {
            if (!medicineStock.Any())
            {
                Console.WriteLine("\nNo hay medicamentos en stock.");
                return;
            }

            var now = DateTime.Today;
            int removed = 0;
            var tempQueue = new Queue<Medicine>();

            while (medicineStock.Any())
            {
                var med = medicineStock.Dequeue();
                if (med.ExpirationDate.Date < now)
                {
                    removed++;
                    Console.WriteLine($"Eliminando medicamento vencido: {med}");
                }
                else
                {
                    tempQueue.Enqueue(med);
                }
            }

            medicineStock = tempQueue;

            Console.WriteLine($"\nSe eliminaron {removed} medicamento(s) vencido(s).");
        }

        // 🚀 Nuevo método: Counting Sort para ordenar por vencimiento
        public void ShowStockSortedByExpiration()
        {
            if (!medicineStock.Any())
            {
                Console.WriteLine("\nEl stock de medicamentos está vacío.");
                return;
            }

            // Convertimos la cola en lista para poder ordenarla
            var medicines = medicineStock.ToList();

            // Creamos un arreglo de claves (días hasta el vencimiento)
            int[] keys = medicines.Select(m => (m.ExpirationDate - DateTime.Today).Days).ToArray();

            // ----------------------------
            // APLICACIÓN DE COUNTING SORT
            // ----------------------------

            int min = keys.Min();
            int max = keys.Max();
            int range = max - min + 1;

            int[] count = new int[range];
            Medicine[] output = new Medicine[medicines.Count];

            // Paso 1: Conteo
            for (int i = 0; i < keys.Length; i++)
                count[keys[i] - min]++;

            // Paso 2: Acumulado
            for (int i = 1; i < count.Length; i++)
                count[i] += count[i - 1];

            // Paso 3: Construcción del resultado (estable)
            for (int i = keys.Length - 1; i >= 0; i--)
            {
                output[count[keys[i] - min] - 1] = medicines[i];
                count[keys[i] - min]--;
            }

            // Mostrar resultados
            Console.WriteLine("\n--- Stock Ordenado por Fecha de Vencimiento (Counting Sort) ---");
            foreach (var med in output)
                Console.WriteLine(med);
            Console.WriteLine("---------------------------------------------------------------");
        }
    }
}
