# Console Menu `(⌐■_■)`
Generates from a bunch of annotated Methods a menu for your CLI application. Therefor the class ConsoleMenu 
must be extended by a child class with the methods of the individual options. The annotation @MenuOption defines 
the name in the menu and which string must be entered for the selection. You can now create an instance of your 
derived class in the entry point method of your Java application and call the object's run method.

## Example
*App.java*
```java
class App extends ConsoleMenu {
    App(String title) {
        super(title);
    }

    @MenuOption(title = "Help", pattern = "h")
    public void help() {
        // Clears the console output
        clear();
        System.out.println("Hello World");
        Thread.sleep(3000);
    }

    @MenuOption(title = "Quit", pattern = "q")
    public void help(Object[] args) {
        // Sets the run variable of the main loop to false.
        args[0] = false;
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

---

## `( •_•)>⌐■-■`