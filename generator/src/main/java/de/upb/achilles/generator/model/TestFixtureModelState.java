package de.upb.achilles.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestFixtureModelState {

  GAVModification changeGav;
  // add state for included qnames
  ArrayList<String> includedQnames;
  ByteCodeModification byteCodeModification;

  public GAVModification getChangeGav() {
    return changeGav;
  }

  public void setChangeGav(GAVModification changeGav) {
    this.changeGav = changeGav;
  }

  public ByteCodeModification getByteCodeModification() {
    return byteCodeModification;
  }

  public void setByteCodeModification(ByteCodeModification byteCodeModification) {
    this.byteCodeModification = byteCodeModification;
  }

  public ArrayList<String> getIncludedQnames() {
    return includedQnames;
  }

  public void setIncludedQnames(ArrayList<String> includedQnames) {
    this.includedQnames = includedQnames;
  }
}
