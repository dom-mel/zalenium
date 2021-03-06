package de.zalando.ep.zalenium.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DashboardTest {

    private static final String TEST_COUNT_FILE_NAME = "executedTestsInfo.json";

    private TestInformation ti = new TestInformation("seleniumSessionId", "testName", "proxyName", "browser",
            "browserVersion", "platform");

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void initDashboard() throws IOException {
        Dashboard.setExecutedTests(0, 0);
        TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
        CommonProxyUtilities proxyUtilities = TestUtils.mockCommonProxyUtilitiesForDashboardTesting(temporaryFolder);
        Dashboard.setCommonProxyUtilities(proxyUtilities);
    }

    @After
    public void restoreCommonProxyUtilities() {
        Dashboard.restoreCommonProxyUtilities();
    }

    @Test
    public void testCountOne() throws IOException {
        Dashboard.updateDashboard(ti);
        Assert.assertEquals(1, Dashboard.getExecutedTests());
        Assert.assertEquals(1, Dashboard.getExecutedTestsWithVideo());
    }

    @Test
    public void testCountTwo() throws IOException {
        Dashboard.updateDashboard(ti);
        Dashboard.updateDashboard(ti);
        Assert.assertEquals(2, Dashboard.getExecutedTests());
        Assert.assertEquals(2, Dashboard.getExecutedTestsWithVideo());
    }

    @Test
    public void missingExecutedTestsFile() throws IOException {
        Dashboard.updateDashboard(ti);
        cleanTempVideosFolder();
        TestUtils.ensureRequiredInputFilesExist(temporaryFolder);
        Dashboard.updateDashboard(ti);
        Assert.assertEquals(1, Dashboard.getExecutedTests());
        Assert.assertEquals(1, Dashboard.getExecutedTestsWithVideo());
    }

    @Test
    public void nonNumberContentsIgnored() throws IOException {
        File testCountFile = new File(temporaryFolder.getRoot().getAbsolutePath() + "/" + Dashboard.VIDEOS_FOLDER_NAME
                + "/" + TEST_COUNT_FILE_NAME);
        JsonObject testQuantities = new JsonObject();
        testQuantities.addProperty("executedTests", "Not-A-Number");
        testQuantities.addProperty("executedTestsWithVideo", "Not-A-Number");
        FileUtils.writeStringToFile(testCountFile, testQuantities.toString(), UTF_8);
        Dashboard.setExecutedTests(0, 0);
        Dashboard.updateDashboard(ti);
        Assert.assertEquals(1, Dashboard.getExecutedTests());
        Assert.assertEquals(1, Dashboard.getExecutedTestsWithVideo());
    }

    private void cleanTempVideosFolder() throws IOException {
        FileUtils.cleanDirectory(new File(temporaryFolder.getRoot().getAbsolutePath()));
    }
}
