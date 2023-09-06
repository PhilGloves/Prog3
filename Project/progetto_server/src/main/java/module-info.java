module unito.prog3.progetto_server {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.json;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires org.jetbrains.annotations;


  opens  unito.prog3.file to com.fasterxml.jackson.annotation, com.fasterxml.jackson.core, com.fasterxml.jackson.databind, org.json;
  opens unito.prog3 to javafx.fxml;
  opens unito.prog3.controller to javafx.fxml;

  exports unito.prog3;
  exports unito.prog3.controller;
  exports unito.prog3.models to com.fasterxml.jackson.databind;
}