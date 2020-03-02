package de.upb.achilles.generator.creator.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.Initializer;
import de.upb.achilles.generator.model.TestFixtureModel;
import de.upb.achilles.generator.util.RandomGavCreator;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Andreas Dann
 * created on 10.01.19
 */
public class TruthCreatorTest {

    @org.junit.Test
    public void createTruthAsString() throws JsonProcessingException {

        List<TestFixtureModel> testFixtureModels = Initializer.dummyTestFixtures();
        GAV projectGAV = RandomGavCreator.getRandomGave();
        TruthCreator truthCreatorTest = new TruthCreator(projectGAV, testFixtureModels, Paths.get(System.getProperty("user.home")));

        String truthAsString = truthCreatorTest.createTruthAsString();

        assertNotNull(truthAsString);
        assertNotEquals("",truthAsString);


        for(TestFixtureModel model : testFixtureModels){

            String truthString = truthCreatorTest.createTestFixturesTruths(Collections.singletonList(model));
            assertNotEquals("",truthAsString);
            assertNotNull(truthString);
            //cut of [, ] and start and end
            truthString = truthString.substring(1,truthString.length());
            truthString = truthString.substring(0,truthString.length()-1);

            boolean contains = truthAsString.contains(truthString);
            assertTrue(contains);
        }




        System.out.println(truthAsString);

    }
}