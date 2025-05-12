package dev.bast.foro.foro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

class OpenAPIConfigTest {

    private OpenAPIConfig openAPIConfig;

    @BeforeEach
    void setUp() {
        openAPIConfig = new OpenAPIConfig();
    }

    @Test
    void testOpenAPIBeanCreation() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getSecurity());
    }

    @Test
    void testOpenAPIInfo() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertEquals("Foro API", openAPI.getInfo().getTitle());
        assertEquals("API REST para la gestión de un foro con usuarios, temas y comentarios", 
                openAPI.getInfo().getDescription());
        assertEquals("v1.0.0", openAPI.getInfo().getVersion());
    }

    @Test
    void testOpenAPIContact() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("DUOC UC", openAPI.getInfo().getContact().getName());
        assertEquals("https://www.duoc.cl", openAPI.getInfo().getContact().getUrl());
        assertEquals("info@duoc.cl", openAPI.getInfo().getContact().getEmail());
    }

    @Test
    void testOpenAPILicense() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
        assertEquals("https://www.apache.org/licenses/LICENSE-2.0", openAPI.getInfo().getLicense().getUrl());
    }

    @Test
    void testSecurityConfiguration() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        
        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        assertTrue(securityRequirement.containsKey("Bearer Authentication"));
    }

    @Test
    void testSecurityScheme() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("Bearer Authentication"));
        
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertNotNull(securityScheme);
        assertEquals("Bearer Authentication", securityScheme.getName());
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
        assertEquals("Ingrese el token JWT obtenido en la autenticación", securityScheme.getDescription());
    }

    @Test
    void testServers() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        assertNotNull(openAPI.getServers());
        assertFalse(openAPI.getServers().isEmpty());
        assertEquals("http://localhost:8080", openAPI.getServers().get(0).getUrl());
        assertEquals("Local Development Server", openAPI.getServers().get(0).getDescription());
    }

    @Test
    void testConfigurationAnnotation() {
        Class<OpenAPIConfig> clazz = OpenAPIConfig.class;
        
        assertTrue(clazz.isAnnotationPresent(Configuration.class));
    }

    @Test
    void testSecuritySchemeNameConstant() {
        // Using reflection to access the private static final field
        try {
            java.lang.reflect.Field field = OpenAPIConfig.class.getDeclaredField("SECURITY_SCHEME_NAME");
            field.setAccessible(true);
            String value = (String) field.get(null);
            assertEquals("Bearer Authentication", value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not access SECURITY_SCHEME_NAME field");
        }
    }

    @Test
    void testBeanMethodAnnotation() {
        try {
            java.lang.reflect.Method method = OpenAPIConfig.class.getMethod("openAPI");
            assertTrue(method.isAnnotationPresent(org.springframework.context.annotation.Bean.class));
        } catch (NoSuchMethodException e) {
            fail("openAPI method not found");
        }
    }

    @Test
    void testOpenAPIStructure() {
        OpenAPI openAPI = openAPIConfig.openAPI();
        
        // Verify that all main components are properly initialized
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getInfo().getTitle());
        assertNotNull(openAPI.getInfo().getDescription());
        assertNotNull(openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getContact());
        assertNotNull(openAPI.getInfo().getLicense());
        assertNotNull(openAPI.getSecurity());
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertNotNull(openAPI.getServers());
    }

    @Test
    void testMultipleInvocations() {
        // Test that multiple invocations create new instances
        OpenAPI openAPI1 = openAPIConfig.openAPI();
        OpenAPI openAPI2 = openAPIConfig.openAPI();
        
        assertNotSame(openAPI1, openAPI2);
        assertEquals(openAPI1.getInfo().getTitle(), openAPI2.getInfo().getTitle());
    }
}
