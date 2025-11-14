# Guía de Despliegue en Render con Docker

Este documento describe cómo desplegar el microservicio de autenticación en Render usando Docker.

## Requisitos Previos

1. Cuenta en [Render](https://render.com)
2. Repositorio Git con el código del microservicio
3. Base de datos PostgreSQL (puede ser en Render o externa como Supabase)

## Archivos de Configuración

Este proyecto incluye los siguientes archivos para el despliegue:

- `Dockerfile`: Configuración multi-stage para construcción optimizada
- `.dockerignore`: Archivos a excluir en la imagen Docker
- `render.yaml`: Blueprint de Render (infraestructura como código)

## Opción 1: Despliegue Automático con Blueprint (Recomendado)

### Paso 1: Conectar Repositorio

1. Inicia sesión en [Render Dashboard](https://dashboard.render.com)
2. Haz clic en "New +" → "Blueprint"
3. Conecta tu repositorio de GitHub/GitLab
4. Render detectará automáticamente el archivo `render.yaml`

### Paso 2: Configurar Variables de Entorno

Antes de desplegar, configura las siguientes variables de entorno en el dashboard de Render:

#### Variables Requeridas

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_HOST` | Host de la base de datos PostgreSQL | `your-db.render.com` |
| `DB_PORT` | Puerto de PostgreSQL | `5432` |
| `DB_NAME` | Nombre de la base de datos | `auth_db` |
| `DB_USERNAME` | Usuario de la base de datos | `auth_user` |
| `DB_PASSWORD` | Contraseña de la base de datos (Secreto) | `********` |
| `JWT_SECRET_KEY` | Clave secreta para JWT (Secreto) | `your-base64-secret-key` |

#### Variables Opcionales (ya configuradas en render.yaml)

| Variable | Valor por Defecto | Descripción |
|----------|-------------------|-------------|
| `SERVER_PORT` | `8081` | Puerto del servidor |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Estrategia de actualización de BD |
| `SPRING_JPA_SHOW_SQL` | `false` | Mostrar SQL en logs |

### Paso 3: Configurar Base de Datos

#### Opción A: Usar PostgreSQL de Render (recomendado para desarrollo)

El archivo `render.yaml` ya incluye la configuración de una base de datos PostgreSQL. Render creará automáticamente:
- Una instancia de PostgreSQL
- Variables de entorno para conectarse a ella

#### Opción B: Usar Base de Datos Externa (Supabase, etc.)

Si ya tienes una base de datos externa:
1. Comenta o elimina la sección `databases:` del archivo `render.yaml`
2. Configura manualmente las variables de entorno `DB_*` en el dashboard

### Paso 4: Desplegar

1. Haz clic en "Apply" para crear los servicios
2. Render comenzará a construir la imagen Docker
3. Una vez completado, el servicio estará disponible en una URL como:
   ```
   https://auth-service-xxxx.onrender.com
   ```

## Opción 2: Despliegue Manual

### Paso 1: Crear Web Service

1. En Render Dashboard, haz clic en "New +" → "Web Service"
2. Conecta tu repositorio
3. Configura:
   - **Environment**: Docker
   - **Region**: Oregon (o tu preferencia)
   - **Plan**: Starter
   - **Dockerfile Path**: `./Dockerfile`
   - **Docker Context**: `.`

### Paso 2: Configurar Variables de Entorno

Agrega todas las variables listadas en la tabla anterior.

### Paso 3: Configurar Health Check

- **Health Check Path**: `/actuator/health`

### Paso 4: Desplegar

Haz clic en "Create Web Service"

## Verificación del Despliegue

Una vez desplegado, puedes verificar que el servicio funciona correctamente:

### 1. Health Check
```bash
curl https://tu-servicio.onrender.com/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP"
}
```

### 2. Documentación API (Swagger)
Visita: `https://tu-servicio.onrender.com/swagger-ui.html`

### 3. Endpoint de Registro
```bash
curl -X POST https://tu-servicio.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!"
  }'
```

## Monitoreo y Logs

### Ver Logs en Tiempo Real
1. Ve a tu servicio en el dashboard de Render
2. Haz clic en la pestaña "Logs"
3. Los logs se actualizarán en tiempo real

### Métricas
Render proporciona métricas básicas de:
- CPU
- Memoria
- Tráfico de red
- Tiempos de respuesta

## Troubleshooting

### El servicio no inicia

1. **Verifica los logs**: Busca errores en la pestaña de Logs
2. **Verifica las variables de entorno**: Asegúrate de que todas las variables requeridas están configuradas
3. **Verifica la conexión a la BD**: El error más común es credenciales incorrectas

### La aplicación se reinicia constantemente

1. **Health check fallando**: Verifica que `/actuator/health` responde correctamente
2. **Out of Memory**: Considera aumentar el plan o ajustar `JAVA_OPTS`
3. **Timeout en inicio**: Spring Boot puede tardar en iniciar, ajusta el timeout del health check

### Rendimiento lento

1. **Plan insuficiente**: Considera actualizar de Starter a Standard
2. **Optimizar JVM**: Ajusta los parámetros en `JAVA_OPTS`
3. **Optimizar consultas**: Revisa queries SQL lentas en los logs

## Optimizaciones de Producción

### 1. Seguridad

- **NUNCA** incluyas secretos en el código
- Usa la función de "Secret Files" de Render para archivos sensibles
- Mantén actualizadas las dependencias de seguridad

### 2. Performance

```yaml
# En render.yaml, ajusta JAVA_OPTS:
- key: JAVA_OPTS
  value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -Xss512k"
```

### 3. Base de Datos

- Para producción, usa un plan de BD más robusto
- Configura backups automáticos
- Considera conexión pooling

### 4. Logs

En producción, desactiva logs verbosos:
```yaml
- key: SPRING_JPA_SHOW_SQL
  value: false
```

## Costos Estimados

| Plan | Precio Mensual | Especificaciones |
|------|----------------|------------------|
| Starter | $7/mes | 512 MB RAM, 0.5 CPU |
| Standard | $25/mes | 2 GB RAM, 1 CPU |
| Pro | $85/mes | 4 GB RAM, 2 CPU |

**Base de Datos PostgreSQL**:
- Starter: $7/mes (256 MB RAM)
- Standard: $20/mes (1 GB RAM)

## Recursos Adicionales

- [Documentación de Render](https://render.com/docs)
- [Render Blueprints](https://render.com/docs/infrastructure-as-code)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot en Producción](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)

## Contacto y Soporte

Para problemas específicos del microservicio, consulta el README principal del proyecto.
