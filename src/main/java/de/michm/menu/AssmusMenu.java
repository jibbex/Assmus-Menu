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

import de.michm.scanner.PoggyScanner;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.AlreadyBoundException;
import java.util.ArrayList;
import java.util.Arrays;

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
public class AssmusMenu {
    final private String title;
    final private ArrayList<Option> options;
    final private Method onUnknownInput;
    final private PoggyScanner poggyScanner;

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
        this.poggyScanner = new PoggyScanner(System.in);
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
     * Creates an underline consisting of "=" for the passed
     * String.
     *
     * @param text <String>
     * @return <String>
     */
    private String getUnderline(String text) {
        final StringBuilder builder = new StringBuilder();
        text.chars().forEach(e -> builder.append("="));
        return builder.toString();
    }

    /**
     * Creates an underline consisting of "=" for the passed
     * String. Multiplies size x times of the original length.
     * @param text <String>
     * @param multiplier <int> length x times
     * @return <String>
     */
    private String getUnderline(String text, int multiplier) {
        String underline = getUnderline(text);
        for (int i = 0; i < multiplier; i++) {
            underline += underline;
        }
        return underline;
    }

    /**
     * Clears the console output.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void clear() throws IOException, InterruptedException {
        final String os = System.getProperty("os.name");

        if (os.contains("Windows")) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            Runtime.getRuntime().exec("clear");
        }
    }

    /**
     * Prints the resulting menu in std out.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void render() throws IOException, InterruptedException {
        clear();
        System.out.println("\n " + title + "\n " + getUnderline(title, 2));

        for (Option option : options) {
            String optText = "   " +
                    "(" +
                    option.getPattern() +
                    ") " +
                    option.getName();

            System.out.println(optText);
        }

        System.out.print("\n > ");
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
     * The run variable of the main loop, the BufferedReader instance,
     * both or nothing is passed to the called method.
     *
     * If the return type of the invoked method is boolean, the run
     * variable will be set to the return value.
     */
    public void run() {
        boolean run = true;

        try {
            while (run) {
                render();

                String pattern = read(String.class);

                if (pattern != null && onUnknownInput != null) {
                    onUnknownInput.invoke(this);
                } else {
                    for (Option option : options) {
                        if (option.getPattern().equals(pattern)) {
                            pattern = null;
                            Object[] args = new Object[]{};

                            if (option.getReturnType().getTypeName().equals("boolean")) {
                                // Reads back run variable
                                run = !((boolean) option.invoke(this, args));
                            } else {
                                option.invoke(this, args);
                            }
                        }
                    }
                }
            }
            poggyScanner.close();
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
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
    protected  <T> T read(Class<?> type) {
        T result = null;

        try {
            if (String.class.equals(type)) {
                result = (T) poggyScanner.nextLine();
            } else if (Integer.class.equals(type)) {
                result = (T) poggyScanner.nextInt();
            } else if (Long.class.equals(type)) {
                result = (T) poggyScanner.nextLong();
            } else if (Short.class.equals(type)) {
                result = (T) poggyScanner.nextShort();
            } else if (BigInteger.class.equals(type)) {
                result = (T) poggyScanner.nextBigInteger();
            } else if (Double.class.equals(type)) {
                result = (T) poggyScanner.nextDouble();
            } else if (BigDecimal.class.equals(type)) {
                result = (T) poggyScanner.nextBigDecimal();
            } else if (Boolean.class.equals(type)) {
                result = (T) poggyScanner.nextBoolean();
            } else if (Byte.class.equals(type)) {
                result = (T) poggyScanner.nextByte();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

        return result;
    }
}
