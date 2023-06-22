package de.upb.achilles.generator.creator.chain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.upb.achilles.generator.model.TestFixtureModel;
import org.junit.Test;

public class JarRemoveFilesTrimmerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.getTestFixtureDetailModel()[0].setInclude(false);
    JarRemoveFilesTrimmer trimmer = new JarRemoveFilesTrimmer();
    assertTrue(trimmer.canHandle(model));
  }

  @Test
  public void canHandleNot() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();

    JarRemoveFilesTrimmer trimmer = new JarRemoveFilesTrimmer();
    assertFalse(trimmer.canHandle(model));
  }

  @Test
  public void modifyJar() {

    // TODO: implement test

  }
}
