# -*- coding: utf-8 -*-
"""
Discrete event simulation of hospital receptionist

@author: lluang
"""

#  DES version
import scipy as sp
import simpy
from scipy.stats import expon, erlang


# Model components ------------------------
class Arrivals(object):
    """ Source generates customers randomly """

    def generate(self, env, meanTBA, resource):
        i = 0
        while env.now() < G.maxTime:
            c = Patient(name="Patient%02d" % (i), env=env)
            self.sim.activate(c, c.visit(b=resource))
            t = expon.rvs(0, 1.0 / meanTBA, size=1)
            yield env.timeout(t)
            i += 1


class Patient(object):
    """ Patient arrives, is served and leaves """

    def visit(self, env, b):
        arrive = env.now()
        yield b.request()
        wait = env.now() - arrive
        tib = erlang.rvs(G.phases,
                         scale=float(G.timeReceptionist)/G.phases, size=1)
        yield env.timeout(tib)
        yield b.release()


# Experiment data -------------------------
class G:
    maxTime = 100.0    # minutes
    timeReceptionist = 0.8  # mean, minutes
    phases = 3
    ARRint = 1.0      # mean, minutes
    theseed = 99999


# Model/Experiment ------------------------------
class Hospitalsim(object):
    def run(self, env, theseed):
        sp.random.seed(theseed)
        k = simpy.Resource(env, name="Reception", unitName="Receptionist")
        self.initialize()
        s = Arrivals('Source', sim=self)
        self.activate(s, s.generate(meanTBA=G.ARRint, resource=k), at=0.0)
        self.simulate(until=G.maxTime)


env = simpy.Environment()
mg1 = Hospitalsim()
mg1.run(env, 4321)
