module com.example.quan_li_nha_hang {
    // ── JavaFX ────────────────────────────────────────────────────────────
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;
    requires javafx.media;
    requires javafx.swing;

    // ── Java platform ─────────────────────────────────────────────────────
    requires java.desktop;
    requires java.sql;                         // kept for backup DAOs
    requires java.naming;                      // needed by Hibernate

    // ── JavaFX UI libs ────────────────────────────────────────────────────
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // ── QR / Camera ───────────────────────────────────────────────────────
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires webcam.capture;

    // ── Persistence ───────────────────────────────────────────────────────
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    // ── Jackson ───────────────────────────────────────────────────────────
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    // ── Lombok (compile-time only) ────────────────────────────────────────
    requires static lombok;

    // ── Opens: JavaFX controllers ─────────────────────────────────────────
    opens ui to javafx.fxml, javafx.graphics;
    opens ui.controllers to javafx.fxml;

    // ── Opens: Hibernate reflective access to entities ────────────────────
    opens core.entity to org.hibernate.orm.core, com.fasterxml.jackson.databind;
    opens core.dto   to com.fasterxml.jackson.databind;
    opens infrastructure.persistence     to org.hibernate.orm.core;
    opens infrastructure.persistence.impl to org.hibernate.orm.core;

    // ── Exports ───────────────────────────────────────────────────────────
    exports ui;
    exports core.dto;
    exports core.service;
    exports core.repository;
}
