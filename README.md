# Assmus Menu `(⌐■_■)`
Generates from a set of annotated Methods a menu for your CLI application. Therefor the class `AssmusMenu` 
must be extended by a child class with the methods of the individual options. The annotation `@MenuOption` defines 
the name in the menu and which string must be entered for the selection. You can now create an instance of your 
derived class in the entry point method of your Java application and call the object's run method.

## Example
*App.java*
```java
class App extends AssmusMenu {
    App(String title) {
        super(title);
    }

    @MenuOption(name = "Help", pattern = "h")
    public void help() {
        // Clears the console output
        clear();
        System.out.println("Hello World");
        System.out.println("Hit any key to continue...");
        read(String.class); 
    }

    @MenuOption(name = "Quit", pattern = "q")
    public boolean quit() {
        clear();
        System.out.print("Do you want to exit? (y/n): ");
        String input = read(String.class);
        // If a boolean is returned, the run variable
        // of the main loop will be set to its value.
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
            // Starts the application.
            app.run();
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

| Return type | Passed Object                                                   |
|-------------|-----------------------------------------------------------------|
| T           | `<T> read(Class<?>)` - Returns input as instance of passed Type |
| void        | `clear()` - Clears console output                               |

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
        System.err.println("Your input couldn't recognized.");
    }
}
```

---

## `( •_•)>⌐■-■`