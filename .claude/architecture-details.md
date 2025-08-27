# Detalles Técnicos de Arquitectura - Them Bots

## 🏛️ Arquitectura Hexagonal Detallada

### Principios Aplicados
- **Independencia del Framework**: El dominio no conoce Spring Boot
- **Inversión de Dependencias**: Interfaces en el dominio, implementaciones en infraestructura
- **Separación de Responsabilidades**: Cada capa tiene un propósito específico
- **Testabilidad**: Cada capa se puede testear independientemente

### Flujo de Dependencias
```
Infrastructure -> Application -> Domain
     ↑              ↑              ↑
Spring Boot    Casos de Uso    Lógica de Negocio
Controllers    Orquestación    Entidades/Services
```

## 🔧 Detalles de Implementación por Capa

### Domain Layer (Núcleo)
**Sin dependencias externas** - Solo Java puro

#### Entidades (`model/`)
- `Robot` - Estado mutable, identidad única UUID
- `Arena` - Agregado raíz, contiene robots/proyectiles/obstáculos
- `Proyectil` - Entidad con movimiento autónomo
- `Obstaculo` - Entidad inmutable

#### Value Objects (`value/`)
- `Posicion(double x, double y)` - Record inmutable
- `Vector(double dx, double dy)` - Record con operaciones matemáticas
- `Vida(double valor)` - Con validaciones de rango
- `Radio(double valor)` - Para cálculos de colisión

#### Services (`service/`)
- `SimuladorCombate` - Orquestador principal (patrón Service)
- `MotorDeColisiones` - Lógica especializada de física
- `MotorDeProyectiles` - Procesamiento de proyectiles
- `AcumuladorEstadisticas` - Agregación de métricas

#### Ports (`ports/`)
- `Comportamiento` - Interfaz para IA de robots

### Application Layer
**Orquesta el dominio, sin lógica de negocio**

#### Use Cases (`usecase/`)
- Implementan casos de uso específicos
- Coordinan servicios de dominio
- No contienen lógica de negocio

#### Services (`service/`)
- `GestorDeSimulaciones` - Gestión de múltiples simulaciones
- `ConsolaSerializadorSimulacion` - Adaptador para salida

### Infrastructure Layer
**Spring Boot y frameworks externos**

- Punto de entrada: `ThemBotsApplication.java`
- Configuraciones de Spring
- Potenciales: REST Controllers, Repositories, etc.

## 🎮 Ciclo de Simulación Detallado

### 1. Inicialización
```java
Arena arena = new Arena(ancho, alto, robots, obstaculos);
SimuladorCombate simulador = new SimuladorCombate(arena, tickMaximo);
```

### 2. Bucle Principal (SimuladorCombate.simularTick())
```java
while (!estaTerminado()) {
    // 1. Crear contexto de percepción para cada robot
    for (Robot robot : robotsActivos) {
        ContextoPercepcion contexto = fabricaContexto.generar(robot.getId(), tick, arena);
        Accion accion = robot.getComportamiento().decidir(contexto);
        decisiones.put(robot.getId(), accion);
    }
    
    // 2. Aplicar acciones con validaciones
    // 3. Procesar proyectiles
    // 4. Registrar eventos
    // 5. Evaluar condiciones de fin
}
```

### 3. Finalización
- Cálculo de estadísticas finales
- Determinación de ganador/empate
- Generación de ranking

## 🧠 Sistema de IA (Comportamientos)

### Interfaz Base
```java
public interface Comportamiento {
    Accion decidir(ContextoPercepcion contexto);
}
```

### Contexto de Percepción
- **miEstado()**: Estado del propio robot
- **todosLosRobots()**: Vista de todos los robots (incluyendo enemigos)
- **proyectiles()**: Proyectiles activos en la arena
- **obstaculos()**: Obstáculos estáticos
- **anchoArena(), altoArena()**: Dimensiones del campo
- **tickActual()**: Tiempo actual de simulación

### Comportamientos Implementados
1. **ComportamientoCazador**
   - Busca enemigo más cercano
   - Gira hacia él si no está alineado
   - Dispara si está en rango y alineado
   - Avanza/retrocede para mantener distancia óptima

2. **ComportamientoCobarde**
   - Huye del enemigo más cercano
   - Mantiene distancia máxima
   - Dispara solo si es inevitable

3. **ComportamientoPatrulla**
   - Patrulla entre dos puntos definidos
   - Ataca enemigos que encuentra en su ruta

## ⚙️ Sistema de Acciones

### Tipos de Acción
- `AccionMover(double distancia)` - Movimiento hacia adelante/atrás
- `AccionGirar(double angulo)` - Rotación en radianes
- `AccionDisparar()` - Crear proyectil
- `AccionNada()` - No hacer nada este tick

### Validaciones Aplicadas
- **Movimiento**: Colisiones con robots/obstáculos, límites de arena
- **Giro**: Normalización de ángulos a [0, 2π)
- **Disparo**: Verificación de cooldown, creación de proyectil

## 🎯 Sistema de Eventos y Debugging

### Tipos de Eventos
```java
public enum TipoEvento {
    TICK_INICIADO, TICK_FINALIZADO,
    MOVIMIENTO_REALIZADO, GIRO_REALIZADO, 
    DISPARO_EJECUTADO, IMPACTO_DETECTADO, 
    ROBOT_DESTRUIDO
}
```

### Registro de Eventos
- **Por tick**: `Map<Integer, List<EventoDeCombate>> eventosPorTick`
- **Acceso**: `simulador.eventosDelTick(tickNumber)`
- **Historial completo**: `simulador.historialCompleto()`

## 🔍 Determinismo del Sistema

### Garantías
1. **Sin aleatoriedad**: No hay Random ni Math.random()
2. **Orden fijo**: Robots procesados en orden determinista
3. **Cálculos precisos**: Uso de double con precisión controlada
4. **Estado inmutable**: Value objects no cambian después de creación

### Verificación
- Tests de determinismo ejecutan la misma configuración múltiples veces
- Verifican identidad exacta de resultados

## 📊 Sistema de Estadísticas

### Métricas Rastreadas
```java
public class EstadisticasRobot {
    private UUID robotId;
    private String nombre;
    private double vidaInicial;
    private double vidaFinal;
    private int disparosRealizados;
    private int impactosRecibidos;
    private int impactosCausados;
    private boolean destruido;
    private int ticksVivo;
}
```

### Ranking
- Ordenado por vida final (descendente)
- Criterios de desempate: impactos causados, disparos realizados
- Identificación de empates cuando múltiples robots tienen misma vida final

---

**Nota**: Esta arquitectura permite fácil extensión con nuevos comportamientos, tipos de acción, métricas y mecánicas de juego sin afectar el núcleo del dominio.