Pour utiliser cette framework il faut que vous mettiez tous vos controller dans un même paquetage et mettre ceci dans votre web.xml

--web.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
    http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" version="2.4">
<display-name>Framework special</display-name> 
    <servlet>
        <servlet-name>FrontController</servlet-name> 
        <servlet-class>mg.itu.prom16.FrontController</servlet-class> 
        <init-param>
            <param-name>controllerPackage</param-name>
            <param-value><!-- chemin.vers.votre.paquetage.des.controllers --></param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>FrontController</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>