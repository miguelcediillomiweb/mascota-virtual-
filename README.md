# Mascota Virtual

Mascota autónoma inspirada en las mascotas clásicas de escritorio de Windows.

## Web
Abre `web/index.html` o publica la carpeta `web/` en GitHub Pages.

## Android
Proyecto nativo Android que muestra la misma mascota web en una ventana superpuesta. Abre `android/` en Android Studio o descarga el APK generado por GitHub Actions.

## Comportamientos
Pasear, correr, saltar, dormir, despertar al tocar, comer pasto, beber de un charco, hacer pipí de forma caricaturesca, colgarse de bordes, resbalar, caerse, desaparecer/reaparecer y viajar en cohete a la luna.

No utiliza IA, servidores, cuentas ni sonidos. Todo funciona localmente.


## Widget web independiente
La mascota puede insertarse en otras páginas con:

```html
<script src="https://miguelcediillomi.github.io/mascota-virtual-/web/mascota-widget.js" defer></script>
```

El archivo `web/mascota-widget.js` crea una mascota flotante autónoma, sin sonidos ni servicios externos. Es una primera implementación del componente reutilizable.


## Firma animada del creador
La mascota realiza de forma ocasional un recorrido por la parte inferior de la pantalla, alternando aleatoriamente izquierda-derecha y derecha-izquierda. Mientras corre, despliega una banderola con el texto **Creado por Miguel Cedillo** y al terminar desaparece. En web se reutiliza el SVG del personaje principal. En Android se muestra mediante una capa superpuesta no táctil, independiente de la mascota.


## Interacción segura y física al soltar
- En la web, la capa exterior de la mascota no captura clics: solo las partes pintadas del SVG reciben el gesto. Se puede arrastrar con ratón o dedo; al soltar cae con gravedad, conserva parte del impulso y rebota hasta detenerse.
- En Android, la ventana de la mascota usa `FLAG_NOT_TOUCHABLE` de forma predeterminada para que los toques atraviesen la mascota y lleguen a WhatsApp, botones de llamada y demás aplicaciones.
- Para moverla manualmente, abre Mascota Virtual y pulsa **“4 · Agarrar y mover mascota (15 segundos)”**. Durante ese modo temporal la pequeña ventana de la mascota sí recibe el gesto; al terminar vuelve automáticamente al modo transparente a los toques. Evita activar ese modo mientras necesites tocar un control que quede justo debajo de la mascota.
- La banderola del creador permanece en una capa no táctil.
