# WinterFramework
WinterFramework is an MVC framework used to develop robust and well-structured web applications. The model relies on separation of concerns to facilitate application development, maintenance, and scalability.

## I - Installation
1 - Download the winter-framework.jar file and add it to the project libraries
2 - Map all url("/") to the mg.itu.prom16.FrontController class in the web.xml file.
3 - Don't forget to set an init parameter to precise which package of your project should be scanned by the winter-framework for Controllers
```web.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
    http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd" version="2.4">
<display-name>Special Framework</display-name> 
    <servlet>
        <servlet-name>FrontController</servlet-name> 
        <servlet-class>mg.itu.prom16.FrontController</servlet-class> 
        <init-param>
            <param-name>controllerPackage</param-name>
            <param-value>path.to.your.controllers.package</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>FrontController</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
</web-app>
```
In the example above, the controllerPackage init parameter is set to path.to.your.controllers.package, which is the package that the winter-framework will scan for Controllers. You should replace this value with the package of your project that contains your Controllers.

## II - Usage
### 1 - Controller and endpoints
a) Controller and endpoint creation
Here is an example:
```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {

    @Get("/greet")
    public ModelAndView greet(){
        ModelAndView modelAndView=new ModelAndView("greet.jsp");
        modelAndView.addObject("message","Hello world!");
        return modelAndView;
    }

    @Post("/endPoint")
    public String endPoint(){
        return "endPoint";
    }

    public void notAnEndpoint(){
        
    }
}
```
#### Explanation
1. `@Controller(mapping = "/test")`:
    - Indicates that the class is a controller and the mapping = "/test" Specifies that all methods within this controller will have /test as the prefix in their URL. By default this prefix is "".
2. `@Get`, `@Post`:
    - These annotations define the specific HTTP methods and sub-paths for each endpoint.

#### Run and Access the Endpoints
1. GET /test/greet
   - URL: http://localhost:8080/test/greet

2. POST /test/endPoint
   - URL: http://localhost:8080/test/endPoint

NB: 
   - The framework only supports GET and POST verbs for now

If you want to redirect, create a method that returns a String that starts with `redirect:` then the path that you want

Here is an example:
```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {
    @Post
    public String greet(){
        return "redirect:/welcome";
    }
}
```
We redirect here to /welcome

#### Warning
- Don't assign a URL and verb pair to more than one method.
- Controller's method should only return a String or a ModelAndView

b) binding
Use `@Param` to extract query parameters from the URL.
Example:

```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {

   @Get("/greet")
   public ModelAndView greet(@Param(name = "firstName")String firstName,@Param(name = "lastName")String lastName) {
      return "Hello "+firstName+" "+lastName;
   }
}
```
- URL: http://localhost:8080/test/greet?firstName=John&lastName=Doe
- Result: `Hello John Doe`

It also works on class
Example:
```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {

   @PostMapping("/user")
   public String createUser(@Param(name="user") User user) {
      return "Hello "+user.getFirstName()+" "+user.getLastName();
   }
}
```
User Class:
```java
public class User {
    private String firstName;
    private String lastName;

    // Getters and Setters
}
```
NB:
   - Parameter binding only supports primitive class, Date or Object having fields of the precedent type.
   - If no binding can be applied then the parameter will be set to a string.
   - Object's class must contain an empty constructor

c) Request file binding
Example:

```java
import java.nio.file.Files;
import java.nio.file.Paths;
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.parameter.WinterFile;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {
   @Post("/session")
   public String insertForm(@WinterFile(name = "cin") Part photo) {
      String fileName = Paths.get(photo.getSubmittedFileName()).getFileName().toString();
      // Sauvegarde du fichier

      String uploadPath = "/var/html/www/assets/file" + fileName;
      Files.copy(photo.getInputStream(), Paths.get(uploadPath));
      return "Fichier uploadé avec succès : ";

   }
}
```
### 2 - Rest API
If you want to make a REST controller, annotate the class with the @RestController annotation. The return value of all its method will be sent as a JSON response. If the return value is an instance of ModelAndView then the view will be ignored and its data attribute will be the response body instead.
```java
import java.sql.Timestamp;
import java.util.Date;

import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.type.RestController;
import model.Student;

@RestController(mapping = "etudiant")
public class StudentController {
    @Get("/testApi")
    public Student getStudent(){
        return new Student("john","Doe");
    }
}
```
You can also annotate a single end point in a controller

```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.method.RestMethod;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {

   @RestMethod
   @PostMapping("/user")
   public String createUser(@Param(name = "user") User user) {
      return user;
   }
}
```

### 3 - Session
To use the session, you can add a field of type Session in your controller and put it in the constructor of the controller. It will be automatically detected and injected by the winter framework. The WinterSession class contains 3 generic methods:

