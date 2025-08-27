# Contexto del Proyecto - Them Bots

## 🎯 Objetivo Principal
Este proyecto es un **ejercicio de aprendizaje** enfocado en:
- **Arquitectura Hexagonal** (Puertos y Adaptadores)
- **Características modernas de Java** (hasta Java 21)
- **Domain-Driven Design** aplicado
- **Sistemas deterministas** y simulaciones

## 📁 Información Detallada
Para contexto completo y detalles técnicos, consultar la carpeta `.claude/`:
- `.claude/architecture-details.md` - Arquitectura hexagonal detallada
- `.claude/implementation-notes.md` - Detalles técnicos críticos y patrones
- `.claude/tests-summary.md` - Suite completa de tests implementada

## 🏗️ Arquitectura Hexagonal - Estructura del Proyecto

### Domain Layer (Núcleo)
**Ubicación**: `src/main/java/dev/maiki/thembots/domain/`

- **model/**: Entidades principales del dominio
  - `Robot.java` - Entidad principal con estado mutable
  - `Arena.java` - Contenedor del combate con robots, proyectiles y obstáculos  
  - `Proyectil.java` - Entidad de proyectil con trayectoria
  - `Obstaculo.java` - Entidad inmutable de obstáculo fijo
  - `accion/` - Actions que pueden realizar los robots
  - `enums/` - Estados y tipos de eventos

- **service/**: Servicios de dominio (lógica compleja)
  - `SimuladorCombate.java` - Orquestador principal de la simulación
  - `MotorDeColisiones.java` - Detección de colisiones
  - `MotorDeProyectiles.java` - Procesamiento de proyectiles
  - `AcumuladorEstadisticas.java` - Recolección de métricas

- **value/**: Objetos de valor inmutables (Records Java 16+)
  - `Posicion.java` - Coordenadas 2D
  - `Vector.java` - Vector de movimiento/dirección
  - `Vida.java` - Puntos de vida
  - `Radio.java` - Radio para colisiones

- **ports/**: Interfaces/contratos
  - `Comportamiento.java` - Interfaz para IA de robots

- **ia/**: Implementaciones de comportamientos
  - `ComportamientoCazador.java` - IA agresiva
  - `ComportamientoCobarde.java` - IA defensiva
  - `ComportamientoPatrulla.java` - IA de patrullaje

### Application Layer (Casos de Uso)
**Ubicación**: `src/main/java/dev/maiki/thembots/application/`

- **usecase/**: Casos de uso específicos
  - `SimularCombateCompleto.java` - Ejecuta simulación completa
  - `SimularUnTick.java` - Ejecuta un paso de simulación
  - `ObtenerHistorial.java` - Recupera eventos históricos
  - `ObtenerResultado.java` - Obtiene resultados finales

- **service/**: Servicios de aplicación
  - `GestorDeSimulaciones.java` - Maneja múltiples simulaciones activas
  - `ConsolaSerializadorSimulacion.java` - Serialización para consola

- **model/**: DTOs y configuraciones
  - `ConfiguracionCombate.java` - Configuración externa de combate
  - `InstanciaRobot.java` - DTO para crear robots

### Infrastructure Layer
Implementada por **Spring Boot** en la clase principal y configuraciones.

## 🔧 Comandos y Herramientas

### Maven
```bash
# Compilar proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run

# Linter y format (si está configurado)
mvn spotless:apply
```

### Testing - SUITE COMPLETA IMPLEMENTADA ✅
- **Framework**: JUnit 5 (incluido en spring-boot-starter-test)
- **Estructura de Tests Granular**: Organizados por capas arquitectónicas

#### Tests de Objetos de Valor (`domain/value/`)
- `PosicionTest.java` - Tests completos de coordenadas 2D, distancias, movimientos
- `VectorTest.java` - Tests de vectores: normalización, escalado, suma
- `VidaTest.java` - Tests de puntos de vida, daño, inmutabilidad
- `RadioTest.java` - Tests de radios de colisión, validaciones

#### Tests de Entidades del Dominio (`domain/model/`)
- `RobotTest.java` - Tests del robot: vida, movimiento, cooldown, estados
- `ArenaTest.java` - Tests de la arena: límites, proyectiles, obstáculos
- `ProyectilTest.java` - Tests de proyectiles: movimiento, propiedades

#### Tests de Servicios de Dominio (`domain/service/`)
- `MotorDeColisionesTest.java` - Tests de detección de colisiones complejas
- `SimuladorCombateTest.java` - Tests del simulador: ticks, eventos, finalización

#### Tests de Comportamientos IA (`domain/ia/`)
- `ComportamientoCazadorTestSimple.java` - Tests de IA agresiva: apuntado, disparo, movimiento

#### Tests de Casos de Uso (`application/usecase/`)
- `SimularCombateCompletoTest.java` - Tests del caso de uso principal con configuraciones complejas

#### Tests de Integración (`integration/`)
- `SimulacionCompletaIntegrationTest.java` - Tests de sistema completo con múltiples robots, IA y obstáculos

#### Comandos de Testing
```bash
# Ejecutar todos los tests
mvn test

# Tests específicos por clase
mvn test -Dtest=PosicionTest

# Tests por paquete
mvn test -Dtest="dev.maiki.thembots.domain.value.*Test"

# Solo tests de integración
mvn test -Dtest="dev.maiki.thembots.integration.*Test"
```

## 🎮 Mecánica de Simulación (Detalles Técnicos)

### Ciclo de Simulación (por tick)
1. **Decidir acciones**: Cada robot ejecuta su comportamiento
2. **Aplicar acciones**: Movimiento, giro, disparo (con validaciones)
3. **Procesar proyectiles**: Movimiento, colisiones, eliminación
4. **Registrar eventos**: Para debugging/replay
5. **Evaluar condiciones**: ¿Terminó el combate?

### Sistema Determinista
- **Orden fijo**: Los robots se procesan en orden determinista
- **Sin aleatoriedad**: No hay números aleatorios en la simulación
- **Estado inmutable**: Los objetos de valor no cambian después de creación

### Características Java 21 Implementadas
- **Records**: `Posicion`, `Vector`, `Radio`, `Vida`
- **Pattern matching**: Switch expressions en `SimuladorCombate.java:71`
- **Streams API**: Extensivo uso para filtros y transformaciones
- **Sealed classes**: (Potencial mejora futura para `TipoAccion`)

## 🐛 Debugging y Análisis

### Sistema de Eventos
- **EventoDeCombate**: Interfaz base para eventos
- **EventoSimple**: Eventos básicos con timestamp
- **Eventos por tick**: Mapa completo de historial `eventosPorTick`

### Tipos de Eventos
- `TICK_INICIADO`/`TICK_FINALIZADO`
- `MOVIMIENTO_REALIZADO`
- `GIRO_REALIZADO` 
- `DISPARO_EJECUTADO`
- `IMPACTO_DETECTADO`
- `ROBOT_DESTRUIDO`

## 📊 Métricas y Estadísticas

### AcumuladorEstadisticas rastrea:
- Disparos realizados por robot
- Impactos recibidos y dados
- Vida inicial vs final
- Tiempo de supervivencia
- Ranking final

## 🧪 ESTADO ACTUAL DE TESTS - COMPLETADO ✅

### Cobertura de Testing Implementada:
- ✅ **Tests Unitarios**: Objetos de valor, entidades, servicios
- ✅ **Tests de Integración**: Sistema completo end-to-end
- ✅ **Tests de Comportamiento**: IA y lógica de decisión
- ✅ **Tests Deterministas**: Verificación de reproducibilidad
- ✅ **Tests de Colisiones**: Validación precisa de física
- ✅ **Tests de Estados**: Ciclo de vida completo de entidades

### Características de los Tests:
- **Granularidad Completa**: Desde objetos básicos hasta simulaciones complejas
- **Casos Límite**: Tests para escenarios extremos y errores
- **Inmutabilidad**: Verificación de objetos de valor inmutables
- **Determinismo**: Validación de comportamiento determinista del simulador
- **Simulaciones Realistas**: Combates completos con múltiples tipos de IA

### Tests de Integración Notables:
- Combates con 3+ robots usando diferentes IAs (Cazador, Cobarde, Patrulla)
- Arenas con obstáculos estratégicos
- Gestión de múltiples simulaciones concurrentes
- Verificación de determinismo completo del sistema
- Análisis de estadísticas y rankings finales

## 🎯 Áreas de Mejora Potencial

1. **Interfaz Web**: Visualización en tiempo real
2. **Más Comportamientos IA**: Comportamientos más sofisticados
3. **Configuración Externa**: JSON/YAML para configurar combates
4. **Replay System**: Visualización de combates pasados
5. **Multithreading**: Simulaciones paralelas
6. **Sealed Classes**: Para tipos de acción más seguros

## 🔍 Puntos de Entrada para Desarrollo

### Para agregar nuevos comportamientos:
1. Implementar interfaz `Comportamiento`
2. Agregar lógica en método `decidir(ContextoPercepcion)`
3. Usar en `ConfiguracionCombate` o tests

### Para modificar mecánicas:
- **Movimiento**: `MotorDeColisiones.java`
- **Proyectiles**: `MotorDeProyectiles.java`
- **Simulación general**: `SimuladorCombate.java`

### Para agregar métricas:
- Modificar `AcumuladorEstadisticas.java`
- Actualizar `EstadisticasRobot.java`

## 🚨 NOTAS IMPORTANTES DE IMPLEMENTACIÓN

### Detalles Técnicos Clave:
1. **ContextoPercepcion**: Es una interfaz, se implementa con clase anónima en `FabricaContexto`
2. **Proyectil.avanzar()**: Método correcto (no `mover()`)
3. **Proyectil.getOrigen()**: Método correcto (no `getOwner()`)
4. **Vector.getVector()**: Método correcto (no `getDireccion()`)
5. **Radio**: Constructor requiere valores > 0 (no acepta 0)
6. **Vida**: Constructor permite negativos pero los convierte a 0

### Patrones de Testing Usados:
- **Implementaciones anónimas** para interfaces en tests
- **Builders helpers** para crear objetos de prueba
- **Contexts simplificados** para IA sin mocks complejos
- **Verificación de determinismo** en tests de integración

### Limitaciones Conocidas:
- No hay setters públicos en `ConfiguracionCombate` e `InstanciaRobot`
- Se usan implementaciones anónimas para testing
- Maven wrapper puede requerir conectividad para primera ejecución

---

**ESTADO**: Proyecto con suite de tests completa y granular implementada. Arquitectura hexagonal limpia con dominio independiente de frameworks externos.