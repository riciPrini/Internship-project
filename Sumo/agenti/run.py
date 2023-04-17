import os
import sys
import optparse
import time

# we need to import some python modules from the $SUMO_HOME/tools directory
if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools)
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")


from sumolib import checkBinary  # Checks for the binary in environ vars
import traci


def get_options():
    opt_parser = optparse.OptionParser()
    opt_parser.add_option("--nogui", action="store_true",
                         default=False, help="run the commandline version of sumo")
    options, args = opt_parser.parse_args()
    return options


# contains TraCI control loop
def run():
    step = 0
    # traci.vehicle.add("v2","route_0","car")
    # traci.vehicle.add("v3","route_0","car")
    # traci.vehicle.add("v4","route_1","car2")
    # traci.vehicle.add("v5","route_1","car2")
    print(traci.simulation.getTime())
    
    while traci.simulation.getMinExpectedNumber() > 0:
        traci.simulationStep()
        
        # 
            #print(traci.calibrator.getVehsPerHour("f_0"))
            # traci.calibrator.setFlow("f_0",traci.simulation.getTime(),32400.0,500)
            # traci.calibrator.setFlow("f_1",traci.simulation.getTime(),32400.0,500)
            # traci.calibrator.setFlow("f_2",traci.simulation.getTime(),32400.0,500)
            # traci.calibrator.setFlow("f_3",traci.simulation.getTime(),32400.0,500)
        
        # if traci.lanearea.getJamLengthVehicle("e2_1") >= 2: # controlla se il detector è congestionato
        #     #traci.lane.setDisallowed("E0_0","custom1") # Disabilita il passaggio per quella lane congestionata
        #     traci.trafficlight.setPhase("J0",2) # Setto la fase del semaforo a green
        #     traci.vehicle.rerouteEffort("v1") #ricalcola il percorso per il veicolo1
        #     traci.vehicle.rerouteEffort("v2") #ricalcola il percorso per il veicolo2
        #     traci.vehicle.rerouteEffort("v3") #ricalcola il percorso per il veicolo3
            
            
            


        
       
        # # print(step)

        # # det_vehs = traci.inductionloop.getLastStepVehicleIDs("det_0")
        # # for veh in det_vehs:
        # #     print(veh)


        # # if step == 100:
        # #     traci.vehicle.changeTarget("1", "e9")
        # #     traci.vehicle.changeTarget("3", "e9")

        step += 1

    traci.close()
    sys.stdout.flush()

def printTime(time,Num):
    with open("times.txt", "a") as file_object:
    # Append 'hello' at the end of file
        file_object.write(f"{Num},{time},")
# main entry point
if __name__ == "__main__":
    options = get_options()

    # check binary
    if options.nogui:
        sumoBinary = checkBinary('sumo')
    else:
        sumoBinary = checkBinary('sumo')

    # traci starts sumo as a subprocess and then this script connects and runs
    
    file=sys.argv[1]
    num = sys.argv[2]
    traci.start([sumoBinary, "-c", file])
    
    startTime = time.time()
    run()
    endTime = time.time()
    elapsed=endTime-startTime
    print(elapsed)
    printTime(elapsed,num)