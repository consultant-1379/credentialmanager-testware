/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.itpf.security.taf.testStep;

import static com.ericsson.oss.testware.hostconfigurator.HostConfigurator.PATH_TO_PRIVATE_KEY;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.TafCliToolShell;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.de.tools.cli.CliCommandResult;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.ericsson.oss.testware.remoteexecution.operators.PibConnectorImpl;

/**
 * This class contains credential manger CLI test steps:.
 * <ul>
 * <li>TEST_STEP_ENVIROMENT_XML</li>
 * <li>TEST_STEP_XML</li>
 * <li>TEST_STEP_TRANSF_XML</li>
 * <li>TEST_STEP_TRANSF_SCRIPT</li>
 * <li>TEST_STEP_CERTIFICATE</li>
 * <li>TEST_STEP_STORECONDITIONS</li>
 * <li>TEST_STEP_RESPONSE_CHECK</li>
 * <li>TEST_STEP_STORE_CHECK</li>
 * <li>TEST_STEP_CHAIN_CHEK</li>
 * <li>TEST_STEP_DATE_CHECK</li>
 * <li>TEST_STEP_DATE_INSTALLCHECK</li>
 * <li>TEST_STEP_SCRIPT_CHECK</li>
 * <li>TEST_STEP_CLOSED_SHELL</li>
 * </ul>
 * <br/>
 * Datasources this class uses:
 * <ul>
 * <li>USERS_TO_VERIFY_DATASOURCE</li>
 * <li>USERS_TO_CREATE</li>
 * <li>CREATED_USER</li>
 * <li>USERS_TO_UPDATE</li>
 * <li>UPDATED_USER</li>
 * <li>AVAILABLE_USERS</li>
 * <li>USERS_TO_DELETE</li>
 * <li>USERS_TO_DELETE</li>
 * </ul>
 */
public class CredMngrCenmTestSteps2 {
    public static final String DELETE_DIR_TESTSTEP = "DeleteDirTestStep";
    public static final String CREATE_DIR_TESTSTEP = "CreateDirTestStep";
    public static final String COPY_FILES_TESTSTEP = "CopyFilesTestStep";
    public static final String RUN_TEST_TESTSTEP = "RunTestTestStep";
    public static final String CLOSE_SHELL_TESTSTEP = "CloseShellTestStep";
    public static final String TAF2_GROUP = "cENMfiles/TAF2";
    public static final long CLITOOL_TIMEOUT = 40;

    public static final String FILE_VERSION_PARAM = "starSearchFileParam";
    
    private static List<String> LOCAL_FILE_NAMES = Arrays.asList("configTAF.sh", "executeTest.sh", "runTAF.sh", "secretTAF.yaml", "xmlTAF.xml", "keystore.JKS", "truststore.JKS");

    private static final Logger log = LoggerFactory.getLogger(CredMngrCenmTestSteps2.class);

    final Host pibHost = HostConfigurator.getPibHost();

    private TafCliToolShell shellPibConn = new PibConnectorImpl().getConnection();
    private String tempRemoteFileWithPath;

    @TestStep(id = DELETE_DIR_TESTSTEP)
    public void deleteDirTestStep(@Input(FILE_VERSION_PARAM) final String fileVersion2) {
        final String rmdirStringCommand = String.format("rm -rf %s", fileVersion2);
        final String lsStringCommand = String.format("ls -la %s", fileVersion2);
        final CliCommandResult rmdirCliResult = shellPibConn.execute(rmdirStringCommand);
        Assertions.assertThat(rmdirCliResult.isSuccess()).as(rmdirCliResult.getOutput()).isTrue();
        final CliCommandResult lsResult = shellPibConn.execute(lsStringCommand);
        log.info("CredMngrCenmTestSteps2: deleteDirTestStep" + lsResult);
    }

    @TestStep(id = CREATE_DIR_TESTSTEP)
    public void createDirTestStep(@Input(FILE_VERSION_PARAM) final String fileVersion2) {
        log.info("CredMngrCenmTestSteps2: Director used: " + pibHost);
        final String mkdirStringCommand = String.format("mkdir -p %s", fileVersion2);
        final String lsStringCommand = String.format("ls -la %s", fileVersion2);
        final String chmodStringCommand = String.format("chmod -R 777 %s", fileVersion2);
        final CliCommandResult mkdirCliResult = shellPibConn.execute(mkdirStringCommand);
        Assertions.assertThat(mkdirCliResult.isSuccess()).as(mkdirCliResult.getOutput()).isTrue();
        final CliCommandResult chmodCliResult = shellPibConn.execute(chmodStringCommand);
        Assertions.assertThat(chmodCliResult.isSuccess()).as(chmodCliResult.getOutput()).isTrue();
        final CliCommandResult lsResult = shellPibConn.execute(lsStringCommand);
        log.info("CREATE_DIR_TESTSTEP : CredMngrCenmTestSteps2: createDirTestStep" + lsResult);
    }

