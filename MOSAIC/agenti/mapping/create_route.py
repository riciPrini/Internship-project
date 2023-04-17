import json,os
import sys
import xml.etree.ElementTree as ET

#file route.rou.xml
#file2 rot.rou.xml

file = sys.argv[1]
file2 = sys.argv[2]
file3 = sys.argv[3]
tree = ET.parse(file)
root = tree.getroot()
det = root.findall("vehicle")
dir_path = os.path.dirname(os.path.realpath(__file__))+"/mapping_config.json"
route =[]
for elem in det:
    route.append(elem.find("route"))
##print(route)
i = 0
for elem in route:
    elem.set("id","r_"+str(i))
    i+=1

det_tree =  ET.parse(file2)
det_root = det_tree.getroot()
n = int(file3)



for i in range(0,n):
    det_root.append(route[i])
det_tree.write(file2, encoding="unicode", method="xml", xml_declaration=True)
# data = {}
# os.system('cls' if os.name == 'nt' else 'clear')
# with open(dir_path, 'r') as fcc_file:
#     data = json.load(fcc_file)
# lad=data["tmcs"][0]["laneAreaDetectors"]
# l = []
# for elem in det:
#     l.append(elem.get("id"))
# data["tmcs"][0]["laneAreaDetectors"]=l
# print(data["tmcs"][0]["laneAreaDetectors"])