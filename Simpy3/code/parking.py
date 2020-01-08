# -*- coding: utf-8 -*-
# Here we consider the parking lot example of Sect. 3.1, a queueing system with timevarying
# car arrival rate, exponentially distributed parking time and an infinite number
# of parking spaces. The simulation program consists of some global declarations
# (Fig. 4.1), a main program and some event routines (Fig. 4.2), an initialization sub
# (Fig. 4.3), a function to generate car arrivals (Fig. 4.4), and the support functionality
# provided by VBASim. Two important aspects of VBASim are illustrated by this
# model: event scheduling and collecting time-averaged statistics.

import random, math
import numpy as np
import scipy as sp
import scipy.stats as stats
import matplotlib.pyplot as plt
import SimPy.Simulation as Sim

## Model components ------------------------

class Arrival(Sim.Process):
    """ Source generates cars at random

        Arrivals are at a time-dependent rate
    """

    def generate(self):
        i=0
        while (self.sim.now() < G.maxTime):
            tnow = self.sim.now()
            arrivalrate =  100 + 10 * math.sin(math.pi * tnow/12.0)
            t = random.expovariate(arrivalrate)
            yield Sim.hold, self, t
            c = Car(name="Car%02d" % (i), sim=self.sim)
            timeParking = random.expovariate(1.0/G.parkingtime)
            self.sim.activate(c, c.visit(timeParking))
            i += 1

class Car(Sim.Process):
    """ Cars arrives, parks for a while, and leaves

        Maintain a count of the number of parked cars as cars arrive and leave
    """

    def visit(self, timeParking=0):
        self.sim.parkedcars += 1
        self.sim.parking.observe(self.sim.parkedcars)
        yield Sim.hold, self, timeParking
        self.sim.parkedcars -= 1
        self.sim.parking.observe(self.sim.parkedcars)

## Experiment data ------------------------------
class G:
    maxTime = 24.0  # hours
    arrivalrate = 100 # per hour
    parkingtime = 2.0 # hours
    seedVal = 9999

## Model/Experiment ------------------------------
class Parkingsim(Sim.Simulation):
    def run(self, aseed):
        random.seed(aseed)
        Sim.initialize()
        self.parkedcars = 0
        s = Arrival(name='Arrivals', sim=self)
        self.parking = Sim.Monitor(name='Parking', ylab='cars', tlab='time', sim=self)
        self.activate(s, s.generate(), at=0.0)
        self.simulate(until=G.maxTime)

parkinglot = Parkingsim()
parkinglot.run(4321)

plt.figure(figsize=(5.5,4))
plt.plot(parkinglot.parking.tseries(),parkinglot.parking.yseries())
plt.xlabel('Time')
plt.ylabel('Number of cars')
plt.xlim(0, 24)

initialseed = 4321
parkingdemand = []
daysrep = 2000
for i in range(daysrep):
    parkinglot.initialize()
    parkinglot.run(initialseed + i)
    parkingdemand.append(parkinglot.parking)

averagedailyparking = [parkingdemand[i].timeAverage() for i in range(daysrep)]
maxparkingdailyparking = [max(parkingdemand[i].yseries()) for i in range(daysrep)]

plt.clf()
plt.hist(averagedailyparking, bins=25, cumulative = True, label = 'Average number of cars during day')
plt.xlabel('Average number of cars in day')
plt.ylabel('Days (cumulative)')
plt.show()

plt.clf()
plt.hist(maxparkingdailyparking, bins=25, cumulative = False)
plt.xlabel('Maximum number of cars')
plt.ylabel('Days (cumulative)')
plt.show()

plt.clf()
testvalue, pvalue = stats.normaltest(averagedailyparking)
print(pvalue)
stats.probplot(averagedailyparking, plot=plt)
plt.show()
