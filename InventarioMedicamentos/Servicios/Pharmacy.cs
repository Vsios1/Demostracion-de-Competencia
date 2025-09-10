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
    }
}
