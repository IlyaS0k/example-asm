import org.junit.BeforeClass;
import org.junit.Test;
import ru.ilyasok.IRunTimeLoadModule;
import ru.ilyasok.MyClass;
import ru.ilyasok.classload.CustomClassLoader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class ASMTest {
    public static Class<?> clss = null;

    @BeforeClass
    public static void loadModule() throws Exception {

    }

    @Test
    public void MyTest() throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MyClass mc = new MyClass();
        System.out.println(mc.getClass().getClassLoader());
        CustomClassLoader customClassLoader = new CustomClassLoader();
        Class<?> clazz = customClassLoader.findClass("ru.ilyasok.RunTimeLoadModule", true);
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        IRunTimeLoadModule module = (IRunTimeLoadModule) constructor.newInstance();
        module.invoke();
    }


}
