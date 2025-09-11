import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Main{

    public static void main(String[] args){

        Pharmacy pharmacy = new Pharmacy();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit){
            
            System.out.println("\033[h\033[2J"); //Limpia la consola 
            System.out.flush();
            System.out.println("--Sistema de Gestion de Medicamentos--");
            System.out.println("1. Registrar Medicamento");
            System.out.println("2. Entregar mediamento");
            System.out.println("3. Mostrar Stock Disponible");
            System.out.println("4. Verificar vencimientos proximos");
            System.out.println("5. Eliminar medicamentos vencidos");
            System.out.println("6. Salir del Programa.");
            System.out.println("\nSeleccione una opcion: ");

            String option = scanner.nextLine();
            switch (option){
                case "1":
                System.out.println("Nombre del medicamento: ");
                String name = scanner.nextLine();
                System.out.println("Lote: ");
                String lot = scanner.nextLine();
                System.out.println("Fecha  de vencimiento(dd/mm/yyyy): ");
                Date expirationDate = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false); // Validacion estricta del formato 
                while (expirationDate == null){
                    try {

                        expirationDate = sdf.parse(scanner.nextLine());
                        // Validar que el medicamento no esté vencido en el momento del registro 
                        if (expirationDate.before(new Date())){

                            System.out.println("No se puede registrar el medicamento vancido.");
                            expirationDate = null; 
                            System.out.println("Intente de nuevo (dd/mm/yyyy): ");
                        }
                    }catch (ParseException e){
                        System.out.println("Formato de fecha incorrecto. Intente de nuevo (dd/mm/yyyy): ");
                    }
                }
                pharmacy.registerMedicine(name , lot, expirationDate);
                break; 
                case "2": 
                    pharmacy.deliverMedicine(); 
                break;
                case "3": 
                    pharmacy.showStock(); 
                break; 
                case "4": 
                    System.out.println("Dias a verificar (ej. 30): "); 
                    try {

                        int days = Integer.parseInt(scanner.nextLine());
                        pharmacy.checkUpcomingExpirations(days);
                    } catch (NumberFormatException e){

                        System.out.println("Entrada invalida. Por favor ingrese un numero.");
                    }
                break; 
                case "5": 
                    pharmacy.removeExpiredMedicines();
                break;
                case "6":
                    exit = true; 
                break; 
                default: 
                    System.out.println("\n Opcion no valida. Intente de nuevo.");
                break; 
            }

            if (!exit){

                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
        System.out.println("\nCerrando el sistema. ¡BYE BYE!");
        scanner.close();
    }
}
