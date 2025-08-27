# Notas de Implementación - Them Bots

## 🚨 Detalles Técnicos Críticos

### Nombres de Métodos Específicos
- **Proyectil**: Usar `avanzar()`, NO `mover()`
- **Proyectil**: Usar `getOrigen()`, NO `getOwner()` 
- **Proyectil**: Usar `getVector()`, NO `getDireccion()`
- **Robot**: `getDireccionVector()` devuelve Vector unitario de la dirección

### Constructores y Validaciones
- **Radio**: Constructor requiere valores > 0 (NO acepta 0)
- **Vida**: Constructor permite negativos pero los convierte a 0 automáticamente
- **Vector**: `normalizar()` devuelve Vector(0,0) si magnitud es 0

### Interfaces vs Clases
- **ContextoPercepcion**: Es interfaz, se implementa con clase anónima en `FabricaContexto`
- **ConfiguracionCombate**: Es clase abstracta sin setters públicos
- **InstanciaRobot**: Es clase sin setters públicos

## 🔧 Patrones de Testing Implementados

### Para Tests de IA
```java
// Crear contexto simplificado sin mocks complejos
private ContextoPercepcion crearContexto(VistaRobot robotPropio, List<VistaRobot> todosRobots) {
    return new ContextoPercepcion() {
        @Override
        public VistaRobot miEstado() { return robotPropio; }
        @Override
        public List<VistaRobot> todosLosRobots() { return todosRobots; }
        // ... otros métodos con valores por defecto
    };
}
```

### Para Tests de Casos de Uso
```java
// Implementaciones anónimas para evitar problemas de setters
private ConfiguracionCombate crearConfiguracion(params...) {
    return new ConfiguracionCombate() {
        @Override
        public double anchoArena() { return ancho; }
        @Override
        public List<Robot> crearRobots() { return robots.stream()... }
        // ... otros métodos
    };
}
```

## 🎯 Puntos de Extensión del Sistema

### Agregar Nuevos Comportamientos
1. Implementar `Comportamiento` interface
2. Lógica en método `decidir(ContextoPercepcion contexto)`
3. Acceso a información via contexto:
   - `contexto.miEstado()` - estado propio
   - `contexto.todosLosRobots()` - robots enemigos
   - `contexto.obstaculos()` - obstáculos estáticos
   - `contexto.proyectiles()` - proyectiles activos

### Agregar Nuevos Tipos de Acción
1. Crear clase que extienda `Accion`
2. Agregar enum en `TipoAccion` 
3. Implementar lógica en `SimuladorCombate.simularTick()` switch
4. Considerar validaciones necesarias

### Agregar Nuevas Métricas
1. Modificar `EstadisticasRobot` con nuevos campos
2. Actualizar `AcumuladorEstadisticas` para capturar datos
3. Modificar `calcularResultadoFinal()` si afecta ranking

## 🐛 Debugging y Troubleshooting

### Verificar Determinismo
```java
// Ejecutar misma configuración múltiples veces
ResultadoCombate r1 = simular(config);
ResultadoCombate r2 = simular(config);
// Deben ser idénticos: ticks, ganador, ranking
```

### Inspeccionar Eventos por Tick
```java
for (int tick = 0; tick < simulador.getTickActual(); tick++) {
    List<EventoDeCombate> eventos = simulador.eventosDelTick(tick);
    eventos.forEach(evento -> System.out.println(evento));
}
```

### Debugging de IA
```java
// En comportamiento, agregar logs temporales
@Override
public Accion decidir(ContextoPercepcion contexto) {
    VistaRobot yo = contexto.miEstado();
    System.out.println("Robot " + yo.getNombre() + " en " + yo.getPosicion());
    // ... lógica de decisión
}
```

## ⚡ Optimizaciones Potenciales

### Performance
- **Spatial Partitioning**: Para colisiones en arenas grandes
- **Object Pooling**: Reutilizar objetos Accion frecuentes
- **Lazy Evaluation**: Contexto de percepción bajo demanda

### Memoria
- **Event Pruning**: Limitar historial de eventos
- **Statistics Optimization**: Calcular métricas incrementalmente
- **Immutable Collections**: Usar estructuras optimizadas

## 🔄 Integración con Spring Boot

### Controladores REST Potenciales
```java
@RestController
@RequestMapping("/api/combate")
public class CombateController {
    
    @Autowired
    private SimularCombateCompleto simularCompleto;
    
    @PostMapping("/ejecutar")
    public ResultadoCombate ejecutarCombate(@RequestBody ConfiguracionCombate config) {
        return simularCompleto.ejecutar(config);
    }
}
```

### Configuración de Beans
```java
@Configuration
public class SimulacionConfig {
    
    @Bean
    public SimularCombateCompleto simularCombateCompleto() {
        return new SimularCombateCompleto();
    }
    
    @Bean
    public GestorDeSimulaciones gestorSimulaciones() {
        return new GestorDeSimulaciones();
    }
}
```

## 🧪 Limitaciones Conocidas de Testing

### Maven Wrapper
- Primera ejecución requiere conectividad para descargar Maven
- Usar `mvn` directamente si está instalado globalmente

### Implementaciones Anónimas
- Se usan para evitar problemas con clases sin setters
- No es ideal para producción, pero funciona para testing
- Considerar builders pattern para casos más complejos

### Mocking Strategy
- Se evitaron mocks complejos en favor de implementaciones reales
- Mejor para confiabilidad, pero tests más verbosos
- Mockito disponible en spring-boot-starter-test si se necesita

## 📝 TODOs Potenciales

### Funcionalidades
- [ ] Interfaz web para visualización
- [ ] Configuración desde JSON/YAML
- [ ] Sistema de replay de combates
- [ ] Más tipos de comportamientos IA
- [ ] Torneos con múltiples combates
- [ ] Estadísticas históricas

### Técnico  
- [ ] Sealed classes para TipoAccion (Java 17+)
- [ ] Pattern matching más extensivo
- [ ] Optimizaciones de performance
- [ ] Métricas de telemetría
- [ ] Configuración externalizada
- [ ] Validaciones con Bean Validation

---

**Importante**: Mantener el dominio libre de dependencias externas para preservar la arquitectura hexagonal.