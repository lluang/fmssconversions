import csv
import scipy as sp
from scipy.stats import expon, erlang

def lindley(m=5500, d=500):
    replications = 10
    lindley = []
    for rep in range(replications):
        y = 0
        SumY = 0
        for i in range(1, d):
            a = expon.rvs(0, 1)
            x = erlang.rvs(3, scale=0.8/3, size=1)
            y = max(0, y + x - a)
        for i in range(d, m):
            a = expon.rvs(0, 1)
            x = erlang.rvs(3, scale=0.8/3, size=1)
            y = max(0, y + x - a)
            SumY += y
        result = SumY / (m - d)
        lindley.append(result)
    return lindley

sp.random.seed(1234)
result = lindley()
print(result)
print(sp.mean(result))
print(sp.std(result))
print("run, waiting time")
for i in range(len(result)):
    print("%1d & %11.9f " % (i+1, result[i]))
print("average & %11.9f" % (sp.mean(result)))
print("std dev & %11.9f" % (sp.std(result)))

with open("lindley.csv", "w", newline='') as myFile:
    lindleyout = csv.writer(myFile)
    lindleyout.writerow(["Waitingtime"])
    for row in result:
        lindleyout.writerow(row)
