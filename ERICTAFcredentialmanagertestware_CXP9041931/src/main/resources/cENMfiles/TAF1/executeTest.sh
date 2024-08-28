
# EXECUTION IN POD OF INITIAL CERTREQUEST TEST (inside POD)

# parameters:
#    servicename
#    namespace
#    working directrory

# checkvalues in configTAF.sh

####### Tests Description ########
# Test 1 : 
T1_Description="Create new secret then get secret and check if \"certReq. is OK\" is present"
# Test 2 :
T2_Description="Check if certReqState in <servicename>-certreq-secret-1 is ready"
# Test 3 :
T3_Description="Check if tlsStoreType in <servicename>-tls-secret-1 is file"
# Test 4 :
T4_Description="Check if tlsStoreLocation in <servicename>-tls-secret-1 contains ${locationCheck}"
# Test 5 :
T5_Description="Check if issuer in tlsStoreData in <servicename>-tls-secret-1 contains ${issuerCheck}"
###################################

NUM_OF_TEST=5
TIME_TEST=$(date)

tafTestName="taf1test"
if [ -n "$1" ]; then
   tafTestName=$1
   echo tafTestName=${tafTestName}
fi

# NOTE: env variable NAMESPACE is present in credm-controller POD
namespace=$NAMESPACE
if [ -n "$2" ]; then
   namespace=$2
   echo namespace=${namespace}
fi

TAF_DIR="/tmp/TAF1"
if [ -n "$3" ]; then
   TAF_DIR=$3
   echo TAF_DIR=${TAF_DIR}
fi

echo "START TAF GROUP 1"
echo $TIME_TEST

cd ${TAF_DIR}
. ./configTAF.sh

############  prepare data

# patch secret yaml file
codedxml=$(cat ./xmlTAF.xml | base64 -w 0)
cat ./secretTAF.yaml | sed -e "s/TAFNAME/${tafTestName}/g" > ./secret.yaml
sed -i -e "s/TAFXML/${codedxml}/g" ./secret.yaml

# create secret
echo "create secrets"
kubectl create -f ./secret.yaml -n ${namespace}
kubectl get secret -n ${namespace} | grep ${tafTestName}
echo $TIME_TEST
# ###########  execute test

# call credm controller
echo "REST to credm controller"
result=$(curl --connect-timeout 25 --max-time 45 eric-enm-credm-controller:5001/certrequest/${tafTestName})
echo $TIME_TEST
# ###########  check results
echo ""
passed=0
echo "TEST 1 : credm controller returns OK"
echo "result:"$result
# check result = {"certReq. is OK for service":"taftest"}
if [[ $result == *"certReq. is OK"* ]]; then
    passed=$((passed+1))
    T1_RESULT="TEST 1 PASSED OK"
    #echo $T1_RESULT
else
    T1_RESULT="TEST 1 NOT PASSED"
    #echo $T1_RESULT
fi
echo $TIME_TEST
echo "TEST 2 : certReqState in <servicename>-certreq-secret-1 is ready"
certState=$(kubectl get secret ${tafTestName}-certreq-secret-1 -o yaml -n ${namespace} | grep " certReqState:" | awk '{print $2}' | base64 -d)
echo "certReqState:"$certState
if [[ $certState == "ready" ]]; then
    passed=$((passed+1))
    T2_RESULT="TEST 2 PASSED OK"
    #echo $T2_RESULT
else
    T2_RESULT="TEST 2 NOT PASSED"
    #echo $T2_RESULT
fi
echo $TIME_TEST
# check certificate
certType=$(kubectl get secret ${tafTestName}-tls-secret-1 -o yaml -n ${namespace} | grep " tlsStoreType:" | awk '{print $2}' | base64 -d)
certFile=$(kubectl get secret ${tafTestName}-tls-secret-1 -o yaml -n ${namespace} | grep " tlsStoreLocation:" | awk '{print $2}' | base64 -d)
kubectl get secret ${tafTestName}-tls-secret-1 -o yaml -n ${namespace} | grep " tlsStoreData:" | awk '{print $2}' | base64 -d > ./certStore1
certIssuer=$(keytool -list -v -keystore ./certStore1 -alias jboss -storepass jbossKS 2>/dev/null | grep Issuer)

echo "TEST 3 : tlsStoreType in <servicename>-tls-secret-1 is file"
echo "tlsStoreType:"$certType
if [[ $certType == "file" ]]; then
    passed=$((passed+1))
    T3_RESULT="TEST 3 PASSED OK"
    #echo $T3_RESULT
else
    T3_RESULT="TEST 3 NOT PASSED"
    #echo $T3_RESULT
fi
echo $TIME_TEST
echo "TEST 4 : tlsStoreLocation in <servicename>-tls-secret-1 contains ${locationCheck}"
echo "tlsStoreLocation:"$certFile
if [[ $certFile == *"${locationCheck}"* ]]; then
    passed=$((passed+1))
    T4_RESULT="TEST 4 PASSED OK"
    #echo $T4_RESULT
else
    T4_RESULT="TEST 4 NOT PASSED"
    #echo $T4_RESULT
fi
echo $TIME_TEST
echo "TEST 5 : issuer in tlsStoreData in <servicename>-tls-secret-1 contains ${issuerCheck}"
echo "certIssuer:"$certIssuer
if [[ $certIssuer == *"${issuerCheck}"* ]]; then
    passed=$((passed+1))
    T5_RESULT="TEST 5 PASSED OK"
    #echo $T5_RESULT
else
    T5_RESULT="TEST 5 NOT PASSED"
    #echo $T5_RESULT
fi
echo $TIME_TEST
# delete secrets
echo "delete secrets"
kubectl delete secret $(kubectl get secret -n ${namespace} | grep ${tafTestName} | awk '{print $1}') -n ${namespace}
kubectl get secret -n ${namespace} | grep ${tafTestName}
echo $TIME_TEST
# check sum of checks

if [ $passed != $NUM_OF_TEST ]
  then
    echo "$T1_RESULT : $T1_Description"
    echo "$T2_RESULT : $T2_Description"
    echo "$T3_RESULT : $T3_Description"
    echo "$T4_RESULT : $T4_Description"
    echo "$T5_RESULT : $T5_Description"
    echo "TAF TEST NOT PASSED"
else
    echo "$T1_RESULT : $T1_Description"
    echo "$T2_RESULT : $T2_Description"
    echo "$T3_RESULT : $T3_Description"
    echo "$T4_RESULT : $T4_Description"
    echo "$T5_RESULT : $T5_Description"
    echo "TAF TEST PASSED"
fi
echo $TIME_TEST
exit 0



