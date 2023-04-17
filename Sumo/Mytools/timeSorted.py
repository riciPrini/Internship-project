import xml.etree.ElementTree as ET
import datetime,time,sys

#file='/home/riccardo/Scrivania/UNIVERSITA/tirocinio/eclipse-mosaic-22.1/scenarios/MATSIM_vs_SUMO/sumo/plans/7k.rou.xml'
file =sys.argv[1]
tree = ET.parse(file)
root = tree.getroot()
root[:] = sorted(root, key=lambda child: (child.tag,child.get('depart')))
for trip in root:
    print(trip.attrib) 
tree.write(file)
print(sys.argv[1])