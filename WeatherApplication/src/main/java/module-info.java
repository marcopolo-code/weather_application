module com.marcalbert {
    requires javafx.controls;
    requires java.desktop;
    requires javafx.swing;
    requires javafx.web;

    opens com.marcalbert;
    exports com.marcalbert;
}