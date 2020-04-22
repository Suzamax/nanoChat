# nanoChat

Creado por Carlos y Sergio

## Cómo usar

Si usas IntelliJ IDEA o Eclipse es trivial.

## Docmuentación

Se editan los Markdown.

Para compilar se ejecuta:

```bash
pandoc <nombre_documento>.md -o <nombre_que_quieras_dar>.pdf --from markdown --template eisvogel --listings
```

Hay que instalar LaTeX, Pandoc y [esta plantilla](https://github.com/Wandmalfarbe/pandoc-latex-template).

Para Windows con MikTeX valdría, Pandoc se instala fácil con un instalador, y la plantilla tiene su propia guía.