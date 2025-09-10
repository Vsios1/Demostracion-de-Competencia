```mermaid
flowchart TD
    A[Inicio] --> B[Mostrar Menú]
    B --> C[Leer Opción]
    C --> D{Opción}
    
    D -- 1 --> E[Registrar Medicamento]
    D -- 2 --> F[Entregar Medicamento]
    D -- 3 --> G[Mostrar Stock]
    D -- 4 --> H[Revisar Vencimientos]
    D -- 5 --> I[Eliminar Vencidos]
    D -- 6 --> J[Salir]
    
    E --> B
    F --> B
    G --> B
    H --> B
    I --> B
    J --> K[Fin]
<<<<<<< HEAD
=======
```
>>>>>>> ca67729090ec0af03618dc75574e30e2fae6f4ba
