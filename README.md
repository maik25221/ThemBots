# Them Bots

Un simulador determinista de combates entre robots implementado en Java 21 con arquitectura hexagonal.

## 🤖 Descripción

Them Bots es un simulador de combates donde robots programados con diferentes comportamientos de IA se enfrentan en una arena con obstáculos. El sistema es completamente determinista, lo que significa que dada la misma configuración inicial, el resultado será siempre el mismo.

## 🏗️ Tecnologías

- **Java 21** - Aprovechando las características más modernas del lenguaje
- **Spring Boot 3.5.3** - Framework de aplicación
- **Maven** - Gestión de dependencias
- **JUnit 5** - Testing

### Características Java 21 Utilizadas

- **Records** para objetos de valor inmutables (`Posicion`, `Vector`, `Vida`, `Radio`)
- **Pattern matching** en switch expressions
- **Streams API** para procesamiento funcional
- **Text blocks** para configuraciones

## 🏛️ Arquitectura Hexagonal

El proyecto está estructurado siguiendo los principios de arquitectura hexagonal (puertos y adaptadores):

```
src/main/java/dev/maiki/thembots/
├── domain/               # Núcleo de la lógica de negocio
│   ├── model/           # Entidades del dominio
│   ├── service/         # Servicios de dominio
│   ├── ports/           # Interfaces/contratos
│   ├── value/           # Objetos de valor
│   └── ia/              # Comportamientos de IA
├── application/          # Casos de uso y servicios de aplicación
│   ├── usecase/         # Casos de uso específicos
│   ├── service/         # Servicios de aplicación
│   └── model/           # DTOs y configuraciones
└── ThemBotsApplication.java  # Punto de entrada (Spring Boot)
```

### Separación de Responsabilidades

- **Domain**: Contiene toda la lógica de negocio sin dependencias externas
- **Application**: Orquesta casos de uso y coordina el dominio
- **Infrastructure**: Implementada por Spring Boot para entrada HTTP

## 🎮 Mecánica de Simulación

### Sistema de Combate

- **Simulación tick-by-tick**: Cada tick representa una unidad de tiempo discreta
- **Determinismo**: Mismo input = mismo output siempre
- **Cooldown de disparo**: Los robots deben esperar 5 ticks entre disparos
- **Sistema de colisiones**: Robots, proyectiles y obstáculos interactúan físicamente

### Acciones de Robot

1. **Mover** (`AccionMover`): Movimiento en la dirección actual
2. **Girar** (`AccionGirar`): Rotación angular
3. **Disparar** (`AccionDisparar`): Lanzar proyectil
4. **Nada** (`AccionNada`): No realizar acción

### Comportamientos de IA

- **ComportamientoCazador**: Busca y ataca al enemigo más cercano
- **ComportamientoCobarde**: Evita el combate directo
- **ComportamientoPatrulla**: Patrulla un área específica

## 🏟️ Arena y Obstáculos

- **Arena**: Espacio rectangular limitado donde ocurre el combate
- **Obstáculos**: Elementos fijos que bloquean movimiento y proyectiles
- **Límites**: Los robots no pueden salir de los límites de la arena

## 📊 Sistema de Estadísticas

- **AcumuladorEstadisticas**: Rastrea métricas de combate
- **Eventos por tick**: Sistema de eventos para debugging/replay
- **Rankings**: Clasificación final basada en supervivencia y rendimiento

## 🎯 Condiciones de Victoria

1. **Último robot en pie**: El combate termina cuando solo queda un robot activo
2. **Límite de ticks**: Si se alcanza el máximo de ticks configurado
3. **Empate**: Posible si múltiples robots tienen la misma vida final

## 🚀 Ejecución

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar aplicación
mvn spring-boot:run
```

## 📋 Casos de Uso Principales

- **SimularCombateCompleto**: Ejecuta una simulación completa hasta su finalización
- **SimularUnTick**: Ejecuta un solo paso de la simulación
- **ObtenerHistorial**: Recupera el historial de eventos de una simulación
- **ObtenerResultado**: Obtiene el resultado final de un combate

## 🎯 Objetivos del Proyecto

Este proyecto fue creado como ejercicio de aprendizaje para:

1. **Arquitectura Hexagonal**: Implementación práctica de puertos y adaptadores
2. **Java 21**: Exploración de características modernas del lenguaje
3. **Domain-Driven Design**: Modelado del dominio como núcleo de la aplicación
4. **Sistemas Deterministas**: Creación de simulaciones predecibles y replicables

## 🧪 Testing

El proyecto incluye tests unitarios para:

- Movimiento y colisiones de robots
- Impacto y trayectoria de proyectiles
- Lógica de simulación general
- Serialización de resultados

---

*Desarrollado como proyecto de aprendizaje de arquitectura hexagonal y características modernas de Java.*