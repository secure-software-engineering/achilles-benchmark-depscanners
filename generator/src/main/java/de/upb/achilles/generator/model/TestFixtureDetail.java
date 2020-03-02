package de.upb.achilles.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** @author Andreas Dann created on 09.01.19 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestFixtureDetail {

  private String affectedFile;

  private String qname;

  private boolean contained;

  public TestFixtureDetail() {}

  public TestFixtureDetail(String file, String qname, boolean contained) {
    this.affectedFile = file;
    this.qname = qname;
    this.contained = contained;
  }

  public String getQname() {
    return qname;
  }

  public void setQname(String qname) {
    this.qname = qname;
  }

  public boolean isContained() {
    return contained;
  }

  public void setContained(boolean contained) {
    this.contained = contained;
  }

  public String getAffectedFile() {
    return affectedFile;
  }

  public void setAffectedFile(String affectedFile) {
    this.affectedFile = affectedFile;
  }
}
