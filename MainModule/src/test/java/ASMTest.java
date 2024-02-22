import org.junit.BeforeClass;
import ru.ilyasok.IRunTimeLoadModule;
import ru.ilyasok.asm.ModuleLoader;
import ru.ilyasok.asm.RuntimeByteCodeEditing;

import java.lang.reflect.Constructor;
import java.util.Scanner;


public class ASMTest {
    public static Class<?> clss = null;

    @BeforeClass
    public static void loadModule() throws Exception {
        ModuleLoader loader = new ModuleLoader(new RuntimeByteCodeEditing());
        Scanner scanner = new Scanner(System.in);
        clss = loader.findClass("ModuleImpl", "ru.ilyasok.RunTimeLoadModule");
        Constructor<?> constructor = clss.getConstructors()[0];
        IRunTimeLoadModule module = (IRunTimeLoadModule) constructor.newInstance();
    }


}
