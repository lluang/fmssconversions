import scipy as sp
from scipy.stats import expon, erlang
import matplotlib.pyplot as plt
import simpy


def source(env, interval, receptionist):
    i = 0
    while True:
        i += 1
        c = patient(env, 'patient%02d' % i, receptionist, time_service=12.0)
        env.process(c)
        t = expon.rvs(0, 1.0/G.ARRint, size=1)
        yield env.timeout(t)


def patient(env, name, receptionist, time_service):
    arrive = env.now
    with receptionist.request() as req:
        yield req
        timenow = env.now
        wait = timenow - arrive
        env.arrival.append(timenow)
        env.waittime.append(wait)
        tis = erlang.rvs(G.phases,
                         scale=float(G.timeReceptionist)/G.phases, size=1)
        yield env.timeout(tis)
    return


#  Experiment data -------------------------
class G:
    RANDOM_SEED = 42
    INTERVAL_patientS = 10.0  # Generate new patients roughly every x seconds
    maxTime = 100.0         # minutes
    timeReceptionist = 0.8  # mean, minutes
    phases = 3
    ARRint = 1.0            # mean, minutes
    theseed = 99999


class HospitalSim(object):
    def run(self, aseed):
        print('Hospital simulation')
        sp.random.seed(aseed)
        env = simpy.Environment()
        env.arrival = []
        env.waittime = []
        receptionist = simpy.Resource(env, capacity=1)
        env.process(source(env, G.INTERVAL_patientS, receptionist))
        env.run(until=G.maxTime)
        return env


if __name__ == "__main__":
    hospital = HospitalSim()
    results = hospital.run(G.theseed)
    print(sp.mean(results.waittime))
    print(sp.std(results.waittime))
    plt.hist(results.waittime)
