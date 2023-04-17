for i in `seq 5000 1000 9000`
do
    echo $i
    java -cp ./plansCreation.jar org.matsim.contrib.smartcity.scenariocreation.RandomPlansCreation $i  ./prove/masa/config.xml "plans$i" org.matsim.contrib.smartcity.agent.CLASSDriverLogicBasic
done
