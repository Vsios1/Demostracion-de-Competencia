import java.util.Scanner;

public class SimulacionRAM {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        byte[] RAM = new byte[8];  // RAM de 8 bytes
        int opcion;

        do {
            System.out.println("\n===== SIMULACIÓN DE RAM (BYTE) =====");
            System.out.println("1. Mostrar memoria");
            System.out.println("2. Escribir en memoria");
            System.out.println("3. Leer de memoria");
            System.out.println("4. Limpiar memoria");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1: // Mostrar RAM
                    for (int i = 0; i < RAM.length; i++) {
                        System.out.println("Dirección [" + i + "] = " + RAM[i]);
                    }
                    break;

                case 2: // Escribir
                    System.out.print("Ingrese dirección (0-" + (RAM.length - 1) + "): ");
                    int dir = sc.nextInt();
                    if (dir >= 0 && dir < RAM.length) {
                        System.out.print("Ingrese valor (-128 a 127): ");
                        byte valor = sc.nextByte();
                        RAM[dir] = valor;
                        System.out.println("Valor escrito en dirección " + dir);
                    } else {
                        System.out.println("Dirección inválida.");
                    }
                    break;

                case 3: // Leer
                    System.out.print("Ingrese dirección a leer (0-" + (RAM.length - 1) + "): ");
                    int dirLeer = sc.nextInt();
                    if (dirLeer >= 0 && dirLeer < RAM.length) {
                        System.out.println("Contenido en dirección " + dirLeer + " = " + RAM[dirLeer]);
                    } else {
                        System.out.println("Dirección inválida.");
                    }
                    break;

                case 4: // Limpiar
                    for (int i = 0; i < RAM.length; i++) {
                        RAM[i] = 0;
                    }
                    System.out.println("Memoria limpiada (todo en 0).");
                    break;

                case 5:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 5);

        sc.close();
    }
}
