for i in `seq 100 100 5000` `seq 6000 1000 9000`
do
    mkdir output$i
    sed -i -e "s/TO_SET/plans$i/g" ./prove/masa/config.xml
    sed -i -e "s/OUTPUT_DIR/output$i/g" ./prove/masa/config.xml
    java -Xmx6g -cp ./run.jar org.matsim.contrib.smartcity.RunSmartcity ./prove/masa/config.xml
    sed -i -e "s/plans$i/TO_SET/g" ./prove/masa/config.xml
    sed -i -e "s/output$i/OUTPUT_DIR/g" ./prove/masa/config.xml
done
