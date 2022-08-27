# Assmus Menu `(⌐■_■)`

Generates from a set of annotated Methods a menu for your CLI application. Therefor the class `AssmusMenu` must be extended by a child class with the methods of the individual options. The annotation `@MenuOption` defines  the name in the menu and which string must be entered for the selection. You can now create an instance of your derived class in the entry point method of your Java application and call the object's run method.

## Example

*App.java*

```java
class App extends AssmusMenu {
    App(String title) {
        super(title);
    }

    @MenuOption(name = "Help", pattern = "h")
    public void help() {        
        clear(); // Clears the console output.
        System.out.println("Hello World");
        System.out.println("Hit any key to continue...");
        read(String.class); 
    }

    @MenuOption(name = "Quit", pattern = "q")
    public boolean quit() {
        clear();
        System.out.print("Do you want to exit? (y/n): ");
        boolean exit = false;
        String input = read(String.class);
        // If a boolean is returned, the run variable of 
        // the main loop will be set to its inverse value.
        return input.equalsIgnoreCase("y");
    }
}
```

*Main.java*

```java
class Main {
    public static void main(String[] args) {
        try {
            App app = new App("MY COOL CLI APP");
            app.run(); // Starts the application.
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }
}
```

*Output*

```bash
 MY COOL CLI APP
 =============================================
   (h) Help
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
        clear();
        System.out.print("Name: ");
        String name = read(String.class);
        System.out.print("Age: ");
        int age = read(Integer.class);
        System.out.print("Height in cm: ");
        double height = read(Double.class);
        clear();
        System.out.println("Name: " + name + ", Age: " + age + ", height: " + height + " cm");
        read(String.class);
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
    public void onError() {
        clear();
        System.out.println("Your input couldn't recognized.");
        System.out.println("Hit any key to continue...");
        read(String.class);
    }
}
```

---

## `( •_•)>⌐■-■`