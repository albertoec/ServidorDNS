# Servidor DNS



## Contributors
 - Moisés Carral Ortiz
 - Alberto Estévez Caldas

## Mejoras

 - Implementación de un segundo protocolo de transporte (TCP/UDP)
 - Implementación del mecanismo de caché simple (sólo muestra las respuestas consultadas, no hace uso de ningún tipo de información para acortar camino en futuras consultas)
 - Soporte para el escenario básico del punto 3 (CNAME, caso A y B)
 - Soporte para el escenario básico del punto 4
 - Soporte para RR MX, SOA, CNAME y TXY
 - Soporte para la resolución de consultas tipo AAAA desde la entrada estándar
