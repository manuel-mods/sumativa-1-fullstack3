#!/bin/bash

# Limpia y compila el proyecto
./gradlew clean build

# Ejecuta las pruebas con cobertura
./gradlew test jacocoTestReport

# Ejecuta SonarQube con los resultados de cobertura
./sonar.run

echo "Proceso completo. Los informes de cobertura están disponibles en:"
echo "- HTML: build/reports/jacoco/test/html/index.html"
echo "- XML (para SonarQube): build/reports/jacoco/test/jacocoTestReport.xml"
