# -*- coding: utf-8 -*-
# Hospital reception model
# ========================
#
# 1.  Apply Lindley's equation to make an approximation
# 2.  Model hospital as a M/G/1 Queue

import math
import numpy as np
#  Use scipy.stats because it includes the Erlang distribution
from scipy.stats import expon, erlang
import matplotlib.pyplot as plt

def lindley(m=55000, d = 5000):
    ''' Estimates waiting time with m customers, discarding the first d customers

        Lindley approximation for waiting time in a M/G/1 queue
    '''
    replications = 10
    lindley = []
    for rep in range(replications):
        y = 0
        SumY = 0
        for i in range(1, d):
            # Random number variable generation from scipy.stats
            # shape = 0, rate =1, 1 value
            a = expon.rvs(0, 1)
            # rate = .8/3, shape = 3
            x = erlang.rvs(3, scale = 0.8/3, size=1)
            y = max(0, y + x - a)
        for i in range(d, m):
            a = expon.rvs(0, 1)
            # rate = .8/3, shape = 3
            x = erlang.rvs(3, scale = 0.8/3, size=1)
            y = max(0, y + x - a)
            SumY += y
        result = SumY / (m - d)
        lindley.append(result)
    return lindley

np.random.seed(1234)
result = lindley()
print(result)
print(np.mean(result))
print(np.std(result))

for i in range(len(result)):
    print ("%1d & %11.9f " % (i+1, result[i]))
print("average & %11.9f" % (np.mean(result)))
print("std dev & %11.9f" % (np.std(result)))

import csv

with open("lindley.csv", "wb") as myFile:
    lindleyout = csv.writer(myFile)
    lindleyout.writerow(["Waitingtime"])
    lindleyout.writerows(result)

#  DES version

import SimPy.Simulation as Sim
import numpy as np
#from scipy.stats import expon, erlang
from random import seed

## Model components ------------------------

class Arrivals(Sim.Process):
    """ Source generates customers randomly """

    def generate(self, meanTBA, resource):
        i=0
        while self.sim.now() < G.maxTime:
            c = Patient(name="Patient%02d" % (i), sim=self.sim)
            self.sim.activate(c, c.visit(b=resource))
            t = expon.rvs(0, 1.0 / meanTBA, size = 1)
            yield Sim.hold, self, t
            i+=1

class Patient(Sim.Process):
    """ Patient arrives, is served and leaves """

    def visit(self, b):
        arrive = self.sim.now()
        yield Sim.request, self, b
        wait = self.sim.now() - arrive
        tib = erlang.rvs(G.phases, scale = float(G.timeReceptionist)/G.phases, size = 1)
        yield Sim.hold, self, tib                            #2
        yield Sim.release, self, b

## Experiment data -------------------------
class G:
    maxTime = 100.0    # minutes
    timeReceptionist = 0.8  # mean, minutes
    phases = 3
    ARRint = 1.0      # mean, minutes
    theseed = 99999

## Model/Experiment ------------------------------
class Hospitalsim(Sim.Simulation):
    def run(self, theseed):
        np.random.seed(theseed)
        k = Sim.Resource(name="Reception", unitName="Receptionist",
                        monitored=True, sim=self)
        self.initialize()
        s = Arrivals('Source', sim=self)
        self.activate(s, s.generate(meanTBA=G.ARRint, resource=k), at=0.0)
        self.simulate(until=G.maxTime)

mg1 = Hospitalsim()
mg1.run(4321)
