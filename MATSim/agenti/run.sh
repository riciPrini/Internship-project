# for i in `seq 100 100 5000` `seq 6000 1000 9000`
# do
#     mkdir output/out/output$i
#     sed -i -e "s/TO_SET/plans$i/g" ./scenarioMasa/config.xml
#     sed -i -e "s/OUTPUT_DIR/output$i/g" ./scenarioMasa/config.xml
#     java -Xmx6g -cp ./run.jar org.matsim.contrib.smartcity.RunSmartcity ./scenarioMasa/config.xml
#     sed -i -e "s/plans$i/TO_SET/g" ./scenarioMasa/config.xml
#     sed -i -e "s/output$i/OUTPUT_DIR/g" ./scenarioMasa/config.xml
# done

java -cp smartcity-0.0.1-SNAPSHOT.jar:lib/* org.matsim.contrib.smartcity.RunSmartcity mappe_matsim/1k/config.xml
java -cp smartcity-0.0.1-SNAPSHOT.jar:lib/* org.matsim.contrib.smartcity.RunSmartcity mappe_matsim/2k/config.xml
java -cp smartcity-0.0.1-SNAPSHOT.jar:lib/* org.matsim.contrib.smartcity.RunSmartcity mappe_matsim/3k/config.xml
java -cp smartcity-0.0.1-SNAPSHOT.jar:lib/* org.matsim.contrib.smartcity.RunSmartcity mappe_matsim/4k/config.xml
java -cp smartcity-0.0.1-SNAPSHOT.jar:lib/* org.matsim.contrib.smartcity.RunSmartcity mappe_matsim/5k/config.xml
java -cp smartcity-0.0.1-SNAPSHOT.jar:lib/* org.matsim.contrib.smartcity.RunSmartcity mappe_matsim/6k/config.xml