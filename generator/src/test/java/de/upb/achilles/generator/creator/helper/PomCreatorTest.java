package de.upb.achilles.generator.creator.helper;

import static org.junit.Assert.*;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.Initializer;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

/** @author Andreas Dann created on 10.01.19 */
public class PomCreatorTest {

  @Test
  public void createPomAsString() {
    List<TestFixtureModel> testFixtureModels = Initializer.dummyTestFixtures();
    GAV projectGAV = RandomGavCreator.getRandomGave();
    PomCreator truthCreatorTest =
        new PomCreator(projectGAV, testFixtureModels, Paths.get(System.getProperty("user.home")));

    String pomAsString = truthCreatorTest.createPomAsString();

    assertNotNull(pomAsString);

    assertNotEquals("", pomAsString);

    boolean contains = pomAsString.contains(truthCreatorTest.createGAVInfo(projectGAV));
    assertTrue(contains);

    GAV gav = new GAV("g", "a", "1");
    contains = pomAsString.contains(truthCreatorTest.createGAVInfo(gav));

    assertTrue(contains);

    // but only once
    // remove it
    pomAsString = pomAsString.replace(truthCreatorTest.createGAVInfo(gav), "");

    contains = pomAsString.contains(truthCreatorTest.createGAVInfo(gav));

    assertFalse(contains);

    System.out.println(pomAsString);
  }
}
