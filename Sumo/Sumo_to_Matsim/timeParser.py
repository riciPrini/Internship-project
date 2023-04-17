import xml.etree.ElementTree as ET
import datetime,time

file='plans/plans5000.rou.xml'
tree = ET.parse(file)
root = tree.getroot()
root[:] = sorted(root, key=lambda child: (child.tag,child.get('depart')))
for trip in root:
    print(trip.attrib) 
tree.write(file)