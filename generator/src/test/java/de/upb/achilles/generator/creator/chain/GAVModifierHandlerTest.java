package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.GAVModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import org.junit.Test;

import static org.junit.Assert.*;

public class GAVModifierHandlerTest {

  @Test
  public void canHandle() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.MOD);

    GAVModifierHandler downloadHandler = new GAVModifierHandler();
    assertTrue(downloadHandler.canHandle(model));
  }

  @Test
  public void canHandleNot() {
    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.ORG);

    GAVModifierHandler downloadHandler = new GAVModifierHandler();
    assertFalse(downloadHandler.canHandle(model));
  }

  @Test
  public void handle() {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.MOD);

    GAVModifierHandler modifierHandler = new GAVModifierHandler();
    modifierHandler.handle(model);
    assertNotEquals(model.getOrgGav(), model.getGAV4Pom());
  }

  @Test
  public void handleSameGAVTheSame() {

    TestFixtureModel model = HandlerTestUtils.getEmptyTestFixtureModel();
    model.setChangeGAV(GAVModification.RANDOM);

    TestFixtureModel model2 = HandlerTestUtils.getEmptyTestFixtureModel();
    model2.setChangeGAV(GAVModification.RANDOM);

    GAVModifierHandler modifierHandler = new GAVModifierHandler();

    modifierHandler.handle(model);
    assertNotEquals(model.getOrgGav(), model.getGAV4Pom());

    modifierHandler.handle(model2);
    assertNotEquals(model2.getOrgGav(), model2.getGAV4Pom());

    assertEquals(model.getGAV4Pom(), model2.getGAV4Pom());
  }
}
