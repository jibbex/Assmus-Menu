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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * The class ConsoleMenu is extended by a child class with the methods of the individual options.
 * The annotation @MenuOption defines the name in the menu and which string must be entered for
 * the selection. You can now create an instance of your derived class in the entry point method
 * of your Java application and call the object's run method.
 *
 * <code>
 *     class App extends ConsoleMenu {
 *         App(String title) {
 *             super(title);
 *         }
 *
 *         @MenuOption(name = "Help", pattern = "h")
 *         public void help() {
 *             System.out.println("Author: Manfred Michaelis <mm@michm.de>");
 *         }
 *
 *         @MenuOption(name = "Quit", pattern = "q")
 *         public void help(Object[] args) {
 *              args[0] = false;
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

    /**
     * The constructor needs a title as String
     *
     * @param title <String>
     */
    public AssmusMenu(String title) {
        this.title = title;
        this.options = new ArrayList<>();

        Class<? extends AssmusMenu> obj = this.getClass();
        Method[] methods = obj.getDeclaredMethods();

        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof MenuOption) {
                    String name = ((MenuOption) annotation).name();
                    String pattern = ((MenuOption) annotation).pattern();

                    add(new Option(name, pattern, method));
                }
            }
        }
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
        System.out.println("\n " + title + "\n " + getUnderline(title) + getUnderline(title) + getUnderline(title));

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
     */
    public void run() {
        InputStreamReader inStream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inStream);
        boolean run = true;

        try {
            while (run) {
                render();
                String pattern = reader.readLine();

                for (Option option : options) {
                    if (option.getPattern().equals(pattern)) {
                        /*
                            An array of Object with the main loops run
                            variable and the BufferedReader instance
                            or nothing will be passed to the invoked
                            method.

                         */
                        Object[] args = new Object[option.getParameterCount()];

                        for (int i = 0; i < option.getParameterCount(); i++) {
                            System.out.println(option.getParameterTypes()[i].getTypeName());
                            if (option.getParameterTypes()[i].getTypeName().equals("boolean")) {
                                args[i] = run;
                            } else if (option.getParameterTypes()[i].getTypeName().equals("java.io.BufferedReader")) {
                                args[i] = reader;
                            }
                        }
                        
                        if (option.getReturnType().getTypeName().equals("boolean")) {
                            // Reads back run variable
                            run = (boolean) option.invoke(this, args);
                        } else {
                            option.invoke(this, args);
                        }
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
