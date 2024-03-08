import org.junit.jupiter.api.*;
import ru.ilyasok.IDivisor;
import ru.ilyasok.IReplicateStringFunction;
import ru.ilyasok.asm.ByteCodeTryCatchWrapper;
import ru.ilyasok.asm.ITryCatchHandler;
import ru.ilyasok.classload.EditBytecodeClassLoader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExceptionMonitoringBytecodeEditingTest {

    public EditBytecodeClassLoader loader;

    @BeforeEach
    public void initLoader() {
        loader = new EditBytecodeClassLoader(new ByteCodeTryCatchWrapper());
    }

    public <EXCEPTION_TYPE extends Throwable> Constructor<?> getInstanceConstructor(
            String className,
            Class<EXCEPTION_TYPE> exceptionClass,
            ITryCatchHandler<EXCEPTION_TYPE> handler,
            String methodToBeWrappedName,
            String methodToBeWrappedDescriptor) {
        Class<?> clazz = loader.editClass(
                className,
                exceptionClass,
                handler,
                methodToBeWrappedName,
                methodToBeWrappedDescriptor
        );
        return clazz.getDeclaredConstructors()[0];
    }


    @Nested
    @DisplayName("checking the behavior of classes after changing the bytecode")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class LoadedClassesBehaviorTest {


        @Nested
        @DisplayName("'Divisor' class")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class DivisorClassTest {
            public final String className = "ru.ilyasok.Divisor";

            @Nested
            @DisplayName("'divide' method that performs integer division of two ints")
            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            class DivideMethodTest {
                public final String methodName = "divide";

                @Test
                @DisplayName("normal invocation")
                public void divideMethodNormalInvocationTest() throws Exception {
                    Constructor<?> constructor = getInstanceConstructor(className, ArithmeticException.class,
                            (e) -> {
                            },
                            methodName, null);
                    IDivisor divisor = (IDivisor) constructor.newInstance();
                    assertEquals(
                            divisor.divide(5, 1), 5,
                            "integer division of 5 by 1 equal 5"
                    );
                    assertEquals(
                            divisor.divide(100, 3), 33,
                            "integer division of 100 by 3 equal 33"
                    );
                }

                @Test
                @DisplayName("thrown exception invocation")
                public void divideMethodThrownExceptionInvocationTest() throws Exception {
                    AtomicReference<String> message = new AtomicReference<>("");
                    String messageOnError = "EXCEPTION WAS THROWN!";
                    Constructor<?> constructor = getInstanceConstructor(className, ArithmeticException.class,  //  ArithmeticException into catch section
                            (e) -> message.set(messageOnError),
                            methodName, null);
                    IDivisor divisor = (IDivisor) constructor.newInstance();
                    assertEquals(
                            message.get(),
                            "",
                            "before method invocation value of variable 'massage' must be empty"
                    );
                    assertThrows(
                            ArithmeticException.class,
                            () -> divisor.divide(10, 0),
                            "'ArithmeticException' will be thrown after dividing by zero "
                    );
                    assertEquals(
                            message.get(),
                            messageOnError,
                            "after the handler fires, the state of the variable 'message' must change"
                    );
                }

                @Test
                @DisplayName("thrown exception invocation with superclass exception in catch block")
                public void divideMethodThrownSuperclassExceptionInvocationTest() throws Exception {
                    AtomicReference<String> message = new AtomicReference<>("");
                    String messageOnError = "EXCEPTION WAS THROWN!";
                    Constructor<?> constructor = getInstanceConstructor(className, RuntimeException.class, //  RuntimeException (superclass of ArithmeticException) into catch section
                            (e) -> message.set(messageOnError),
                            methodName, null);
                    IDivisor divisor = (IDivisor) constructor.newInstance();
                    assertEquals(
                            message.get(),
                            "",
                            "before method invocation value of variable 'massage' must be empty"
                    );
                    assertThrows(
                            ArithmeticException.class,
                            () -> divisor.divide(10, 0),
                            "'ArithmeticException' will be thrown after dividing by zero "
                    );
                    assertEquals(
                            message.get(),
                            messageOnError,
                            "after the handler fires, the state of the variable 'message' must change"
                    );
                }
            }
        }

        @Nested
        @DisplayName("'ReplicateStringFunction' class")
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class RepeatStringFunctionClassTest {
            public final String className = "ru.ilyasok.ReplicateStringFunction";

            @Nested
            @DisplayName("checking 'apply' method that returns string concatenated with itself n times")
            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            class ApplyMethodTest {
                public final String methodName = "apply";

                @Test
                @DisplayName("normal invocation")
                public void applyMethodNormalInvocationTest() throws Exception {
                    Constructor<?> constructor = getInstanceConstructor(className, NullPointerException.class,
                            (e) -> {
                            },
                            methodName, null);
                    IReplicateStringFunction replicator =
                            (IReplicateStringFunction) constructor.newInstance(2);
                    assertEquals(
                            replicator.apply("aBc"), "aBcaBc",
                            "string 'aBc' concatenated with itself 2 times will be 'aBcaBcaBc'"
                    );
                    assertEquals(
                            replicator.apply(""), "",
                            "empty string does not change after concatenation"
                    );
                }

                @Test
                @DisplayName("thrown exception")
                public void applyMethodThrownExceptionInvocationTest() throws Exception {
                    List<Object> arrayList = new ArrayList<>();
                    Constructor<?> constructor = getInstanceConstructor(className, NullPointerException.class,
                            (e) -> arrayList.add(new Object()),
                            methodName, null);
                    IReplicateStringFunction replicator =
                            (IReplicateStringFunction) constructor.newInstance(1);
                    assertTrue(
                            arrayList.isEmpty(),
                            "we expect that list is empty before method invocation"
                    );
                    assertThrows(
                            NullPointerException.class,
                            () -> replicator.apply(null),
                            "'NullPointerException' will be thrown when attempting replicate null"
                    );
                    assertFalse(
                            arrayList.isEmpty(),
                            "we expect that list is not empty after exception handler fires"
                    );
                }

                @Test
                @DisplayName("thrown unhandled exception")
                public void applyMethodThrownUnhandledExceptionInvocationTest() throws Exception {
                    List<Object> arrayList = new ArrayList<>();
                    Constructor<?> constructor = getInstanceConstructor(className, IOException.class,
                            (e) -> arrayList.add(new Object()),
                            methodName, null);
                    IReplicateStringFunction replicator =
                            (IReplicateStringFunction) constructor.newInstance(1);
                    assertTrue(
                            arrayList.isEmpty(),
                            "we expect that list is empty before method invocation"
                    );
                    assertThrows(
                            NullPointerException.class,
                            () -> replicator.apply(null),
                            "'NullPointerException' will be thrown when attempting replicate null"
                    );
                    assertTrue(
                            arrayList.isEmpty(),
                            "handler does not fire because it handles 'IOException'," +
                                    " but 'NullPointerException' was thrown"
                    );
                }
            }
        }
    }

    @Nested
    @DisplayName("checking the behavior of different handler invocations in row")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DifferentHandlerInvocations{


        @Test
        @DisplayName("one handler does not overload other handlers")
        public synchronized void handlerDoesntOverloadOtherHandlersTest() throws Exception {
            AtomicReference<String> textException = new AtomicReference<>("");
            String onDivisorErrorText = "DivisorError";
            String onReplicateStringErrorText = "ReplicateStringError";
            Constructor<?> divisorConstructor = getInstanceConstructor("ru.ilyasok.Divisor", ArithmeticException.class,
                    (e) -> textException.set(onDivisorErrorText),
                    "divide", null);
            IDivisor divisor =
                    (IDivisor) divisorConstructor.newInstance();
            Constructor<?> replicateStringConstructor = getInstanceConstructor("ru.ilyasok.ReplicateStringFunction", NullPointerException.class,
                    (e) -> textException.set(onReplicateStringErrorText),
                    "apply", null);
            IReplicateStringFunction replicateString =
                    (IReplicateStringFunction) replicateStringConstructor.newInstance(1);
            assertTrue(
                    textException.get().isEmpty(),
                    "we expect that before invocation value of variable text is still empty"
            );
            assertThrows(
                    ArithmeticException.class,
                    () -> divisor.divide(-10, 0),
                    "'ArithmeticException' will be thrown while dividing by zero"
            );
            assertNotEquals(
                    onReplicateStringErrorText,
                    textException.get(),
                    "we expect that handler for 'Divisor' class does not overload " +
                            "by handler for 'ReplicateStringFunction' class that was loaded after it"
            );
            assertEquals(
                    onDivisorErrorText,
                    textException.get(),
                    "we expect that handler for 'Divisor' class works correct"
            );
        }
    }

}

