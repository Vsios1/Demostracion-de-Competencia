using System;

/// <summary>
/// Clase principal que contiene el punto de entrada de la aplicación de consola.
/// </summary>
class Program
{
    static void Main(string[] args)
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

            var option = Console.ReadLine();

            switch (option)
            {
                case "1":
                    Console.Write("Nombre del medicamento: ");
                    string name = Console.ReadLine();
                    Console.Write("Lote: ");
                    string lot = Console.ReadLine();
                    Console.Write("Fecha de vencimiento (dd/mm/yyyy): ");
                    DateTime expirationDate;
                    // Se valida el formato de fecha y que no esté vencida
                    while (!DateTime.TryParseExact(Console.ReadLine(), "dd/MM/yyyy", null, System.Globalization.DateTimeStyles.None, out expirationDate) || expirationDate.Date < DateTime.Today.Date)
                    {
                        if (expirationDate.Date < DateTime.Today.Date)
                        {
                            Console.WriteLine("❌ No se puede registrar un medicamento vencido. Intente de nuevo (dd/mm/yyyy): ");
                        }
                        else
                        {
                            Console.WriteLine("Formato de fecha incorrecto. Intente de nuevo (dd/mm/yyyy): ");
                        }
                    }
                    pharmacy.RegisterMedicine(name, lot, expirationDate);
                    break;
                case "2":
                    pharmacy.DeliverMedicine();
                    break;
                case "3":
                    pharmacy.ShowStock();
                    break;
                case "4":
                    Console.Write("Días a verificar (ej. 30): ");
                    if (int.TryParse(Console.ReadLine(), out int days))
                    {
                        pharmacy.CheckUpcomingExpirations(days);
                    }
                    else
                    {
                        Console.WriteLine("Entrada inválida. Por favor ingrese un número.");
                    }
                    break;
                case "5":
                    pharmacy.RemoveExpiredMedicines();
                    break;
                case "6":
                    exit = true;
                    break;
                default:
                    Console.WriteLine("\n❌ Opción no válida. Intente de nuevo.");
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
}
using System;

/// <summary>
/// Clase principal que contiene el punto de entrada de la aplicación de consola.
/// </summary>
class Program
{
    static void Main(string[] args)
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

            var option = Console.ReadLine();

            switch (option)
            {
                case "1":
                    Console.Write("Nombre del medicamento: ");
                    string name = Console.ReadLine();
                    Console.Write("Lote: ");
                    string lot = Console.ReadLine();
                    Console.Write("Fecha de vencimiento (dd/mm/yyyy): ");
                    DateTime expirationDate;
                    // Se valida el formato de fecha y que no esté vencida
                    while (!DateTime.TryParseExact(Console.ReadLine(), "dd/MM/yyyy", null, System.Globalization.DateTimeStyles.None, out expirationDate) || expirationDate.Date < DateTime.Today.Date)
                    {
                        if (expirationDate.Date < DateTime.Today.Date)
                        {
                            Console.WriteLine("❌ No se puede registrar un medicamento vencido. Intente de nuevo (dd/mm/yyyy): ");
                        }
                        else
                        {
                            Console.WriteLine("Formato de fecha incorrecto. Intente de nuevo (dd/mm/yyyy): ");
                        }
                    }
                    pharmacy.RegisterMedicine(name, lot, expirationDate);
                    break;
                case "2":
                    pharmacy.DeliverMedicine();
                    break;
                case "3":
                    pharmacy.ShowStock();
                    break;
                case "4":
                    Console.Write("Días a verificar (ej. 30): ");
                    if (int.TryParse(Console.ReadLine(), out int days))
                    {
                        pharmacy.CheckUpcomingExpirations(days);
                    }
                    else
                    {
                        Console.WriteLine("Entrada inválida. Por favor ingrese un número.");
                    }
                    break;
                case "5":
                    pharmacy.RemoveExpiredMedicines();
                    break;
                case "6":
                    exit = true;
                    break;
                default:
                    Console.WriteLine("\n❌ Opción no válida. Intente de nuevo.");
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
}
