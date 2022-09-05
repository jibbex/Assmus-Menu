/*
 * Copyright (c) 2022. Manfred Michaelis <mm@michm.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.michm.menu;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * The class ConsoleMenu is extended by a child class with the methods of the individual options.
 * The annotation @MenuOption defines the name in the menu and which string must be entered for
 * the selection. You can now create an instance of your derived class in the entry point method
 * of your Java application and call the object's run method.
 *
 * <code>
 *     class App extends AssmusMenu {
 *         App(String title) {
 *             super(title);
 *         }
 *
 *         @MenuOption(name = "Info", pattern = "i")
 *         public void info() {
 *             System.out.println("Author: Manfred Michaelis <mm@michm.de>");
 *         }
 *
 *         @MenuOption(name = "Quit", pattern = "q")
 *         public boolean quit() {
 *              return false;
 *         }
 *     }
 *
 *     class Main {
 *         public static void main(String[] args) {
 *             App app = new App("MY COOL CLI APP");
 *             app.run();
 *         }
 *     }
 * </code>
 */
public class AssmusMenu implements AutoCloseable {
    final private String title;
    final private ArrayList<Option> options;
    final private Method onUnknownInput;
    final private BufferedReader reader;

    /**
     * The constructor needs a title as String
     *
     * @param title <String>
     */
    public AssmusMenu(String title) throws AlreadyBoundException {
        this.title = title;
        this.options = new ArrayList<>();

        Class<? extends AssmusMenu> obj = this.getClass();
        Method[] methods = obj.getDeclaredMethods();
        Method unknownInput = null;

        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof MenuOption) {
                    String name = ((MenuOption) annotation).name();
                    String pattern = ((MenuOption) annotation).pattern();

                    add(new Option(name, pattern, method));
                } else if (annotation instanceof OnUnknownInput) {
                    if (unknownInput == null) {
                        unknownInput = method;
                    } else {
                        throw new AlreadyBoundException(
                            "Only one method with @OnUnknownInput annotation is possible."
                        );
                    }
                }
            }
        }

        this.onUnknownInput = unknownInput;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Returns the size of the underlying List of options.
     *
     * @return List of options size <int>
     */
    public int size() {
        return options.size();
    }

    /**
     * Adds an Option object to the list.
     *
     * @param option <Option>
     */
    public void add(Option option) {
        options.add(option);
    }

    /**
     * Removes an Option object from the list. Returns
     * true on success and false on a failure.
     *
     * @param option <Option>
     * @return <boolean>
     */
    public boolean remove(Option option) {
        return options.remove(option);
    }

    /**
     * Removes the Option object on the position specified
     * by the passed index from the list. Returns true on
     * success and false on a failure.
     *
     * @param index <int>
     * @return <boolean>
     */
    public Option remove(int index) {
        return options.remove(index);
    }

    /**
     * Returns the Option object at the position of the passed
     * index from the list.
     *
     * @param index <int>
     * @return <Option>
     */
    public Option get(int index) {
        return options.get(index);
    }

    /**
     * Creates an underline consisting of "=" with the passed
     * length.
     *
     * @param length <int>
     * @return <String>
     */
    private String getUnderline(int length) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append("=");
        }
        return builder + "\n";
    }

    /**
     * Tries to clear stdout.
     */
    public static void clear() throws IOException, InterruptedException {
        final String os = System.getProperty("os.name");

        if (os.contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            new ProcessBuilder("bash", "clear").inheritIO().start().waitFor();
        }
    }

    /**
     * Prints the resulting menu in std out.
     *
     * @param underline String "=======..." as underline for title separation.
     */
    private void render(String underline) throws IOException, InterruptedException {
        clear();
        System.out.printf("\n%s\n%s", title, underline);

        for (Option option : options) {
            String optText = "   " +
                    "(" +
                    option.getPattern() +
                    ") " +
                    option.getName();

            System.out.println(optText);
        }

        System.out.flush();
    }

    /**
     * Prints the resulting menu in std out. Creates an underline with the
     * double length of title property.
     *
     * Deprecated:
     * Creating a new underline on each draw iteration is inefficiently.
     */
    @Deprecated
    private void render() throws IOException, InterruptedException {
        render(getUnderline(title.length() * 2));
    }

    /**
     * The run method is called from your `public static void main()` method.
     *
     * <code>
     *     class Main {
     *           static void public main(String[] args) {
     *               App app = new App("MY COOL CLI APP");
     *               app.run();
     *           }
     *       }
     * </code>
     *
     * If the return type of the invoked method is boolean, the run variable will
     * be set to the return value.
     */
    public void run() {
        boolean run = true;
        String underline = getUnderline(title.length() * 2);

        try {
            while (run) {
                render(underline);
                boolean foundFlag = false;
                String pattern = read(String.class, "\n > ");

                if (pattern != null && !pattern.isEmpty()) {
                    for (Option option : options) {
                        if (option.getPattern().equals(pattern)) {
                            pattern = null;
                            foundFlag = true;

                            Class<?>[] argTypes = option.getParameterTypes();
                            Object[] args = new Object[argTypes.length];

                            for (int i = 0; i < argTypes.length; i++) {
                                if (BufferedReader.class.equals(argTypes[i])) {
                                    args[i] = reader;
                                } else {
                                    throw new IllegalArgumentException("Unknown argument");
                                }
                            }

                            if (option.getReturnType().getTypeName().equals("boolean")) {
                                // Reads back run variable
                                run = !((boolean) option.invoke(this, args));
                            } else {
                                option.invoke(this, args);
                            }
                        }
                    }
                }

                if (!foundFlag) {
                    onUnknownInput.invoke(this);
                }
            }
        } catch (Exception e) {
            printException(e);
        }
    }

    /**
     * Reads the user input from stdin and returns a
     * value of type, which class was passed as
     * parameter. In case of a String the line
     * will be read.
     *
     * @param type Defines the return type.
     * @param fmt A formatted String prompt.
     * @param args Variables of the formatted String.
     * @return The stdin input as instance of passed [type].class.
     *
     * <code>
     *     String name = read(String.class, "name: ");
     *     Integer age = read(Integer.class, "age: ");
     *     Double price = read(Double.class, "price: ");
     * </code>
     */
    protected  <T> T read(@NotNull Class<T> type, String fmt, Object ... args) {
        String input;
        T result = null;

        if (fmt != null) {
            System.out.printf(fmt, args);
        }

        try {
            input = reader.readLine();

            if (String.class.equals(type)) {
                result = type.cast(input);
            } else if (Integer.class.equals(type)) {
                result = type.cast(Integer.valueOf(input));
            } else if (Long.class.equals(type)) {
                result = type.cast(Long.valueOf(input));
            } else if (Short.class.equals(type)) {
                result = type.cast(Short.valueOf(input));
            } else if (BigInteger.class.equals(type)) {
                result = type.cast(new BigInteger(input));
            } else if (Double.class.equals(type)) {
                result = type.cast(Double.valueOf(input));
            } else if (Float.class.equals(type)) {
                result = type.cast(Float.valueOf(input));
            } else if (BigDecimal.class.equals(type)) {
                result = type.cast(new BigDecimal(input));
            } else if (Boolean.class.equals(type)) {
                result = type.cast(Boolean.valueOf(input));
            } else if (Byte.class.equals(type)) {
                result = type.cast(Byte.valueOf(input));
            }
        } catch (Exception e) {
            printException(e);
        }

        return result;
    }

    /**
     * Reads the user input from stdin and returns a
     * value of type, which class was passed as
     * parameter. In case of a String the line
     * will be read.
     *
     * @param type Defines the return type.
     * @return The stdin input as instance of passed [type].class.
     *
     * <code>
     *     String name = read(String.class);
     *     Integer age = read(Integer.class);
     *     Double price = read(Double.class);
     * </code>
     */
    protected  <T> T read(@NotNull Class<T> type) {
        return read(type, null);
    }

    /**
     * Prints an Exception and its stack trace to stdout.
     * @param e Exception to be printed.
     */
    protected void printException(Exception e) {
        System.out.printf("Error: %s\n", e);
        e.printStackTrace();
        System.err.println("\n\tHit return to continue...");
        read(String.class);
    }

    /**
     * Closes the BufferedReader on disposing.
     */
    @Override
    public void close() throws Exception {
        reader.close();
    }
}
