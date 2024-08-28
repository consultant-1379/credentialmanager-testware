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
package com.ericsson.oss.itpf.security.taf.flows;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;

import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.oss.itpf.security.taf.testStep.CredMngrCenmTestSteps2;

import javax.inject.Inject;


public class CredMngCenmFlows2 {

    @Inject
    CredMngrCenmTestSteps2 credMngrCenmTestSteps;


    public TestStepFlow deleteDir(final String fileVersion2) {
        return flow("Delete Dir")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps2.DELETE_DIR_TESTSTEP)
                                .withParameter(CredMngrCenmTestSteps2.FILE_VERSION_PARAM, fileVersion2))
                .build();
    }

    public TestStepFlow createDir(final String fileVersion2) {
        return flow("Create Dir")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps2.CREATE_DIR_TESTSTEP)
                                .withParameter(CredMngrCenmTestSteps2.FILE_VERSION_PARAM, fileVersion2))
                .build();
    }

    public TestStepFlow copyFiles(final String fileVersion2) {
        return flow("Copy Files")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps2.COPY_FILES_TESTSTEP)
                                .withParameter(CredMngrCenmTestSteps2.FILE_VERSION_PARAM, fileVersion2))
                .build();
    }

    public TestStepFlow runTest(final String fileVersion2) {
        return flow("Run Test")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps2.RUN_TEST_TESTSTEP))
                .build();
    }

    public TestStepFlow closeShell() {
        return flow("Close Shell")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps2.CLOSE_SHELL_TESTSTEP))
                .build();
    }

}
