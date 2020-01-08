# -*- coding: utf-8 -*-
# A service center receives faxed orders throughout the day,
# with the rate of arrival varying hour by hour. The arrivals are
# modeled by a nonstationary Possion process with the rates
# shown in Table~\ref{tab:fax.arrivals}. \index{nonstationary Poisson
# process}
#
#
#
# A team of Entry Agents select faxes on a first-come-first-served basis
# from the fax queue. Their time to process a fax is modeled as normally
# distributed with mean $2.5$ minutes and standard deviation $1$ minute.
# There are two possible outcomes after the Entry Agent finishes
# processing a fax: either it was a simple fax and the work on it is
# complete, or it was not simple and it needs to go to a Specialist for
# further processing.  Over the course of a day, approximately 20\% of
# the faxes require a Specialist.  The time for a Specialist to process
# a fax is modeled as normally distributed with mean $4.0$ minutes and
# standard deviation $1$ minute.
#
# Minimizing the number of staff minimizes cost, but certain
# service-level requirements much be achieved.  In particular, 96\% of
# all simple faxes should be completed within $10$ minutes of their
# arrival, while 80\% of faxes requiring a Specialist should also be
# completed (by both the Entry Agent and the Specialist) within $10$
# minutes of their arrival.
#
# The service center is open from 8 AM to 4 PM daily, and it is possible
# to change the staffing level at 12 PM. Thus, a staffing policy
# consists of four numbers: the number of Entry Agents and Specialists
# before noon, and the number of Entry Agents and Specialists after
# noon.  Any fax that starts its processing before noon completes
# processing by that same agent before the agent goes off duty; and
# faxes in the queues at the end of the day are processed before the
# agents leave work and therefore are not carried over to the next day.

# <rawcell>

# Table: Arrival rate of faxes by hour
#
# |  Time        | Rate (faxes/minute)  |
# |--------------|---------------------:|
# | 8 AM--9 AM   | 4.37                 |
# | 9 AM--10 AM  | 6.24                 |
# | 10 AM--11 AM | 5.29                 |
# | 11 AM--12 PM | 2.97                 |
# | 12 PM--1 PM  | 2.03                 |
# | 1 PM--2 PM   | 2.79                 |
# | 2 PM--3 PM   | 2.36                 |
# | 3 PM--4 PM   | 1.04                 |

import random, math
import numpy as np
import SimPy.Simulation as Sim

## Model components ------------------------


class Source(Sim.Process):
    """ Source generates customers randomly """

    def generate(self, resourcenormal, resourcespecial, resourcenormalPM, resourcespecialPM):
        i = 0
        while(self.sim.now() < F.nPeriods and self.sim.meanTBA>0):
            f = Fax(name="Fax%02d" % (i), sim=self.sim)
            print("%8.4f %s: Generate a fax" % (self.sim.now(), f.name))
            self.sim.activate(f,  \
                f.visit(aAM=resourcenormal, sAM = resourcespecial, aPM=resourcenormalPM, sPM = resourcespecialPM))
            t = random.expovariate(1.0 / self.sim.meanTBA)
            yield Sim.hold, self, t
            i += 1


class Fax(Sim.Process):
    """ Fax arrives and is prcoesed

        Processed first by regular staff, then specialized staff if needed
        Note that after the shift change time, a different set of resources are used.
        Assume that anyone working on a fax will continue working until it finishes.
        This is an approximation because in real life it could be the same person, but since any individual fax
        does not take long it does not change the results much.
    """

    def visit(self, aAM, sAM, aPM, sPM):
        arrive = self.sim.now()
        print("%8.4f %s: Here I am     " % (self.sim.now(), self.name))
        # Check if shift change
        if (arrive < F.tPMshiftchange):
            agent = aAM
        else:
            agent = aPM
        yield Sim.request, self, agent
        wait = self.sim.now() - arrive
        print("%8.4f %s: Waited %6.3f" % (self.sim.now(), self.name, wait))
        tis = -1.0
        while (tis < 0):
            tis = random.normalvariate(F.meanRegular, F.stdRegular)
        yield Sim.hold, self, tis
        yield Sim.release, self, agent
        checkSpecial = random.random()
        # Check if shift change
        if (checkSpecial < F.pSpecial):
            if (self.sim.now() < F.tPMshiftchange):
                specialagent = sAM
            else:
                specialagent = sPM
            print("%8.4f %s: I'm special" % (self.sim.now(), self.name))
            tspecial = -1.0
            while (tspecial < 0):
                tspecial = random.normalvariate(F.meanSpecial, F.stdSpecial)
            yield Sim.request, self, specialagent
            yield Sim.hold, self, tspecial
            yield Sim.release, self, specialagent
            finished = self.sim.now() - arrive
            self.sim.Special10.observe(finished)
        else:
            finished = self.sim.now() - arrive
            self.sim.Regular10.observe(finished)
        print("%8.4f %s: Finished" % (self.sim.now(), self.name))

