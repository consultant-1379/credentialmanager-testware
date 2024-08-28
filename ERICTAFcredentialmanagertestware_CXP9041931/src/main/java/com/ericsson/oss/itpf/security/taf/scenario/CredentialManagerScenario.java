/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.taf.scenario;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.itpf.security.taf.flows.CredMngCenmFlows;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

/**
 * Main class for Testware.
 */
public class CredentialManagerScenario extends TafTestBase {

    public static final String CREDMNG_BEFORESUITE = "CredManager_BeforeSuite_TAF1";
    public static final String CREDMNG_AFTERSUITE = "CredManager_AfterSuite_TAF1";
    public static final String CREDMNG_TEST = "CredManager_Tests_TAF1";
    public static final String CREDENTIALMANAGERCENMTITLE = "Credential Manager cENM Tests TAF1";

    Logger log = LoggerFactory.getLogger(CredentialManagerScenario.class);

    final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();

    @Inject
    CredMngCenmFlows crdMngCenmFlows;

    @BeforeSuite
    @Parameters({"fileVersion"})
    public void beforeSuite(final String fileVersion) {
        log.info("CredentialManagerScenario: beforeSuite start");
        log.info("CredentialManagerScenario: FileVersion running: " + fileVersion);
        final boolean isCloud = HostConfigurator.isCloudEnvironment();
        log.info("CredentialManagerScenario: beforeSuite is isCloudEnvironment: " + isCloud);
        if (!isCloud) {
            log.info("NA : wrong environment");
            throw new SkipException("NA : wrong environment");
        }
        final TestScenario scenario = scenario(CREDMNG_BEFORESUITE)
                .addFlow(crdMngCenmFlows.deleteDir(fileVersion))
                .addFlow(crdMngCenmFlows.createDir(fileVersion))
                .addFlow(crdMngCenmFlows.copyFiles(fileVersion))
                .build();
        runner.start(scenario);
        log.info("CredentialManagerScenario: beforeSuite end");
    }

    @AfterSuite(alwaysRun = true)
    @Parameters({"fileVersion"})
    public void afterSuite(final String fileVersion) {
        log.info("CredentialManagerScenario: afterSuite start");
        final TestScenario scenario = scenario(CREDMNG_AFTERSUITE)
                .addFlow(crdMngCenmFlows.deleteDir(fileVersion))
                .addFlow(crdMngCenmFlows.closeShell())
                .build();
        runner.start(scenario);
        log.info("CredentialManagerScenario: afterSuite end");
    }

    @Test(enabled = true, groups = { "Functional" })
    @TestId(id = "TORF-521726", title = CREDENTIALMANAGERCENMTITLE)
    @Parameters({"fileVersion"})
    public void credMngTest(final String fileVersion) {
        log.info("CredentialManagerScenario: credMngTest start");
        final TestScenario scenario = scenario(CREDMNG_TEST)
                .addFlow(crdMngCenmFlows.runTest(fileVersion))
                .build();
        runner.start(scenario);
        log.info("CredentialManagerScenario: credMngTest end");
    }

}
