import xml.etree.ElementTree as ET
import datetime,time

file = '/home/riccardo/Scrivania/UNIVERSITA/tirocinio/eclipse-mosaic-22.1/scenarios/MATSIM_vs_SUMO/sumo/plans/10k.rou.xml'
tree = ET.parse(file)
root = tree.getroot()

root[:] = sorted(root, key=lambda child: (child.tag,child.get('depart')))

L=[]
for trip in root:
    print(trip.attrib)
    if(trip.get("depart") == "None"):
        trip.set("depart","0")
    elif (trip.get("id") !="car" and trip.get("id")!="bicycle"):
        timestamp = (str(trip.get("depart")))  
        ftr = [3600,60,1]
        timeParsed=str(sum([a*b for a,b in zip(ftr, map(int,timestamp.split(':')))]))+".00"
        trip.set("depart",timeParsed)
    if (trip.get("id") !="car" and trip.get("id")!="bicycle"):
        L.insert(0,trip.attrib)



     


L=sorted(L, key=lambda x: x['depart'])
print(L) 
tree.write(file)