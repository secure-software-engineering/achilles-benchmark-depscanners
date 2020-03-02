package de.upb.achilles.generator.model;

public enum GAVModification {
  ORG("O", "Original"),
  MOD("M", "Modified"),
  RANDOM("R", "Random");

  private final String code;
  private final String text;

  GAVModification(String code, String text) {
    this.code = code;
    this.text = text;
  }

  public static GAVModification getByCode(String gavModCode) {
    for (GAVModification g : GAVModification.values()) {
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
