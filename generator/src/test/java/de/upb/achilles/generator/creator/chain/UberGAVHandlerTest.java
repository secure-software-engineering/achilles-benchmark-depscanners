package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.TestFixtureModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UberGAVHandlerTest {

  @Test
  public void canHandle() {
    UberGAVHandler uberGAVHandler = new UberGAVHandler(true);
    TestFixtureModel testFixtureModel = HandlerTestUtils.getEmptyTestFixtureModel();
    assertTrue(uberGAVHandler.canHandle(testFixtureModel));
  }

  @Test
  public void handle() {

    UberGAVHandler uberGAVHandler = new UberGAVHandler(true);
    TestFixtureModel testFix1 = HandlerTestUtils.getEmptyTestFixtureModel();
    TestFixtureModel testFix2 = HandlerTestUtils.getEmptyTestFixtureModel();


    uberGAVHandler.handle(testFix1);

    uberGAVHandler.handle(testFix2);


    assertEquals(testFix1.getGAV4Pom(), testFix2.getGAV4Pom());


  }
}
