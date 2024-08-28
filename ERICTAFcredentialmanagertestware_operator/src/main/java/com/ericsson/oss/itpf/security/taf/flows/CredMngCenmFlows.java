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
import com.ericsson.oss.itpf.security.taf.testStep.CredMngrCenmTestSteps;

import javax.inject.Inject;


public class CredMngCenmFlows {

    @Inject
    CredMngrCenmTestSteps credMngrCenmTestSteps;


    public TestStepFlow deleteDir(final String fileVersion) {
        return flow("Delete Dir")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps.DELETE_DIR_TESTSTEP)
                                .withParameter(CredMngrCenmTestSteps.FILE_VERSION_PARAM, fileVersion))
                .build();
    }

    public TestStepFlow createDir(final String fileVersion) {
        return flow("Create Dir")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps.CREATE_DIR_TESTSTEP)
                                .withParameter(CredMngrCenmTestSteps.FILE_VERSION_PARAM, fileVersion))
                .build();
    }

    public TestStepFlow copyFiles(final String fileVersion) {
        return flow("Copy Files")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps.COPY_FILES_TESTSTEP)
                                .withParameter(CredMngrCenmTestSteps.FILE_VERSION_PARAM, fileVersion))
                .build();
    }

    public TestStepFlow runTest(final String fileVersion) {
        return flow("Run Test")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps.RUN_TEST_TESTSTEP))
                .build();
    }

    public TestStepFlow closeShell() {
        return flow("Close Shell")
                .addTestStep(
                        annotatedMethod(credMngrCenmTestSteps, CredMngrCenmTestSteps.CLOSE_SHELL_TESTSTEP))
                .build();
    }

}
