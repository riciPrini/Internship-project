import json,os

dir_path = os.path.dirname(os.path.realpath(__file__))+"/mapping_config.json"

data = {}
os.system('cls' if os.name == 'nt' else 'clear')
with open(dir_path, 'r') as fcc_file:
    data = json.load(fcc_file)

numClass = int(input("Num class vehicles: "))
item=types=[]
D2=data["vehicles"][0].copy()
D=data["vehicles"][0]["types"][0].copy()



for i in range(0,numClass):
    os.system('cls' if os.name == 'nt' else 'clear')
        
        
    # NameClass
    # hook_2= D.copy()
    # hook_2["name"] = input("Name Class vehicles (default : \"Car\"): ")or "Car"
    # className = hook_2["name"]
    # types.insert(i,hook_2)
    
    hook = D2.copy()
    # Starting Time
    hook["startingTime"]=float(input(f"Starting time vehicles (default : 0.0): ")or "0.0")
    # TargetFlow
    hook["targetFlow"]=int(input(f"Flow vehicles (default : 2000): ") or "2000")
    # Numero veicoli
    hook["maxNumberVehicles"]=int(input(f"Max number of vehicles (default : 10): ") or "10")
    # Route
    hook["route"]=input(f"Vehicles route: ") or ""
    item.insert(i,hook)

print(item)


data["vehicles"] = item

# print("---------------------------------------------------------")
# print(data)

with open(dir_path, "w") as outfile:
    json.dump(data, outfile, indent=2)

