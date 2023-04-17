import sys
import xml.etree.ElementTree as ET


file = sys.argv[1] #.add.xml
file2 =sys.argv[2] #cameras.xml

det_tree =  ET.parse(file2)
det_root = det_tree.getroot()
tree = ET.parse(file)
root = tree.getroot()
lane = []
det = det_root.findall("laneAreaDetector")

for elem in det:
    lane.append(elem.get("lane"))

lane = [s.replace("_0", "") for s in lane]
detector =  ET.Element("cameras") 
detector.set("all","false")
n = int(len(lane)/4)
print(n)
for i in range(0,n):
   
    camera = ET.Element("camera")
    camera.set("class","org.matsim.contrib.smartcity.perception.camera.PassiveCamera")
    camera.set("id","camera"+str(i))
    camera.set("link",lane[i])
    camera.text="\n"
    detector.append(camera)
    detector.text = "\n"
root.append(detector)
tree.write(file, encoding="unicode", method="xml", xml_declaration=True)
# cameras = root.findall("cameras")
# camera = []
# for elem in cameras:
#     camera.append(elem.find("camera"))
# for elem in camera:
#     print(elem.get("id"))