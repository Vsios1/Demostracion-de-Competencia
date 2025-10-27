# Configuración de Java 21 LTS para SimuladorRAM_DarkPlus

Este documento proporciona instrucciones para configurar Java 21 (LTS) en tu entorno de desarrollo para el proyecto SimuladorRAM_DarkPlus.

## Requisitos

El proyecto está configurado para usar Java 21 LTS. Necesitarás:
- Java Development Kit (JDK) 21
- Apache Maven 3.9.x o superior (ya incluido en el proyecto)

## Pasos de Instalación

### 1. Instalar JDK 21

1. Descarga JDK 21 desde la página oficial de Oracle o usa OpenJDK:
   - [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
   - [Eclipse Temurin OpenJDK 21](https://adoptium.net/temurin/releases/?version=21)

2. Ejecuta el instalador y sigue las instrucciones.

### 2. Configurar JAVA_HOME en Windows

Abre PowerShell como administrador y ejecuta:

```powershell
# Establecer JAVA_HOME (ajusta la ruta según tu instalación)
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-21', 'Machine')

# Agregar Java al PATH
$javaPath = [System.Environment]::GetEnvironmentVariable('PATH', 'Machine')
[System.Environment]::SetEnvironmentVariable('PATH', $javaPath + ';%JAVA_HOME%\bin', 'Machine')
```

### 3. Verificar la Instalación

Abre una nueva terminal y ejecuta:
```powershell
java -version
```

Deberías ver algo como:
```
java version "21.0.x"
Java(TM) SE Runtime Environment (build 21.0.x+x-LTS)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.x+x-LTS, mixed mode)
```

## Usando Maven Toolchains (Opcional)

Si necesitas mantener múltiples versiones de Java en tu sistema:

1. Copia el archivo `toolchains-example.xml` a `~/.m2/toolchains.xml`
2. Ajusta las rutas en el archivo según tu instalación
3. Ejecuta Maven con: `mvn -t ~/.m2/toolchains.xml clean install`

## Problemas Comunes

### Error: "Invalid source release: 21"
- **Solución**: Verifica que JAVA_HOME apunta a JDK 21
```powershell
echo $env:JAVA_HOME
```

### Error: "No toolchain found with specification [...]"
- **Solución**: Verifica que el archivo `toolchains.xml` está correctamente configurado y las rutas existen

## Soporte

Si encuentras problemas, verifica:
1. La versión de Java: `java -version`
2. La ubicación de JAVA_HOME: `echo $env:JAVA_HOME`
3. Que Maven usa el JDK correcto: `mvn -v`