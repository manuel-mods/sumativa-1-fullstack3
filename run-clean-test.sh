#!/bin/bash

# Limpia los archivos compilados
./gradlew clean

# Ejecuta los tests específicos sin compilar todos
./gradlew test --tests "dev.bast.foro.foros.controller.CommentControllerTest"
./gradlew test --tests "dev.bast.foro.foros.service.CommentServiceTest"
./gradlew test --tests "dev.bast.foro.foros.controller.TopicControllerTest"
./gradlew test --tests "dev.bast.foro.usuarios.security.jwt.JwtUtilsTest"
./gradlew test --tests "dev.bast.foro.usuarios.service.AuthServiceTest"
./gradlew test --tests "dev.bast.foro.usuarios.service.UserServiceTest"
./gradlew test --tests "dev.bast.foro.usuarios.controller.AuthControllerTest"
./gradlew test --tests "dev.bast.foro.usuarios.controller.UserControllerTest"

echo "Pruebas completadas. Revisa los resultados en:"
echo "build/reports/tests/test/index.html"
