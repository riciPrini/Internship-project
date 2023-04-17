import matplotlib.pyplot as plt

with open("times.txt", "r") as f:
    text=f.read().split(',')
    Y=[]
    Y=[text[i] for i in range(0,len(text),2)]
    X_SUMO=[text[i] for i in range(1,len(text),2)]

with open("/home/riccardo/Scrivania/UNIVERSITA/tirocinio/per_prini/time.txt", "r") as f2:
    text=f2.read().split(',')
    X_MAT=[text[i] for i in range(1,len(text),2)] 

plt.plot(Y,X_SUMO)
plt.plot(Y,X_MAT)

plt.show()