import random
import simpy
from MonitoredResource import MonitoredResource
RANDOM_SEED = 42
NUM_MACHINES = 2  # Number of machines in the carwash
WASHTIME = 3      # Minutes it takes to clean a car
T_INTER = 2       # Car interarrival interval
NUM_CARS = 10      # Number of cars in simulation

def car(env, name, bcs, driving_time, charge_duration):
    # Simulate driving to the BCS
    yield env.timeout(driving_time)
    # Request one of its charging spots
    print('%s arriving at %d' % (name, env.now))
    with bcs.request() as req:
        yield req
        # Charge the battery
        print('%s starting to charge at %s' % (name, env.now))
        yield env.timeout(charge_duration)
        print('%s leaving the bcs at %s' % (name, env.now))
# Setup and start the simulation
print('Carwash with monitor')
random.seed(RANDOM_SEED)
# Create an environment and start car arrivals
env = simpy.Environment()
carwash = MonitoredResource(env, capacity=NUM_MACHINES)
t = 0
for i in range(1, NUM_CARS + 1):
    tnext = t + random.randint(max(0, T_INTER - 2), T_INTER + 2)
    washtime = random.randint(WASHTIME - 1, WASHTIME + 1)
    env.process(car(env, 'Car %d' % i, carwash, tnext, washtime))
# Execute
env.run()

# Calculate statistics
carwash.updatestats()
carwash.carwashdata[0:5]
carwash.elapseddata[0:5]

print("------OUTPUT------")
print("Number of car wash stations: " + str(NUM_MACHINES))
print("Number of cars: " + str(carwash.countcars()))
print("Average and variance of car wash use: %5.3f, %5.3f" %
      (carwash.actTimeAverage(), carwash.actTimeVariance()))
print("Average and variance of cars in queue: %5.3f, %5.3f" %
      (carwash.waitTimeAverage(), carwash.waitTimeVariance()))
