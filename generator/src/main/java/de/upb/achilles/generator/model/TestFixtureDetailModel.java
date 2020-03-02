package de.upb.achilles.generator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/** @author Andreas Dann created on 10.01.19 */
public class TestFixtureDetailModel {

  private final SimpleStringProperty file;
  private final SimpleStringProperty qname;
  private final SimpleBooleanProperty contained;
  private final SimpleBooleanProperty include;
  private final BooleanProperty editable = new SimpleBooleanProperty(false);
  private String repackagedQName;

  public TestFixtureDetailModel(TestFixtureDetail testFixtureDetail) {
    file = new SimpleStringProperty(testFixtureDetail.getAffectedFile());
    qname = new SimpleStringProperty(testFixtureDetail.getQname());
    contained = new SimpleBooleanProperty(testFixtureDetail.isContained());
    if (testFixtureDetail.isContained()) {
      include = new SimpleBooleanProperty(testFixtureDetail.isContained());
      editable.setValue(true);
    } else {
      include = new ReadOnlyBooleanWrapper(false);
    }
    repackagedQName = null;
  }

  public boolean getEditable() {
    return editable.get();
  }

  public void setEditable(boolean editable) {
    this.editable.set(editable);
  }

  public BooleanProperty editableProperty() {
    return editable;
  }

  public void setRepackagedQName(String repackagedQName) {
    this.repackagedQName = repackagedQName;
  }

  public String getFile() {
    return file.get();
  }

  public SimpleStringProperty fileProperty() {
    return file;
  }

  public String getQname() {

    if (repackagedQName != null && !repackagedQName.isEmpty()) {
      return repackagedQName;
    }

    return qname.get();
  }

  public SimpleStringProperty qnameProperty() {
    return qname;
  }

  public boolean isContained() {
    return contained.get();
  }

  public void setContained(boolean contained) {
    this.contained.set(contained);
  }

  public SimpleBooleanProperty containedProperty() {
    return contained;
  }

  public boolean isInclude() {
    return include.get();
  }

  public void setInclude(boolean include) {
    this.include.set(include);
  }

  public SimpleBooleanProperty includeProperty() {
    return include;
  }

  @Override
  public String toString() {
    return this.getFile();
  }
}
