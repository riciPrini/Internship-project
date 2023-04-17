import xml.etree.ElementTree as ET
import datetime,time,sys

## file1 --> .net.xml 
# file2 --> .add.xml 
#file 3 --> n
file =sys.argv[1]
file2 = sys.argv[2]
file3 =sys.argv[3]
tree = ET.parse(file)
root = tree.getroot()
edges =root.findall("edge")

def set_length(length):
    res = int(float(length)/2)
    if res <=1:
        return 2
    else:
        return res

lanes = []
for edge in edges:
    if not edge.get("id").startswith(":"):
        lanes.append(edge.find("lane"))
det_tree =  ET.parse(file2)
det_root = det_tree.getroot()
n = int(file3)
print(int(len(lanes)/n))
for i in range(1,n):
    detector =  ET.Element("laneAreaDetector") 
    detector.set("id","e2_"+str(i))
    detector.set("lane",lanes[i].get("id"))
    detector.set("pos", "00.00")
    detector.set("length",str(set_length(lanes[i].get("length"))) + ".00")
    detector.set("file","e2_5000.xml")
    detector.text = "\n"
    det_root.append(detector)

det_tree.write(file2, encoding="unicode", method="xml", xml_declaration=True)