## DIAGRAMA DE 

```mermaid
classDiagram
    class Program {
        +Main() void
        +Register(pharmacy: Pharmacy) void
        +CheckExpirations(pharmacy: Pharmacy) void
    }

    class Medicine {
        +string Name
        +string Lot
        +DateTime ExpirationDate
        +Medicine(name: string, lot: string, expirationDate: DateTime)
        +ToString() string
    }

    class Pharmacy {
        -Queue~Medicine~ medicineStock
        +RegisterMedicine(name: string, lot: string, expirationDate: DateTime) void
        +DeliverMedicine() void
        +ShowStock() void
        +CheckUpcomingExpirations(days: int) void
        +RemoveExpiredMedicines() void
    }

    %% Relaciones
    Program --> Pharmacy : usa
    Pharmacy --> Medicine : contiene


```

__*Documentado*__
__Este diagrama de clases representa la estructura y relaciones principales del sistema de gestión de medicamentos:__

*Clase Program*

Actúa como el punto de entrada del sistema.

Métodos principales:

Main() → ejecuta el menú interactivo y controla el flujo del programa.

Register(pharmacy: Pharmacy) → solicita los datos de un medicamento y delega el registro a Pharmacy.

CheckExpirations(pharmacy: Pharmacy) → invoca la verificación de medicamentos próximos a vencer.

Relación: usa Pharmacy para delegar las operaciones sobre los medicamentos.

*Clase Pharmacy*

Gestiona el inventario de medicamentos.

Atributo privado: medicineStock (cola FIFO de objetos Medicine) para mantener el orden de ingreso.

Métodos principales:

RegisterMedicine() → agrega un medicamento al stock.

DeliverMedicine() → entrega el medicamento más antiguo (FIFO).

ShowStock() → muestra todos los medicamentos disponibles.

CheckUpcomingExpirations(days) → lista los medicamentos próximos a vencer.

RemoveExpiredMedicines() → elimina los vencidos del stock.

Relación: contiene Medicine, es decir, maneja múltiples instancias de esta clase.

*Clase Medicine*

Representa un medicamento con sus atributos específicos: Name, Lot y ExpirationDate.

Constructor y método ToString() para instanciar y mostrar la información del medicamento.


## DIAGRAMA DE SECCION

```mermaid
    sequenceDiagram
    participant U as Usuario
    participant P as Program
    participant Ph as Pharmacy
    participant M as Medicine

    U ->> P: Selecciona opción "1"
    P ->> U: Solicita datos (nombre, lote, vencimiento)
    U ->> P: Ingresa datos del medicamento
    P ->> Ph: RegisterMedicine(name, lot, expirationDate)
    Ph ->> M: Crear nuevo Medicine(name, lot, expirationDate)
    M -->> Ph: Retorna objeto Medicine
    Ph ->> Ph: Enqueue(Medicine)
    Ph -->> P: Confirmación de registro
    P -->> U: Mensaje "Medicamento registrado correctamente"
```
__*Documentado*__
Este diagrama de secuencia representa el flujo de interacción para la operación de registro de un medicamento en el sistema:

El usuario inicia la operación seleccionando la opción de registro en la interfaz de Program.

Program solicita los atributos del medicamento (nombre, lote, fecha de vencimiento) y captura la entrada del usuario.

Program invoca el método RegisterMedicine() de la clase Pharmacy, transmitiendo los datos ingresados.

Pharmacy instancia un objeto de la clase Medicine con los atributos proporcionados.

El objeto Medicine es retornado a Pharmacy, que lo enfila en la estructura de datos Queue para garantizar un control FIFO.

Pharmacy envía una confirmación de registro a Program, que finalmente comunica el éxito de la operación al usuario.

