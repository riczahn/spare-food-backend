package de.thb.sparefood.meals.service;

import java.io.File;

public interface StorageService {
  String saveFile(File file, String path);
  byte[] getFileAsBytes(String path);
  void deleteFile(String path);
}
