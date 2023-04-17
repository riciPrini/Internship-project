import xml.etree.ElementTree as ET
import datetime,time

file='/home/riccardo/Scrivania/UNIVERSITA/tirocinio/eclipse-mosaic-22.1/scenarios/MATSIM_vs_SUMO/sumo/15k.rou.xml'
tree = ET.parse(file)
root = tree.getroot()
root[:] = sorted(root, key=lambda child: (child.tag,child.get('depart')))
for trip in root:
    print(trip.attrib) 
tree.write(file)