    @TestStep(id = COPY_FILES_TESTSTEP)
    public void copyFilesTestStep(@Input(FILE_VERSION_PARAM) final String fileVersion2) {
        log.info("COPY_FILES_TESTSTEP : You are using file from : " + fileVersion2);
        log.info("COPY_FILES_TESTSTEP : FILE_VERSION_PARAM is :" + FILE_VERSION_PARAM);
        tempRemoteFileWithPath = shellPibConn.execute("pwd").getOutput();
        log.info("COPY_FILES_TESTSTEP : RunTest Path is :" + tempRemoteFileWithPath);
        final String lsStringCommand = String.format("ls -la %s", fileVersion2);
        log.info("COPY_FILES_TESTSTEP : Directory string command is :" + lsStringCommand);
        final RemoteObjectHandler remote = new RemoteObjectHandler(pibHost);
        final Path privateKey = Paths.get(PATH_TO_PRIVATE_KEY).getFileName();
        final String pemKeyFile = privateKey.toString();
        log.info("COPY_FILES_TESTSTEP : CredMngrCenmTestSteps2: copyFilesTestStep pem file path -> " + pemKeyFile);
        for (final String fileName : LOCAL_FILE_NAMES) {
            log.info("COPY_FILES_TESTSTEP : LOCAL_FILE_NAMES is :  -> " + fileName);
            final String remoteFileName = String.format("%s/%s", fileVersion2, fileName);
            final String remoteFullPath = String.format("%s/%s", tempRemoteFileWithPath, remoteFileName);
            final boolean copyResult = remote.copyLocalFileToRemoteWithSshKeyFile(fileName, remoteFullPath, fileVersion2, pemKeyFile);
            log.info("COPY_FILES_TESTSTEP : fileName  is  :" + fileName);
            log.info("COPY_FILES_TESTSTEP : remoteFullPath  is  :" + remoteFullPath);
            log.info("COPY_FILES_TESTSTEP : fileVersion2  is  :" + fileVersion2);
            log.info("COPY_FILES_TESTSTEP : pemKeyFile  is  :" + pemKeyFile);
//             final boolean copyResult2 = remote.copyLocalDirToRemoteWithSshKeyFile(fileVersion2, remoteFullPath, pemKeyFile);
//             Assertions.assertThat(copyResult2).as(String.format("Transfer dir %s failed", remoteFileName)).isTrue();
            Assertions.assertThat(copyResult).as(String.format("Transfer file %s failed", remoteFileName)).isTrue();
            final String chmodStringCommand = String.format("sudo chmod -R 777 %s", remoteFileName);
            final CliCommandResult chmodCliResult = shellPibConn.execute(chmodStringCommand);
            Assertions.assertThat(chmodCliResult.isSuccess()).as(chmodCliResult.getOutput()).isTrue();
        }
        final CliCommandResult lsResult = shellPibConn.execute(lsStringCommand);
        log.info("COPY_FILES_TESTSTEP : CredMngrCenmTestSteps2: copyFilesTestStep" + lsResult);
    }

    @TestStep(id = RUN_TEST_TESTSTEP)
    public void runTestTestStep(@Input(FILE_VERSION_PARAM) final String fileVersion2) {
        log.info("RUN_TEST_TESTSTEP : You are using file from : " + TAF2_GROUP);
        tempRemoteFileWithPath = shellPibConn.execute("pwd").getOutput();
        log.info("RUN_TEST_TESTSTEP : RunTest Path is :" + tempRemoteFileWithPath);
        final String cdCommand = String.format("cd %s/%s", tempRemoteFileWithPath, TAF2_GROUP);
        shellPibConn.execute(cdCommand);
        final String runTest = String.format("%s  namespace=%s", "./runTAF.sh", pibHost.getNamespace());
        log.info("RUN_TEST_TESTSTEP : RunTest String :" + runTest);
        final String runTestResult = shellPibConn.execute(runTest, CLITOOL_TIMEOUT).getOutput();
        log.info("RUN_TEST_TESTSTEP : RunTestResult Strings are:" + runTestResult);
        log.info("RUN_TEST_TESTSTEP : RunTestResult Log Finished:" );
        if (runTestResult.contains("TEST NOT PASSED")) {
            final String catCommand = "cat result.log";
            final String catResult = shellPibConn.execute(catCommand).getOutput();
            //Logger.info("Script Output is [{}]", catResult)
            //log.info(runTestResult + catResult);
            log.info("RUN_TEST_TESTSTEP : runTestResult: " + runTestResult);
            log.info("Script Output is [{}]", catResult);
            Assertions.assertThat(runTestResult).as(catResult).doesNotContain("TEST NOT PASSED");
        } else {
            final String catCommand = "cat result.log";
            final String catResult = shellPibConn.execute(catCommand).getOutput();
            log.info("RUN_TEST_TESTSTEP : runTestResult: " + runTestResult + catResult);
            Assertions.assertThat(runTestResult).as(catResult).doesNotContain("TEST NOT PASSED");
        }
    }

    @TestStep(id = CLOSE_SHELL_TESTSTEP)
    public void closeShellTestStep() {
        shellPibConn.close();
    }
}
