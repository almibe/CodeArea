package org.almibe.codeeditor.demo;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.almibe.codeeditor.CodeMirrorEditor;
import org.almibe.codeeditor.CodeEditor;

import java.nio.file.Paths;

public class Demo extends Application {
    private final CodeEditor codeEditor = new CodeMirrorEditor();
    private final CodeEditor codeEditorRight = new CodeMirrorEditor();

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(codeEditor.getWidget());
        VBox controls = new VBox();
        controls.getChildren().addAll(readOnlyControls(), modeControls(), themeControls(), contentControls());
        borderPane.setBottom(controls);
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        SplitPane sp = new SplitPane();
        sp.getItems().addAll(codeEditor.getWidget(), codeEditorRight.getWidget());
        codeEditor.init(Paths.get("src/test/resources/html/editor.html").toUri(),
                //() -> codeEditor.setContent("select * from T t where t.name = \"test\" limit 20;"),
                () -> codeEditor.setContent("select * from T t where t.name = 'test' limit 20;"),
                () -> codeEditor.setMode("text/x-sql"),
                () -> codeEditor.setTheme("xq-light"));

        codeEditorRight.init(Paths.get("src/test/resources/html/editor.html").toUri(),
                //() -> codeEditorRight.setContent("select * from T t where t.name = \"test\" limit 20;"),
                () -> codeEditorRight.setContent("select * from T t where t.name = 'test' limit 20;"),
                () -> codeEditorRight.setMode("text/x-sql"),
                () -> codeEditorRight.setTheme("xq-light"),
                () -> codeEditorRight.setReadOnly(true));
        borderPane.setCenter(sp);
        //codeEditor.setReadOnly(true); <-- this will crash
//        codeEditor.runWhenReady(() -> codeEditor.setReadOnly(true));
//        codeEditor.runWhenReady(() -> codeEditor.setReadOnly(false));
//        codeEditor.runWhenReady(() -> codeEditor.setContent("Test content?!?@!?@!?@!?@!?"));
        primaryStage.show();
    }

    private Parent readOnlyControls() {
        HBox box = new HBox();
        Button setReadOnly = new Button("Set Read Only");
        Button unsetReadOnly = new Button("Set Editable");
        setReadOnly.setOnAction(event -> codeEditor.setReadOnly(true));
        unsetReadOnly.setOnAction(event -> codeEditor.setReadOnly(false));
        box.getChildren().addAll(setReadOnly, unsetReadOnly);
        return box;
    }

    private Parent modeControls() {
        HBox box = new HBox();
        TextField modeInput = new TextField();
        Button button = new Button("Set Mode");
        box.getChildren().addAll(modeInput, button);
        button.setOnAction(event -> codeEditor.setMode(modeInput.getText()));
        return box;
    }

    private Parent themeControls() {
        HBox box = new HBox();
        Text themeText = new Text("Theme: ");
        TextField themeInput = new TextField();
        Text cssFileText = new Text("Css Files: ");
        TextField cssInput = new TextField();
        Button button = new Button("Set Theme");
        button.setOnAction(event -> {
            String[] files = null;
            if(!cssInput.getText().equals("")) {
                files = cssInput.getText().split(",");
            }
            codeEditor.setTheme(themeInput.getText(), files);
        });
        box.getChildren().addAll(themeText, themeInput, cssFileText, cssInput, button);
        return box;
    }

    private Parent contentControls() {
        HBox box = new HBox();
        TextArea content = new TextArea();
        Button setButton = new Button("Set Content");
        Button getButton = new Button("Get Content");
        setButton.setOnAction(event -> {
            codeEditor.setContent(content.getText());});
        getButton.setOnAction(event -> {content.setText(codeEditor.getContent());});
        box.getChildren().addAll(content, setButton, getButton);
        return box;
    }

    public static void main(String[] args) {
        Demo.launch();
    }
}
