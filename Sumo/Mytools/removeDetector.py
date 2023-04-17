import xml.etree.ElementTree as ET
import datetime,time,sys
file2 = sys.argv[1]
det_tree =  ET.parse(file2)
det_root = det_tree.getroot()

element = [ 
 'e2_191',
 'e2_441',
 'e2_480',
 'e2_549',
 'e2_567',
 'e2_587',
 'e2_600',
 'e2_602',
 'e2_603',
 'e2_604',
 'e2_839',
 'e2_840',
 'e2_1238',
 'e2_1239',
 'e2_1611',
 'e2_1613',
 'e2_1655',
 'e2_1657',
 'e2_2111',
 'e2_2223',
 'e2_3139',
 'e2_3140',
 'e2_3141',
 'e2_3142',
 'e2_3143',
 'e2_3144',
 'e2_3146',
 'e2_3147',
 'e2_3150',
 'e2_3151',
 'e2_3152',
 'e2_3153',
 'e2_3154',
 'e2_3155',
 'e2_3157',
 'e2_3158',
 'e2_3161',
 'e2_3162',
 'e2_3163',
 'e2_3164',
 'e2_3234',
 'e2_3245',
 'e2_3249',
 'e2_3497',
 'e2_3498',
 'e2_3568',
 'e2_3579',
 'e2_3774',
 'e2_3854',
 'e2_3855',
 'e2_3966',
 'e2_3968',
 'e2_4438',
 'e2_4623',
 'e2_4711',
 'e2_4713',
 'e2_4718',
 'e2_4719',
 'e2_4725',
 'e2_4726',
 'e2_4736',
 'e2_4737',
 'e2_4860',
 'e2_4866',
 'e2_4879',
 'e2_4881',
 'e2_5231',
 'e2_5232',
 'e2_5242',
 'e2_5243',
 'e2_5271',
 'e2_5272',
 'e2_6149', ]
ed =det_root.findall("laneAreaDetector")
for lane in ed:
    if lane.get("id") in element:
        elem = lane
        if elem is not None:
            det_root.remove(elem)
det_tree.write(file2)






































































































































































































 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 






