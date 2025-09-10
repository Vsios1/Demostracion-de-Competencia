using System;
using InventarioMedicamentos.Modelos;
using InventarioMedicamentos.Servicios;

class Program
{
    static void Main()
    {
        Pharmacy pharmacy = new Pharmacy();
        bool exit = false;

        while (!exit)
        {
            Console.Clear();
            Console.WriteLine("--- Sistema de Gestión de Medicamentos ---");
            Console.WriteLine("1. Registrar medicamento");
            Console.WriteLine("2. Entregar medicamento");
            Console.WriteLine("3. Mostrar stock disponible");
            Console.WriteLine("4. Verificar vencimientos próximos");
            Console.WriteLine("5. Eliminar medicamentos vencidos");
            Console.WriteLine("6. Salir");
            Console.Write("\nSeleccione una opción: ");

            string option = Console.ReadLine();

            switch (option)
            {
                case "1":
                    Register(pharmacy);
                    break;
                case "2":
                    pharmacy.DeliverMedicine();
                    break;
                case "3":
                    pharmacy.ShowStock();
                    break;
                case "4":
                    CheckExpirations(pharmacy);
                    break;
                case "5":
                    pharmacy.RemoveExpiredMedicines();
                    break;
                case "6":
                    exit = true;
                    break;
                default:
                    Console.WriteLine("\nOpción no válida. Intente de nuevo.");
                    break;
            }

            if (!exit)
            {
                Console.WriteLine("\nPresione cualquier tecla para continuar...");
                Console.ReadKey();
            }
        }

        Console.WriteLine("\nCerrando el sistema. ¡Adiós!");
    }

    static void Register(Pharmacy pharmacy)
    {
        Console.Write("Nombre del medicamento: ");
        string name = Console.ReadLine();

        Console.Write("Lote: ");
        string lot = Console.ReadLine();

        Console.Write("Fecha de vencimiento (dd/MM/yyyy): ");
        DateTime expirationDate;

        while (!DateTime.TryParseExact(Console.ReadLine(), "dd/MM/yyyy", null, System.Globalization.DateTimeStyles.None, out expirationDate) 
               || expirationDate.Date < DateTime.Today)
        {
            Console.WriteLine("Fecha inválida o vencida. Intente de nuevo (dd/MM/yyyy): ");
        }

        pharmacy.RegisterMedicine(name, lot, expirationDate);
    }

    static void CheckExpirations(Pharmacy pharmacy)
    {
        Console.Write("Días a verificar (ej. 30): ");
        if (int.TryParse(Console.ReadLine(), out int days) && days > 0)
        {
            pharmacy.CheckUpcomingExpirations(days);
        }
        else
        {
            Console.WriteLine("Entrada inválida. Por favor ingrese un número válido.");
        }
    }
}
