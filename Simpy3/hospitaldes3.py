import pandas as pd
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

# collect data by arrival

def patient(env, name, receptionist, time_service):
    arrive = env.now
    env.arrival.append(arrive)
    with receptionist.request() as req:
        yield req
        start = env.now
        wait = start - arrive
        env.waittime.append(wait)
        tis = erlang.rvs(G.phases,
                         scale=float(G.timeReceptionist)/G.phases, size=1)
        yield env.timeout(tis)
        complete = env.now
        flow = complete - arrive
        env.flowtime.append(flow)
        mytimes = {'arrive': arrive, 'start': start, 'leave':complete, 
           'flowtime':flow, 'waittime': wait}
        env.patientdata.loc[len(env.patientdata)] = mytimes
    return


#  Experiment data -------------------------
class G:
    RANDOM_SEED = 42
    INTERVAL_patientS = 10.0  # Generate new patients roughly every x seconds
    maxTime = 480.0         # minutes
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
        env.flowtime = []
        env.patientdata = pd.DataFrame(columns = ['arrive', 'start', 'leave', 'flowtime', 'waittime'])
        receptionist = simpy.Resource(env, capacity=1)
        env.process(source(env, G.INTERVAL_patientS, receptionist))
        env.run(until=G.maxTime)
        return env


if __name__ == "__main__":
    hospital = HospitalSim()
    results = hospital.run(G.theseed)
    flowtime = [val for sublist in results.patientdata['flowtime'] for val in sublist]
    waittime = [val for sublist in results.patientdata['waittime'] for val in sublist]
    print(sp.mean(results.patientdata['waittime']))
    print(sp.std(results.patientdata['waittime']))
    plt.hist(flowtime)
    #plt.hist(results.patientdata['flowtime'])
