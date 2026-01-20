# Programa completo

## 1. Empleados
- Un empleado puede ser creado desde el menú principal
- Hay un listado de empleados que se puede ver desde el menú principal
- Cada empleado puede tener distintos salarios según el día

## 2. Salarios
- Son los que proveen la información de cuánto se pagará al final de un periodo
- Se puede ver la lista de salarios desde el menú principal
- Se puede crear un salario con el botón de crear desde la lista de salarios

## 3. Calendario 
- Es la vista donde se gestiona los días y horas trabajados de un empleado
- Se puede editar un empleado desde la vista del calendario
- Se pueden crear y eliminar periodos para ser pagados
- Las celdas del calendario dan retroalimentación visual según su estado

## 4. Guardado de la información
- La información se almacena en un JSON en una carpeta `AlbalatroApp\data` que está al nivel de la carpeta de usuario
- Los archivos se copian y se hacen los cambios sobre la copia, se tiene que guardar los cambios en el archivo original manualmente utilizando el botón de guardar en la barra superior
- Los archivos copia son eliminados al cerrar el programa

### 5. Importación y exportación
Hay opción para poder importar y exportar los JSON por si se necesita cambiar de computadora.

#### PDF
Se puede exportar la información en un pdf que tiene un formato de tabla similar al de la vista de calendario.
- No se puede importar datos desde un pdf