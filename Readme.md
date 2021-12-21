# Telegram BOT

Bot de Telegram en JAVA y SQLite.

Sirve para consultar los precios de las gasolineras más cercanas a tu ubicación. Los datos son procedentes del portas de Datos Abiertos del gobierno de España. Se ha realizado como un ejercicio para ver las posibilidades de explotación de los datos abiertos a través de Telegram. Siéntase libre de usar el código como mejor vea. 

URL del Bot: https://t.me/SurtidorBot

Pasos de instalación:

 - clona el proyecto y lo abres en tu IDE favorito
 - Importa las librerías del pom.xml con Maven/Update proyect..
 - Crea tu propio bot con @Botfather y guarda el token
 - Crea el fichero configuracion.properties en la carpeta "res". En el defines las variables
      var.BotUsername= (valor devuelto por @Botfather)
      var.BotToken= (valor devuelto por @Botfather)
      var.userAdm = user_id del administrador o dueño del bot. Tendrá los comandos especias de creación de base de datos /crearbd y de consulta de veces de uso del bot /datos
 - Importar el certificado de https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroProducto en el almacen de certificados de tu servidor
 - Ejecuta la clase Main.java
 
 Text encoding -> UTF-8
 
 

