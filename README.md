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
        Thread.sleep(3000);
    }

    @MenuOption(name = "Quit", pattern = "q")
    public boolean quit() {
        // If a boolean is returned, the run variable
        // of the main loop will be set to its value.
        return false;
    }
}
```

*Main.java*
```java
class Main {
    public static void main(String[] args) {
        App app = new App("MY COOL CLI APP");
        // Starts the application.
        app.run();
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

## Method parameters
Following parameter will be passed to the called method if 
it declares a parameter of the particular type.

| Type           | Passed Object                   |
|----------------|---------------------------------|
| boolean        | run variable of main loop       |
| BufferedReader | An instance of a BufferedReader |

## Return types
The annotated method must have a return type of `void` or `boolean`.
If the type is `boolean`, the run variable of the main loop will be
set to the return value of the method.

---

## `( •_•)>⌐■-■`