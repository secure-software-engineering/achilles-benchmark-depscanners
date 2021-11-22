package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;

public class JarModificationException extends IOException {

  private final Set<TestFixtureModel> failedTestFixture;

  public JarModificationException(
      String message, Throwable cause, @Nonnull TestFixtureModel failedTestFixture) {
    this(message, cause, Collections.singleton(failedTestFixture));
  }

  public JarModificationException(
      String message, Throwable cause, Set<TestFixtureModel> failedTestFixtures) {
    super(message, cause);
    this.failedTestFixture = failedTestFixtures;
  }

  public Collection<TestFixtureModel> getFailedTestFixture() {
    return failedTestFixture;
  }
}
