export const environment = {
  apiBaseUrl: 'http://localhost:8080/paw-2023b-02/api',
  deployUrl: '/paw-2023b-02/static/browser/',
  version: '3.0',
};

/*
 * La SPA de localhost:4200 consume la API de localhost:8080 pero para tener ese servidor levantado con el
 * anidamiento correcto de /paw-2023b-02 la corremos como si fuese produccion.
 * Tomcat si agrega ese anidamiento, pero los plugins de intellij para correr no!
 * Como estamos corriendo en modo produccion en localhost:8080 tambien esta la SPA, pero esa deberia consumir la
 * API de produccion, o sea
 * apiBaseUrl: 'http://old-pawserver.it.itba.edu.ar/paw-2023b-02/api',
 * Para que nadie accidentalmente acceda a la SPA de localhost:8080 e interactue accidentalmente con la api de
 * produccion lo mantenemos en este valor
 * apiBaseUrl: 'http://localhost:8080/paw-2023b-02/api',
 */
