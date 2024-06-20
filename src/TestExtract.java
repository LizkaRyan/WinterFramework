import java.lang.reflect.Method;
import java.sql.Date;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class TestExtract {

    public static void name(String firstname, String lastname, int years, Date birthDate) {
        // Code goes here
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException {
        Class<?> clazz = TestExtract.class;

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            System.out.println(method.getName());

            Paranamer paranamer = new AdaptiveParanamer();

            String[] parameterNames = paranamer.lookupParameterNames(method);

            for (String parameterName : parameterNames) {
                System.out.println("Parameter name: " + parameterName);
            }
        }
    }
}