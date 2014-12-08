package org.almibe.codearea;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class CodeMirrorArea implements CodeArea {
    private final WebView webView;
    private final WebEngine webEngine;
    private final ReadOnlyBooleanWrapper isInitializedProperty = new ReadOnlyBooleanWrapper(false);
    private final StringProperty editorContent = new SimpleStringProperty("");
    
    public CodeMirrorArea() {
        webView = new WebView();        
        webEngine = webView.getEngine();
    }

    @Override
    public void init() {
        final String html = CodeMirrorArea.class.getResource("html/editor.html").toExternalForm();
        webEngine.load(html);
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue == Worker.State.SUCCEEDED) {
                isInitializedProperty.setValue(true);
                editorContent.addListener((textObservable, oldTextValue, newTextValue) -> {
                    fetchEditor().call("setOption","value", newTextValue);
                });
                fetchEditor().call("setOption","value", editorContent.getValue());
            }
        });
    }

    @Override
    public ReadOnlyBooleanProperty isInitializedProperty() {
        return isInitializedProperty.getReadOnlyProperty();
    }

    @Override
    public Parent getWidget() {
        return this.webView;
    }

    @Override
    public boolean isReadOnly() {
        return (Boolean) fetchEditor().call("getOption","readOnly");
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        fetchEditor().call("setOption", "readOnly", readOnly);
    }

    @Override
    public JSObject fetchEditor() {
        Object editor = webEngine.executeScript("codeMirror;");
        if(editor instanceof JSObject) {
            return (JSObject) editor;
        }
        throw new IllegalStateException("CodeMirror not loaded.");
    }

    @Override
    public void setMode(String mode) {
        webEngine.executeScript("setMode('" + mode + "')");
    }

    @Override
    public void includeJSFile(String filePath, Runnable runnable) {
        //TODO implement
        throw new RuntimeException("includeJSFile is not implemented");
    }

    @Override
    public String getTheme() {
        return (String) fetchEditor().call("getOption", "theme");
    }

    @Override
    public void setTheme(String theme) {
        webEngine.executeScript("setTheme('" + theme + "')");
    }

    @Override
    public String getMode() {
        return (String) fetchEditor().call("getOption", "mode");
    }

    @Override
    public StringProperty contentProperty() {
        return this.editorContent;
    }
}