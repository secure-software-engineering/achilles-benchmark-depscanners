package de.upb.achilles.generator.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public class TestFixtureModelParent extends TestFixtureModel {
  private boolean childIsVulnerable;
  private boolean childContainsCode;

  public TestFixtureModelParent(TestFixture testFixture) {
    super(testFixture);
    this.editableProperty().setValue(true);
  }

  public boolean isChildContainsCode() {
    return childContainsCode;
  }

  public void setChildContainsCode(boolean childContainsCode) {
    this.childContainsCode = childContainsCode;
  }

  public boolean isChildIsVulnerable() {
    return childIsVulnerable;
  }

  public void setChildIsVulnerable(boolean childIsVulnerable) {
    this.childIsVulnerable = childIsVulnerable;
  }

  @Override
  public boolean isChangeGAV() {
    return false;
  }

  @Override
  public boolean isVulnerable() {
    return childIsVulnerable;
  }

  @Override
  public ReadOnlyBooleanProperty vulnerableProperty() {
    return new ReadOnlyBooleanWrapper(this.childIsVulnerable);
  }

  @Override
  public ReadOnlyBooleanWrapper containsCodeProperty() {
    return new ReadOnlyBooleanWrapper(this.childContainsCode);
  }

  @Override
  public boolean isContainsCode() {
    return childContainsCode;
  }
}
