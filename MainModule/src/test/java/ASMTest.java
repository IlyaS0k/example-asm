import org.junit.BeforeClass;
import org.junit.Test;
import ru.ilyasok.IDivisor;
import ru.ilyasok.MyClass;
import ru.ilyasok.asm.ByteCodeTryCatchWrapper;
import ru.ilyasok.asm.ITryCatchHandler;
import ru.ilyasok.classload.EditBytecodeClassLoader;

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
        EditBytecodeClassLoader loader = new EditBytecodeClassLoader(new ByteCodeTryCatchWrapper());
        Class<?> clazz = loader.editClass(
                "ru.ilyasok.Divisor",
                ArithmeticException.class,
                (r) ->  { System.out.println("Поймано исключение! :"  + r.getClass());},
                "divide",
                null

        );
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        IDivisor divisor = (IDivisor) constructor.newInstance();
        divisor.divide(5, 1);
        divisor.divide(100, 0);
    }


}
