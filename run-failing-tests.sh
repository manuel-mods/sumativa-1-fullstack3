#!/bin/bash

# Limpia los archivos compilados
./gradlew clean

# Tests CommentControllerTest
echo "Ejecutando CommentControllerTest.banComment_WithValidId_ShouldReturnNoContent"
./gradlew test --tests "dev.bast.foro.foros.controller.CommentControllerTest.banComment_WithValidId_ShouldReturnNoContent"

# Tests TopicControllerTest
echo "Ejecutando TopicControllerTest.banTopic_WithValidId_ShouldReturnNoContent"
./gradlew test --tests "dev.bast.foro.foros.controller.TopicControllerTest.banTopic_WithValidId_ShouldReturnNoContent"

# Tests AuthControllerTest
echo "Ejecutando AuthControllerTest.registerUser_*"
./gradlew test --tests "dev.bast.foro.usuarios.controller.AuthControllerTest.registerUser_WithExistingEmail_ShouldReturnBadRequest"
./gradlew test --tests "dev.bast.foro.usuarios.controller.AuthControllerTest.registerUser_WithExistingUsername_ShouldReturnBadRequest"
./gradlew test --tests "dev.bast.foro.usuarios.controller.AuthControllerTest.registerUser_WithInvalidRole_ShouldReturnBadRequest"
./gradlew test --tests "dev.bast.foro.usuarios.controller.AuthControllerTest.registerUser_WithValidData_ShouldReturnSuccessMessage"

# Tests UserControllerTest
echo "Ejecutando UserControllerTest"
./gradlew test --tests "dev.bast.foro.usuarios.controller.UserControllerTest.deleteUser_WhenAdmin_ShouldReturnNoContent"
./gradlew test --tests "dev.bast.foro.usuarios.controller.UserControllerTest.getAllUsers_WhenAdmin_ShouldReturnUsersList"
./gradlew test --tests "dev.bast.foro.usuarios.controller.UserControllerTest.getUserById_WhenAdmin_ShouldReturnUser"
./gradlew test --tests "dev.bast.foro.usuarios.controller.UserControllerTest.updateUser_WhenAdmin_ShouldUpdateAndReturnUser"

# Tests AuthServiceTest
echo "Ejecutando AuthServiceTest"
./gradlew test --tests "dev.bast.foro.usuarios.service.AuthServiceTest.registerUser_WithInvalidRole_ShouldThrowException"
./gradlew test --tests "dev.bast.foro.usuarios.service.AuthServiceTest.registerUser_WithValidDataAndSpecifiedRoles_ShouldCreateUserWithGivenRoles"

# Tests UserServiceTest
echo "Ejecutando UserServiceTest"
./gradlew test --tests "dev.bast.foro.usuarios.service.UserServiceTest.deleteUser_WithInvalidId_ShouldThrowException"
./gradlew test --tests "dev.bast.foro.usuarios.service.UserServiceTest.updateUser_WithInvalidRole_ShouldThrowException"
./gradlew test --tests "dev.bast.foro.usuarios.service.UserServiceTest.deleteUser_WithValidId_ShouldDeleteUser"

echo "Pruebas completadas. Revisa los resultados en:"
echo "build/reports/tests/test/index.html"
