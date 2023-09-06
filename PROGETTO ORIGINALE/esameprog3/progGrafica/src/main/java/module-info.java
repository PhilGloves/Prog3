module unito.prog3.proggrafica {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.kordamp.bootstrapfx.core;

  requires charm.glisten;
  requires de.jensd.fx.glyphs.fontawesome;
  requires de.jensd.fx.glyphs.commons;
  requires de.jensd.fx.glyphs.materialdesignicons;

  requires org.jetbrains.annotations;


  opens unito.prog3 to javafx.fxml;
  exports unito.prog3;
  exports unito.prog3.controller;
  opens unito.prog3.controller to javafx.fxml;
}