module sn.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;

    opens sn.socialnetwork to javafx.fxml;
    opens controller to javafx.fxml;
    exports sn.socialnetwork;
    exports controller;
    exports main.domain;
    exports main.repository;
    exports main.service;
}