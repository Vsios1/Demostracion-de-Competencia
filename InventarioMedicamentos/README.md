# Sistema de Gestión de Medicamentos

## 1. Descripción
Este proyecto es un sistema de consola en C# que permite gestionar un inventario de medicamentos en una farmacia u hospital.  
Funciones principales:

- Registrar medicamentos.  
- Entregar medicamentos (FIFO).  
- Mostrar stock disponible.  
- Verificar vencimientos próximos.  
- Eliminar medicamentos vencidos.  

El sistema utiliza **colas (`Queue<Medicine>`)** para mantener el orden de entrega.

---

## 2. Estructura de carpetas y archivos
InventarioMedicamentos/
│
├── Modelos/
│ └── Medicine.cs ← Clase que representa un medicamento
│
├── Servicios/
│ └── Pharmacy.cs ← Clase que maneja la lógica de la farmacia
│
└── Program.cs ← Menú principal y flujo de ejecución

---

## 3. Clases principales

### 3.1 Medicine
Representa un medicamento con sus propiedades:

| Propiedad        | Descripción                        |
|-----------------|------------------------------------|
| `Name`          | Nombre del medicamento             |
| `Lot`           | Lote del medicamento               |
| `ExpirationDate`| Fecha de vencimiento               |

---

### 3.2 Pharmacy
Clase encargada de la lógica de la farmacia:

| Método                     | Función                                                          |
|-----------------------------|-----------------------------------------------------------------|
| `RegisterMedicine`          | Registra un medicamento en la cola                               |
| `DeliverMedicine`           | Entrega el primer medicamento registrado (FIFO)                 |
| `ShowStock`                 | Muestra todos los medicamentos disponibles                      |
| `CheckUpcomingExpirations`  | Muestra los medicamentos que vencen en los próximos días        |
| `RemoveExpiredMedicines`    | Elimina los medicamentos vencidos del stock                     |

---

### 3.3 Program.cs
- Muestra el **menú principal**.  
- Recibe la opción del usuario y llama a los métodos de `Pharmacy`.  
- Contiene validación de entradas y control de flujo.

---

## 4. Menú del Sistema

Al ejecutar el programa (`dotnet run`):

--- Sistema de Gestión de Medicamentos ---

Registrar medicamento

Entregar medicamento

Mostrar stock disponible

Verificar vencimientos próximos

Eliminar medicamentos vencidos

Salir


---

## 5. Flujo de ejecución por opción

            ┌─────────────────────────────┐
            │       Program.cs           │
            │   (Menú principal)         │
            └─────────────┬─────────────┘
                          │
  ┌───────────────────────┼────────────────────────┐
  │                       │                        │
  ▼                       ▼                        ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│ Opción 1: │ │ Opción 2: │ │ Opción 3: │
│ Registrar │ │ Entregar │ │ Mostrar stock │
│ medicamento │ │ medicamento │ │ disponible │
└───────┬───────┘ └───────┬───────┘ └───────┬───────┘
│ │ │
▼ ▼ ▼
┌───────────────┐ ┌─────────────────┐ ┌──────────────────┐
│ Register() │ │ Pharmacy. │ │ Pharmacy. │
│ en Program.cs │──────>│ DeliverMedicine │─────>│ ShowStock() │
└───────┬───────┘ │ (Dequeue FIFO) │ │ Recorre Queue │
│ └─────────────────┘ └──────────────────┘
▼
┌───────────────┐
│ Pharmacy │
│ RegisterMedicine│
│ (Enqueue) │
└───────┬───────┘
▼
┌───────────────┐
│ Medicine │
│ (Name, Lot, │
│ Expiration) │
└───────────────┘

**Otros flujos:**

- **Opción 4: Verificar vencimientos próximos**  
  `CheckUpcomingExpirations()` → Filtra la cola y muestra próximos vencimientos.  

- **Opción 5: Eliminar vencidos**  
  `RemoveExpiredMedicines()` → Elimina expirados y actualiza la cola.  

- **Opción 6: Salir** → Finaliza el programa.

---

## 6. Ejemplo de uso

1. Registrar medicamentos:

Nombre del medicamento: Paracetamol
Lote: A123
Fecha de vencimiento (dd/MM/yyyy): 15/12/2025
Medicamento 'Paracetamol' registrado correctamente

2. Mostrar stock:

--- Stock Disponible ---
Nombre: Paracetamol, Lote: A123, Vencimiento: 15/12/2025

3. Entregar medicamento:

Medicamento entregado (FIFO): Nombre: Paracetamol, Lote: A123, Vencimiento: 15/12/2025

4. Verificar vencimientos próximos (30 días):

No hay medicamentos que venzan en los próximos 30 días.

5. Eliminar vencidos:

Se eliminaron 0 medicamento(s) vencido(s).

---

## 7. Cómo ejecutar

1. Abrir terminal en la raíz del proyecto:

InventarioMedicamentos


2. Ejecutar:

```bash
dotnet run



