
#
#CREDM TAF TEST 2 (period check with certificates ok)
#

# parameters : logfilename="filename" servicename="service" namespace="namespace"
#              default values : logfilename="TAF2result.log", servicename="taftest", namespace="default"

# TEST: check of certificate for the entity NetworkElementConnector<servicename>
#       with already done certificates
#
# secrets:
#     taf-test1-certreq-secret-1
#     taf-test1-tls-secret-1
#     taf-test1-tls-secret-2
# keystore : /ericsson/credm/district11/certs/jbossKS.JKS
# truststore : /ericsson/credm/district11/certs/jbossTS.JKS
# secret data:
#    TYPE1 = cat type1
#    LOCATION1 = cat location1
#    DATA1 = cat keystore.JKS
#    TYPE2 = cat type2
#    LOCATION2 = cat location2
#    DATA2 = cat truststore.JKS

# checks:
# TEST 1 PASSED = credm controller returns OK
# TEST 2 PASSED = certReqState in taf-test1-certreq-secret-1 is ready
# TEST 3 PASSED = tlsStoreType in taf-test1-tls-secret-1 is file
# TEST 4 PASSED = tlsStoreLocation in taf-test1-tls-secret-1 contains jbossKS.JKS
# TEST 5 PASSED = issuer in tlsStoreData in taf-test1-tls-secret-1 contains CN=ENM_Management_CA 

# to be executed in director:

if [[ $1 == help ]]; then
    echo 'usage: runTAF.sh logfilename="filename" servicename="service" namespace="namespace"'
    echo 'default values : logfilename="result.log", servicename="taftest", namespace="default"'
    exit 0
fi

TAF_DIR="/tmp/TAF2"
TAF_LOG="result.log"
tafname="taf2-test"
namespace="default"

for ARGUMENT in "$@"
do
    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)   

    case "$KEY" in
            logfilename)  TAF_LOG=${VALUE} ;;
            servicename)  tafname=${VALUE} ;;   
            namespace)    namespace=${VALUE} ;;    
            *)   
    esac    
done

echo logfilename=$TAF_LOG
echo servicename=$tafname
echo namespace=$namespace

# retrive POD name
podname=$(kubectl get pods -l app=eric-enm-credm-controller --no-headers -o custom-columns=":metadata.name" -n ${namespace} | tail -n 1)
echo "podname="$podname

# make dir inside POD
kubectl exec ${podname} -n ${namespace} -c eric-enm-credm-controller -- mkdir -v ${TAF_DIR}

# copy files inside POD
echo "copy files"
kubectl cp executeTest.sh ${podname}:${TAF_DIR}/executeTest.sh -n ${namespace}  -c eric-enm-credm-controller
kubectl cp configTAF.sh ${podname}:${TAF_DIR}/configTAF.sh -n ${namespace}  -c eric-enm-credm-controller
kubectl cp secretTAF.yaml ${podname}:${TAF_DIR}/secretTAF.yaml -n ${namespace}  -c eric-enm-credm-controller
kubectl cp xmlTAF.xml ${podname}:${TAF_DIR}/xmlTAF.xml -n ${namespace} -c eric-enm-credm-controller
kubectl cp keystore.JKS ${podname}:${TAF_DIR}/keystore.JKS -n ${namespace} -c eric-enm-credm-controller
kubectl cp truststore.JKS ${podname}:${TAF_DIR}/truststore.JKS -n ${namespace} -c eric-enm-credm-controller


kubectl exec ${podname} -n ${namespace}  -c eric-enm-credm-controller -- ls ${TAF_DIR}

# execute test
echo "run executeTest Script for Group 2"
kubectl exec ${podname} -n ${namespace} -- sh ${TAF_DIR}/executeTest.sh ${tafname} ${namespace} ${TAF_DIR} &> ${TAF_LOG}
res=$?


# check result of execution
if [ $res != 0 ]
  then
    echo "TAF2 TEST FAILED"
    exit 1
fi

# check log
#echo ""
#echo "TEST LOG"
#echo ""
#cat ${TAF_LOG}
passedTestStep=$(cat ${TAF_LOG} | grep "PASSED" |grep -v "NOT PASSED" )
failedTestStep=$(cat ${TAF_LOG} | grep "NOT PASSED" )
nPassed=$(cat ${TAF_LOG} | grep "NOT PASSED" | wc -l)
if [[ $nPassed == 0 ]]
then
	echo ""
	echo "TAF2 TEST PASSED"
	echo ""
	echo $passedTestStep
	# remove files
    kubectl exec ${podname} -n ${namespace} -c eric-enm-credm-controller -- rm -vrf ${TAF_DIR}
	exit 0
else
	echo ""
	echo "TAF2 TEST NOT PASSED"
	echo ""
	echo $failedTestStep
	echo ""
	exit 1
fi
