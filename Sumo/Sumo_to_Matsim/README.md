# Visualization
Da fare nella cartella Sumo_to_Matsim
## Vehroute

```python3 ../../tools/visualization/plotXMLAttributes.py -x depart -y arrival -s -o img/vehroute.png output/vehroute.xml --scatterplot```

## Tripinfo

```python3 ../../tools/visualization/plot_tripinfo_distributions.py -i output/tripinfos.xml -o img/tripinfo.png --measure waitingCount --bins 10 --maxV 10  --xlabel "number of stops [-]" --ylabel "count [-]" --title "distribution of number of stops" --colors blue -b --no-legend```

## Summary

```python3 ../../tools/visualization/plotXMLAttributes.py output/summary.xml -x time -y running -o img/summary.png -i collisions --legend```

```python3 ../../tools/visualization/plot_summary.py -i output/100Veh/summary.xml,output/500Veh/summary.xml,output/2000Veh/summary.xml,output/5000Veh/summary.xml -l 100Veh,500Veh,2000Veh,5000Veh -o ../sum_all.png --ylabel "running vehicles [#]" --xlabel "time" --title "running vehicles over time"``
