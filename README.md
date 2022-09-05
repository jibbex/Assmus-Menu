# Assmus Menu `(⌐■_■)`

Generates from a set of annotated Methods a menu for your CLI application. Therefor the class `AssmusMenu` must be extended by a child class with the methods of the individual options. The annotation `@MenuOption` defines  the name in the menu and which string must be entered for the selection. You can now create an instance of your derived class in the entry point method of your Java application and call the object's run method.

## Example

*App.java*

```java
class App extends AssmusMenu {
    public App(String title) throws AlreadyBoundException {
        super(title);
    }

    @MenuOption(name = "Info", pattern = "i")
    void info() throws IOException, InterruptedException {
        clear(); // Clears the console output.
        System.out.println("Information");
        System.out.println("Hit return to continue...");
        read(String.class);
    }

    @MenuOption(name = "Quit", pattern = "q")
    boolean quit() throws IOException, InterruptedException {
        clear();
        String input = read(String.class, "Do you want to exit? (y/n): ");
        // If a bool is returned, the run variable of 
        // the main loop will be set to its inverse value.
        return input.equalsIgnoreCase("y");
    }

    @OnUnknownInput
    public void onError() throws IOException, InterruptedException {
        clear();
        System.out.println("Your input couldn't recognized.");
        System.out.println("Hit return to continue...");
        read(String.class);
    }
}
```

*Main.java*

```java
class Main {
    public static void main(String[] args) {
        try {
            App app = new App("Awesome App");
            app.run(); // Starts the application.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

*Output*

```bash
Awesome App
======================
   (i) Info
   (q) Quit

 >
```

## Methods

Following methods are implemented and can be called from the child class: 

| Return type | Passed Object                                                                                                                       |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------|
| T           | `<T> read(Class<?>, String fmt, Object ... args)` - Prints formatted String as prompt and returns input as instance of passed Type. |
| T           | `<T> read(Class<?>)` - Returns input as instance of passed Type.                                                                    |
| void        | `printException(Exception e)` - Prints the passed Exception object and its stack trace.                                             |
| void        | `clear()` - Clears console output.                                                                                                  |

**Supported types of `<T> read(Class<?>)` are currently:** 
* String
* Short
* Integer
* Long
* Double
* Float
* Byte
* Boolean
* BigInteger
* BigDecimal

### Example

```java
class App extends AssmusMenu {
    App(String title) {
        super(title);
    }

    @MenuOption(name = "Add", pattern = "a")
    public void add() {
        try {
            clear();

            String name = read(String.class, "Name: ");
            int age = read(Integer.class, "Age: ");
            double height = read(Double.class, "Height in cm: ");

            clear();

            System.out.println("name: " + name + ", age: " + age + ", height: " + height + " cm");
            read(String.class);
        } catch (Exception e) {
            printException(e);
            add();
        }
    }
}
```

## Return types

The annotated method must have a return type of `void` or `boolean`.
If the type is `boolean`, the run variable of the main loop will be
set to the inverted return value of the method.

## OnUnknownInput

With `@OnUnknownInput` annotation it is possible to define **1** method 
to handle unexpected user input.

```java
class App extends AssmusMenu {
    App(String title) {
        super(title);
    }

    @OnUnknownInput
    public void onError() throws IOException, InterruptedException {
        clear();
        System.out.println("Your input couldn't recognized.");
        System.out.println("Hit return to continue...");
        read(String.class);
    }
}
```

---

## `( •_•)>⌐■-■`