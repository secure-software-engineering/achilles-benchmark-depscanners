package de.upb.achilles.generator.model;

import java.util.EventListener;
import java.util.EventObject;

public interface DetailModelListener extends EventListener {
  void advertisement(DetailChangeEvent e);

  class DetailChangeEvent extends EventObject {
    private final String fileName;

    public boolean isNewIncludeValue() {
      return newIncludeValue;
    }

    private final boolean newIncludeValue;

    public DetailChangeEvent(TestFixtureModel source, String fileName, boolean newIncludeValue) {
      super(source);
      this.fileName = fileName;
      this.newIncludeValue = newIncludeValue;
    }

    public String getFileName() {
      return fileName;
    }
  }
}
