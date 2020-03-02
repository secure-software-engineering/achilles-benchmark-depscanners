package de.upb.achilles.generator.model;

public enum ByteCodeModification {
  ORG("O", "Original"),
  REPACKAGE("RP", "Repackage"),
  // re-writes the class files using ASM framework, and thus modifies the class file digest
  RECOMPILE("RC","Recompile"),
  // forces the jar to be recompiled from source
  FORCE_RECOMPILE("FRC", "Force Recompile");

  private final String code;
  private final String text;

  ByteCodeModification(String code, String text) {
    this.code = code;
    this.text = text;
  }

  public static ByteCodeModification getByCode(String gavModCode) {
    for (ByteCodeModification g : ByteCodeModification.values()) {
      if (g.code.equals(gavModCode)) {
        return g;
      }
    }
    return null;
  }

  public String getCode() {
    return code;
  }

  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return this.text;
  }
}
