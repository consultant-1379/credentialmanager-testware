

# EXECUTION IN POD OF PERIODIC CHECK TEST (inside POD)

# parameters:
#    servicename
#    namespace
#    working directrory

# checkvalues in configTAF.sh

####### Tests Description ########
# Test 1 : 
T1_Description="Create new secret then get secret and check if is OK for service\":\"taftest\""
# Test 2 :
T2_Description="Check if certReqState in <servicename>-certreq-secret-1 is ready"
# Test 3 :
T3_Description="Check if tlsStoreType: in <servicename>-tls-secret-1 is file"
# Test 4 :
T4_Description="Check if tlsStoreLocation in <servicename>-tls-secret-1 contains ${locationCheck}"
# Test 5 :
T5_Description="Check if issuer in tlsStoreData in <servicename>-tls-secret-1 contains ${issuerCheck}"
###################################

NUM_OF_TEST=5

tafTestName="taf2test"
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

TAF_DIR="/tmp/TAF2"
if [ -n "$3" ]; then
   TAF_DIR=$3
   echo TAF_DIR=${TAF_DIR}
fi

echo "START TAF"

cd ${TAF_DIR}
. ./configTAF.sh

############  prepare data
# Step 1 : Create secret.yaml (base64)
# patch secret yaml file
codedxml=$(cat ./xmlTAF.xml | base64 -w 0)
cat ./secretTAF.yaml | sed -e "s/TAFNAME/${tafTestName}/g" > ./secret.yaml
sed -i -e "s/TAFXML/${codedxml}/g" ./secret.yaml
# Step 2 : md5sum of xmlTAF.xml then populate certReqMD5: in secret.yaml
md5xml=$(md5sum ./xmlTAF.xml | awk '{ print $1 }')
sed -i -e "s/TAFMD5/${md5xml}/g" ./secret.yaml
# Step 3 : Set certReqState: in secret.yaml to ready
readystate="ready"
sed -i -e "s/READYSTATE/${readystate}/g" ./secret.yaml

# Step 4 : secret-1 - Set tlsStoreType: in secret.yaml to file and tlsStoreLocation: to /ericsson/credm/district11/certs/jbossKS.JKS
type1="file"
sed -i -e "s/TYPE1/${type1}/g" ./secret.yaml
location1=$(echo $locationCheck | sed -e 's/\//\\\//g')
sed -i -e "s/LOCATION1/${location1}/g" ./secret.yaml
base64 -w0 keystore.JKS > keystore.b64
data1=$( cat ./keystore.b64 | sed 's/[\\&*./+!]/\\&/g' )
sed -i -e "s/DATA1/${data1}/g" ./secret.yaml

# Step 5 : secret-2 - Set tlsStoreType: in secret.yaml to file and tlsStoreLocation: to /ericsson/credm/district11/certs/jbossTS.JKS
type2="file"
sed -i -e "s/TYPE2/${type2}/g" ./secret.yaml
location2=$(echo $trustLocation | sed -e 's/\//\\\//g')
sed -i -e "s/LOCATION2/${location2}/g" ./secret.yaml
# Step 6 : base64 of truststore.JKS then populate certReqMD5: in secret.yaml
base64 -w0 truststore.JKS > truststore.b64
data2=$( cat ./truststore.b64 | sed 's/[\\&*./+!]/\\&/g' )
sed -i -e "s/DATA2/${data2}/g" ./secret.yaml

# create secret
echo "create secrets"
kubectl create -f ./secret.yaml -n ${namespace}
kubectl get secret -n ${namespace} | grep ${tafTestName}

# ###########  execute test

# call credm controller
echo "REST to credm controller"
result=$(curl --connect-timeout 25  --max-time 45 eric-enm-credm-controller:5001/periodicCheck/${tafTestName})

# ###########  check results

echo ""
passed=0
echo "TEST 1 : credm controller returns OK"
echo "result:"$result
# check result = {"check. is OK for service":"taftest"}
if [[ $result == *"check. is OK for service"* ]]; then
    passed=$((passed+1))
    T1_RESULT="TEST 1 PASSED"
    #echo $T1_RESULT
else
    T1_RESULT="TEST 1 NOT PASSED"
    #echo $T1_RESULT
fi

echo "TEST 2 : certReqState in <servicename>-certreq-secret-1 is ready"
certState=$(kubectl get secret ${tafTestName}-certreq-secret-1 -o yaml -n ${namespace} | grep " certReqState:" | awk '{print $2}' | base64 -d)
echo "certReqState:"$certState
if [[ $certState == "ready" ]]; then
    passed=$((passed+1))
    T2_RESULT="TEST 2 PASSED"
    echo $T2_RESULT
else
    T2_RESULT="TEST 2 NOT PASSED"
    echo $T2_RESULT
fi

# Collect all the certificate information ( tlsStoreType / tlsStoreLocation / tlsStoreData )

certType=$(kubectl get secret ${tafTestName}-tls-secret-1 -o yaml -n ${namespace} | grep " tlsStoreType:" | awk '{print $2}' | base64 -d)
certFile=$(kubectl get secret ${tafTestName}-tls-secret-1 -o yaml -n ${namespace} | grep " tlsStoreLocation:" | awk '{print $2}' | base64 -d)
kubectl get secret ${tafTestName}-tls-secret-1 -o yaml -n ${namespace} | grep " tlsStoreData:" | awk '{print $2}' | base64 -d > ./certStore1
certIssuer=$(keytool -list -v -keystore ./certStore1 -alias jboss -storepass jbossKS 2>/dev/null | grep Issuer)

echo "TEST 3 : tlsStoreType: in <servicename>-tls-secret-1 is file"
echo "tlsStoreType:"$certType
if [[ $certType == "file" ]]; then
    passed=$((passed+1))
    T3_RESULT="TEST 3 PASSED"
    #echo $T3_RESULT
else
    T3_RESULT="TEST 3 NOT PASSED"
    #echo $T3_RESULT
fi

echo "TEST 4 : tlsStoreLocation in <servicename>-tls-secret-1 contains jbossKS.JKS"
echo "tlsStoreLocation:"$certFile
if [[ $certFile == *"${locationCheck}"* ]]; then
    passed=$((passed+1))
    T4_RESULT="TEST 4 PASSED"
    #echo $T4_RESULT
else
    T4_RESULT="TEST 4 NOT PASSED"
    #echo $T4_RESULT
fi

echo "TEST 5 : issuer in tlsStoreData in <servicename>-tls-secret-1 contains CN=ENM_Management_CA"
echo "certIssuer:"$certIssuer
if [[ $certIssuer == *"${issuerCheck}"* ]]; then
    passed=$((passed+1))
    T5_RESULT="TEST 5 PASSED"
    #echo $T5_RESULT
else
    T5_RESULT="TEST 5 NOT PASSED"
    #echo $T5_RESULT
fi

# delete secrets
echo "delete secrets"
kubectl delete secret $(kubectl get secret -n ${namespace} | grep ${tafTestName} | awk '{print $1}') -n ${namespace}
kubectl get secret -n ${namespace} | grep ${tafTestName}

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

exit 0

