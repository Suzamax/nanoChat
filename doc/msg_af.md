---
title: "Mensajes y autómatas"
author: [Carlos Cañellas Tovar, Sergio Raúl Rech Lizon]
date: "20 de abril de 2020"
subject: "Redes de Comunicaciones"
keywords: [Markdown, Example]
subtitle: "Formato de los mensajes y su funcionamiento y autómatas de cada aplicación"
lang: "es"
titlepage: true,
titlepage-text-color: "FFFFFF"
titlepage-rule-color: "360049"
titlepage-rule-height: 0
titlepage-background: "background.pdf"
page-background: "backgroundPage.pdf"
page-background-opacity: 0.1
output: pdf_document
...

# Formato de mensajes

## Mensajes binarios al directorio



## Mensajes Campo-valor

- NCNickMessage

```
operation:1\n
nick:<nick>\n // Siendo <nick> el nick elegido.
\n
```

- NCRoomMessage

```
operation:2\n
\n
```

- NCEnterMessage
```
operation:3\n
room:<room>\n // Siendo <room> la habitación.
\n
```
Se responde con la siguiente operación si se entra con éxito:

- NCInRoomMessage
```
operation:4\n
\n
```
- NCSendMessage
```
operation:5\n
message:<message>\n // Los saltos de línea in-message son %n
\n
```

- NCExitMessage
```
operation:6\n
\n
```

- NCInfoMessage
```
	operation:7\n
	\n
```
Se responde con lo siguiente:

- NCInfoReplyMessage
```
	operation:8\n
	topic:<topic>\n
	users:<lista de usuarios>\n
	\n
```

# Autómatas

## Autómata del cliente con el directorio

![Autómata de cliente a directorio](./cliente_dir.png)

## Autómata del servidor con el directorio

## Autómata del servidor para recibir peticiones del cliente y procesarlas

![Autómata de servidor a cliente](./servidor.png)

## Autómata del cliente para enviar peticiones al servidor y recibir respuestas

