package de.upb.achilles.generator.creator;

import org.junit.Assert;
import org.junit.Test;

/** @author Andreas Dann created on 10.01.19 */
public class UtilTest {

  @Test
  public void getClassName() {
    Assert.assertEquals("java.lang.System", Util.getClassName("java.lang.System"));

    Assert.assertEquals("java.lang.System", Util.getClassName("java.lang.System.method()"));

    Assert.assertEquals(
        "java.lang.System", Util.getClassName("java.lang.System.method(java.lang.String)"));

    Assert.assertEquals("java.lang.System", Util.getClassName("java.lang.System.<clinit>"));

    Assert.assertEquals(
        "java.lang.System$Start", Util.getClassName("java.lang.System$Start.method()"));
  }
}