void add(String key,Object value)
Object get(String key)
void remove(String key)
Here is a use case example:
```java
import mg.itu.prom16.Session;
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.method.RestMethod;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {
   Session session;

   public TestController(Session session) {
      this.session = session;
   }

   /**
    * Handles the login form submission
    * @param email the email
    * @param password the password
    * @return the view
    */
   @Post
   public ModelAndView login(@Param("email") String email, @Param("password") String password) {
      session.add("email", email);
      session.add("password", password);

      return new ModelAndView("home.jsp");
   }

   @Get("/my-info")
   public ModelAndView myInfo() {
      ModelAndView modelAndView = new ModelAndView("my-info.jsp");
      modelAndView.addObject("email", session.get("email"));
      modelAndView.addObject("password", session.get("password"));
      return modelAndView;
   }
}
```
Here is another use case example:
```java
import mg.itu.prom16.Session;
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.method.Post;
import mg.itu.prom16.winter.annotation.method.RestMethod;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller(mapping = "/test")
public class TestController {
   /**
    * Handles the login form submission
    * @param email the email
    * @param password the password
    * @return the view
    */
   @Post
   public ModelAndView login(@Param("email") String email, @Param("password") String password) {
      session.add("email", email);
      session.add("password", password);

      return new ModelAndView("home.jsp");
   }

   @Get("/my-info")
   public ModelAndView myInfo(Session session) {
      ModelAndView modelAndView = new ModelAndView("my-info.jsp");
      modelAndView.addObject("email", session.get("email"));
      modelAndView.addObject("password", session.get("password"));
      return modelAndView;
   }
}
```

### 4 - Parameters validation
- You can validate the parameters and their fields using the following annotations:
    - `RangeInt(min,max,field)`
    - `Required`

   Note that you don't have to specify the field value. You have to specify with the `@IfNotValidated` annotation where the user should be redirected in case a validation error occurs

Controller:

```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.method.Get;
import mg.itu.prom16.winter.annotation.parameter.Param;
import mg.itu.prom16.winter.annotation.type.Controller;
import mg.itu.prom16.winter.validation.annotation.IfNotValidated;

@Controller
public class TestController {

    @Get("/form")
    public ModelAndView form() {
        return new ModelAndView("form.jsp");
    }

    @Get("/employee")
    @IfNotValidated("/form")
    public ModelAndView employeeGet(@Param("employee") Employee emp) {
        ModelAndView modelAndView = new ModelAndView("employee.jsp");
        modelAndView.addObject("emp", emp);
        modelAndView.addObject("age", age);

        return modelAndView;
    }

}
```

Employee class:
```java
import mg.itu.prom16.winter.validation.annotation.RangeInt;
import mg.itu.prom16.winter.validation.annotation.Required;

public class Employee {
   @Required
   private String firstname;

   private String lastname;

   @RangeInt(min=0,field="age")
   private int age;

   public Employee() {
   }

   // ...
}
```
You can also create your own annotation validator

Here is how you make it:
- Step 1) Create the annotation and Specify the class validator with the `@PointerValidator` 
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import mg.itu.prom16.winter.validation.generic.annotation.PointerValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@PointerValidator(NotBlankValidator.class)
public @interface NotBlank {
    String message();
}
```
- Step 2) Create a class that extends `CustomValidator<T>` where T is the annotation that we created to validate which is `@NotBlank`.
```java
import mg.itu.prom16.winter.validation.annotation.RangeInt;
import mg.itu.prom16.winter.validation.exception.RangeIntException;
import mg.itu.prom16.winter.validation.generic.CustomValidator;
import mg.itu.prom16.winter.validation.generic.exception.ValidationException;

public class NotBlankValidator extends CustomValidator<NotBlank> {
    public RangeDoubleValidator(){
        super(NotBlank.class);
    }

    @Override
    public ValidationException validate(Object t, NotBlank annotation) {
        String value=(String)t;
        if(value.equals("")){
            return ValidationException(annotation.message());
        }
        return null;
    }
}
```
And now you can use your own validation annotation
```java
public class User {

    @NotBlank(message="The first name can't be blank")
    private String firstName;
    
    @NotBlank(message="The last name can't be blank")
    private String lastName;

    // Other fields, getters, and setters
}
```
### 5 - Authentification
In WinterFramewok, authentication is a mechanism for filtering HTTP requests entering your application.
It acts as a layer between the HTTP request and the response, where you can perform authentication before it reaches the controller.

Here is how you make it:
#### Step 1) Create a class that implements `Authenticator`. If your authentication needs session you put `Session` in the constructor
```java
import mg.itu.prom16.Session;
import mg.itu.prom16.winter.authentication.AuthenticationException;
import mg.itu.prom16.winter.authentication.Authenticator;

public class StudentAuthenticator  implements Authenticator{

    Session session;

    public StudentAuthenticator(Session session){
        this.session=session;
    }

    @Override
    public void authentificate() throws AuthenticationException {
        if((String)session.get("etudiant")==null){
            throw new AuthenticationException("Vous devez log d'abord");
        }
    }
}
```
#### Step 2) Put the annotation `@Authenticate` in the class controller or the method.
```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller("/student")
@Authenticate(StudentAuthenticator.class)
public class EtudiantController {
    @Get
    public ModelAndView getReport() {
        ModelAndView modelAndView = new ModelAndView("/views/report.jsp");
        return modelAndView;
    }
}
```
or
```java
import mg.itu.prom16.winter.ModelAndView;
import mg.itu.prom16.winter.annotation.type.Controller;

@Controller("/student")
public class EtudiantController {
    @Get
    @Authenticate(StudentAuthenticator.class)
    public ModelAndView getReport() {
        ModelAndView modelAndView = new ModelAndView("/views/report.jsp");
        return modelAndView;
    }
}
```
NB:

- If you put the annotation on both the class and the method, it will be the authentication of the method that will be used and not that of the class.
- If you put nothing on the value of `@Authenticate`, it will act like there's no authenticator
