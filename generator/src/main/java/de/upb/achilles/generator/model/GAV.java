package de.upb.achilles.generator.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** @author Andreas Dann created on 05.01.19 */
public class GAV {

  private String groupId;
  private String artifactId;
  private String version;

  public GAV() {}

  public GAV(String groupId, String artifactId, String version) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return groupId + ":" + artifactId + ":" + version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    GAV gav = (GAV) o;

    return new EqualsBuilder()
        .append(groupId, gav.groupId)
        .append(artifactId, gav.artifactId)
        .append(version, gav.version)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(groupId)
        .append(artifactId)
        .append(version)
        .toHashCode();
  }
}
