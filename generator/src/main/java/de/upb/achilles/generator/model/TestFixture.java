package de.upb.achilles.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.annotation.Nonnull;

/** @author Andreas Dann created on 05.01.19 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestFixture {

  private String cve;

  private String comment;

  private GAV gav;

  private boolean vulnerable;

  private TestFixtureDetail[] details;

  private String timestamp;

  public TestFixture() {}

  public TestFixture(String cve, String comment, GAV gav, boolean vulnerable) {
    this(cve, comment, gav, vulnerable, new TestFixtureDetail[0]);
  }

  public TestFixture(
      String cve, String comment, GAV gav, boolean vulnerable, TestFixtureDetail[] details) {
    this.cve = cve;
    this.comment = comment;
    this.gav = gav;
    this.vulnerable = vulnerable;
    this.details = details;
  }

  public String getCve() {
    return cve;
  }

  public String getComment() {
    return comment;
  }

  public GAV getGav() {
    return gav;
  }

  public boolean isVulnerable() {
    return vulnerable;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  @Nonnull
  public TestFixtureDetail[] getDetails() {
    return details;
  }
}
