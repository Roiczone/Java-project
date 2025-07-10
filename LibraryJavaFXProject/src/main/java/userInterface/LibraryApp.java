package userInterface;

import database.Database;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import output.Output;

import java.time.LocalDate;

public class LibraryApp extends Application {
    private TextArea area = new TextArea();

    public static void main(String[] args) {
        Database.connect();
        Database.createTables();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Library Management System - JavaFX");

        TabPane tabPane = new TabPane();

        Tab bookTab = new Tab("Books", createBookPane());
        Tab memberTab = new Tab("Members", createMemberPane());
        Tab transactionTab = new Tab("Transactions", createTransactionPane());
        Tab updateTab = new Tab("Updates", createUpdatePane());

        tabPane.getTabs().addAll(bookTab, memberTab, transactionTab, updateTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(tabPane, area);

        area.setEditable(false);
       area.setWrapText(true);
        area.setPrefHeight(150);
        Output.outputArea = area;

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createBookPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField authorField = new TextField();
        authorField.setPromptText("Author");

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        Button addButton = new Button("Add Book");
        addButton.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            int qty = Integer.parseInt(quantityField.getText());
            Database.addBook(title, author, qty);
            output("Book " + title + " by " + author + " with quantity " + qty);
            titleField.clear();
            authorField.clear();
            quantityField.clear();
        });

        Button showBooksButton = new Button("Show All Books");
        showBooksButton.setOnAction(e ->{Output.clear(); Database.showBooks();});

        Button deleteButton = new Button("Delete Book by ID");
        TextField idField = new TextField();
        idField.setPromptText("Book ID");
        deleteButton.setOnAction(e -> {
            int id = Integer.parseInt(idField.getText());
            Database.deleteBook(id);
            output("Book deleted (ID: " + id + ")");
            idField.clear();
        });


        vbox.getChildren().addAll(titleField, authorField, quantityField, addButton,
                new Separator(), idField, deleteButton,
                new Separator(), showBooksButton);

        return vbox;
    }
    private VBox createMemberPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Member Name");
        TextField ageField = new TextField();
        ageField.setPromptText("Member Age");

        Button addMemberButton = new Button("Add Member");
        addMemberButton.setOnAction(e -> {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            Database.addMember(name, age);
            nameField.clear();
            ageField.clear();
        });

        Button showMembersButton = new Button("Show All Members");
        showMembersButton.setOnAction(e -> {Output.clear(); Database.showMembers();});

        Button deleteButton = new Button("Delete Member by ID");
        TextField idField = new TextField();
        idField.setPromptText("Member ID");
        deleteButton.setOnAction(e -> {
                    int id = Integer.parseInt(idField.getText());
                    Database.deleteMember(id);
                    output("Member deleted (ID: " + id + ")");
                    idField.clear();
        });

        vbox.getChildren().addAll(nameField, ageField, addMemberButton, new Separator(),idField, deleteButton, new Separator(), showMembersButton);
        return vbox;
    }


    private VBox createUpdatePane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label bookSectionLabel = new Label("Update Book Quantity");

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");

        TextField newQuantityField = new TextField();
        newQuantityField.setPromptText("New Quantity");

        Button updateBookBtn = new Button("Update Book Quantity");
        updateBookBtn.setOnAction(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText());
                int newQty = Integer.parseInt(newQuantityField.getText());
                Database.updateBookQuantity(bookId, newQty);
            } catch (NumberFormatException ex) {
                output("Invalid book ID or quantity.");
            }
            bookIdField.clear();
            newQuantityField.clear();
        });


        Label memberSectionLabel = new Label("Update Member Info");

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");

        TextField newNameField = new TextField();
        newNameField.setPromptText("New Name");

        TextField newAgeField = new TextField();
        newAgeField.setPromptText("New Age");

        Button updateMemberBtn = new Button("Update Member");
        updateMemberBtn.setOnAction(e -> {
            try {
                int memberId = Integer.parseInt(memberIdField.getText());
                String newName = newNameField.getText();
                int newAge = Integer.parseInt(newAgeField.getText());
                Database.updateMember(memberId, newName, newAge);
            } catch (NumberFormatException ex) {
                output("Invalid member ID or age.");
            }
            memberIdField.clear();
            newNameField.clear();
            newAgeField.clear();
        });

        vbox.getChildren().addAll(
                bookSectionLabel, bookIdField, newQuantityField, updateBookBtn,
                new Separator(),
                memberSectionLabel, memberIdField, newNameField, newAgeField, updateMemberBtn
        );

        return vbox;
    }


    private VBox createTransactionPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");

        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Book ID");

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due Date");

        Button borrowButton = new Button("Borrow Book");
        borrowButton.setOnAction(e -> {
            int memberId = Integer.parseInt(memberIdField.getText());
            int bookId = Integer.parseInt(bookIdField.getText());
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = dueDatePicker.getValue();
            boolean success = Database.borrowBook(memberId, bookId, borrowDate, dueDate);
            output(success ? "Book borrowed successfully." : "Borrow failed.");
            memberIdField.clear();
            bookIdField.clear();
            dueDatePicker.setValue(null);
        });

        Button returnButton = new Button("Return Book");
        returnButton.setOnAction(e -> {
            int memberId = Integer.parseInt(memberIdField.getText());
            int bookId = Integer.parseInt(bookIdField.getText());
            boolean success = Database.returnBook(memberId, bookId, LocalDate.now());
            output(success ? "Book returned successfully." : "Return failed.");
            memberIdField.clear();
            bookIdField.clear();
        });

        Button showTx = new Button("Show Transactions");
        showTx.setOnAction(e -> {Output.clear(); Database.showTransactions();});

        Button findTransactionById = new Button("Find Transaction by ID");
        TextField idField = new TextField();
        idField.setPromptText("Transaction ID");
        findTransactionById.setOnAction(e -> {
            int id = Integer.parseInt(idField.getText());
            Database.findTransactionById(id);
            output("ID: " + id );
            idField.clear();
        });

        vbox.getChildren().addAll(memberIdField, bookIdField, dueDatePicker, borrowButton, returnButton, new Separator(), idField, findTransactionById, new Separator(), showTx);
        return vbox;
    }

    private void output(String message) {
        Platform.runLater(() -> area.appendText(message + "\n"));
    }
}
