package output;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
//import userInterface.LibraryApp;

public class Output {
    public static TextArea outputArea;

    public static void println(String message) {
        if (outputArea != null) {
            Platform.runLater(() -> outputArea.appendText(message + "\n"));
        } else {
            System.out.println(message); // fallback
        }
    }

    public static void clear() {
        if (outputArea != null) {
            Platform.runLater(() -> outputArea.clear());
        }
    }
}
