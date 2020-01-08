import random
import math
import scipy.stats
import simpy

# Model components ------------------------
def Arrival(env):
    i = 0
    while (env.now < G.maxTime):
        tnow = env.now
        arrivalrate = 100 + 10 * math.sin(math.pi * tnow/12.0)
        t = scipy.stats.expon.rvs(0, arrivalrate)
        yield env.timeout(t)
        timeparking = scipy.stats.expon(0, 1.0/G.parkingtime)
        c = Car(env, "Car%02d" % i, timeparking)
        env.process(c)
        print("Car %i parked from %3.1f to %3.1f" (i, tnow, tnow + timeparking))
        i += 1

def Car(env, name, timeparking=0):
    G.parkedcars += 1
    G.parking[env.now] = G.parkedcars
    yield env.timeout(timeparking)
    G.parkedcars -= 1

# Experiment data ------------------------------
class G:
    maxTime = 24.0  # hours
    arrivalrate = 100  # per hour
    parkingtime = 2.0  # hours
    seedVal = 9999
    parkedcars = 0
    parking = {}
    RANDOM_SEED = 1234

random.seed(G.RANDOM_SEED)
env = simpy.Environment()
env.process(Arrival(env))
env.run(until=24)
parking_time = []
parking_cars = []
parkingdemand = 0
lasti = 0
for i in sorted(G.parking.keys()):
    parking_time.append(i)
    parking_cars.append(G.parking[i])
    parkingdemand = parkingdemand + G.parking[i]*(i-lasti)
    lasti = i
print(parkingdemand/24.)