class Arrival(Sim.Process):
    """ update the arrival rate every hour

        Reads in the arrival rate table and updates the arrival rate every hour
        One hour after the last hour begins, changes arrival rate to 0
    """
    def generate(self, arrivalrate):
        for i in range(len(arrivalrate)):
            self.sim.meanTBA = 1.0/(arrivalrate[i])
            yield Sim.hold, self, 1.0
        # After the end of the day, set the arrival rate = 0
        self.sim.meanTBA = 0.0

class SecondShift(Sim.Process):
    """ Trigger the change in shifts for agents

        The effect should be to move the wait queue to the new set of agents

    """
    def generate(self, tshiftchange):
        yield Sim.hold, self, tshiftchange

## Experiment data -------------------------
class F:
    maxTime = 100    # hours
    theseed = 9999

    period = 60.0
    nPeriods = 8
    meanRegular = 2.5/period # hours
    varRegular = 1.0/period # hours
    stdRegular = math.sqrt(1.0)/period
    meanSpecial = 4.0/period # hours
    varSpecial = 1.0/period # hours
    stdSpecial = math.sqrt(1.0)/period

    tPMshiftchange = 4.0
    numAgents = 15
    numAgentsPM = 9
    numSpecialists = 6
    numSpecialistsPM = 3

    maxRate = 6.24
    aRate= [4.37, 6.24, 5.29, 2.97, 2.03, 2.79, 2.36, 1.04] # per minute
    aRateperhour = [aRate[i] * period for i in range(len(aRate))] # per hour
    meanTBA = 1/(maxRate * period) # hours
    pSpecial = 0.20

## Model/Experiment ------------------------------

class Faxcentersim(Sim.Simulation):
    def run(self, aseed):
        random.seed(aseed)
        agents = Sim.Resource(capacity=F.numAgents,
            name="Service Agents", unitName="Agent", monitored = True, sim=self)
        specialagents = Sim.Resource(capacity=F.numSpecialists,
            name="Specialist Agents", unitName="Specialist", monitored=True, sim=self)
        agentspm = Sim.Resource(capacity=F.numAgentsPM,
            name="Service AgentsPM", unitName="Agent", monitored = True, sim=self)
        specialagentspm = Sim.Resource(capacity=F.numSpecialistsPM,
            name="Specialist AgentsPM", unitName="Specialist", monitored=True, sim=self)

        self.meanTBA = 0.0
        self.initialize()
        s = Source('Source', sim=self)
        a = Arrival('Arrival Rate', sim=self)
        tchange = SecondShift('PM Shift', sim=self)
        self.Regular10 = Sim.Monitor(name="Regular time", ylab='hours', sim=self)
        self.Special10 = Sim.Monitor(name="Special time", ylab='hours', sim=self)
        self.activate(a, a.generate(F.aRateperhour))
        self.activate(s,
                s.generate(resourcenormal=agents, resourcespecial=specialagents, resourcenormalPM=agentspm, resourcespecialPM=specialagentspm)
                , at=0.0)
        self.activate(tchange, tchange.generate(F.tPMshiftchange))
        self.simulate(until=F.maxTime)

faxsim = Faxcentersim()
faxsim.run(4321)

faxsim.Regular10.count()

faxsim.Special10.count()

np.mean(faxsim.Special10.yseries()) * 60

np.mean(faxsim.Regular10.yseries()) * 60
