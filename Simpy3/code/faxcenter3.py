# Fax center
#
# A service center receives faxed orders throughout the day,
# with the rate of arrival varying hour by hour. The arrivals are
# modeled by a nonstationary Possion process with the rates
# shown in Table~\ref{tab:fax.arrivals}. \index{nonstationary Poisson
# process}
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
#
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

import math
import random
import simpy


class Fax(object):
    """ Fax arrives and is processed

        Processed first by regular staff, then specialized staff if needed
        Note that after the shift change time, a different set of resources are
        used. Assume that anyone working on a fax will continue working until
        it finishes. This is an approximation because in real life it could be
        the same person, but since any individual fax does not take long it
        does not change the results much.
    """

    def visit(self, env, aAM, sAM, aPM, sPM):
        arrive = env.now()
        print("%8.4f %s: Here I am     " % (env.now(), self.name))
        # Check if shift change
        if (arrive < F.tPMshiftchange):
            agent = aAM
        else:
            agent = aPM
        yield agent.request()
        wait = env.now() - arrive
        print("%8.4f %s: Waited %6.3f" % (env.now(), self.name, wait))
        tis = -1.0
        while (tis < 0):
            tis = random.normalvariate(F.meanRegular, F.stdRegular)
        yield env.timeout(tis)
        yield agent.release()
        checkSpecial = random.random()
        # Check if shift change
        if (checkSpecial < F.pSpecial):
            if (env.now() < F.tPMshiftchange):
                specialagent = sAM
            else:
                specialagent = sPM
            print("%8.4f %s: I'm special" % (env.now(), self.name))
            tspecial = -1.0
            while (tspecial < 0):
                tspecial = random.normalvariate(F.meanSpecial, F.stdSpecial)
            yield specialagent.request()
            yield env.timeout(tspecial)
            yield specialagent.release()
            finished = env.now() - arrive
            env.Special10.observe(finished)
        else:
            finished = env.now() - arrive
            env.Regular10.observe(finished)
        print("%8.4f %s: Finished" % (env.now(), self.name))


def generatecustomer(env, resourcenormal, resourcespecial,
                     resourcenormalPM, resourcespecialPM):
    i = 0
    while(env.now() < F.nPeriods and F.meanTBA > 0):
        f = Fax(name="Fax%02d" % (i), sim=env)
        print("%8.4f %s: Generate a fax" % (env.now(), f.name))
        env.activate(f,
                     f.visit(aAM=resourcenormal, sAM=resourcespecial,
                             aPM=resourcenormalPM, sPM=resourcespecialPM))
        t = random.expovariate(1.0 / F.meanTBA)
        yield env.timeout(t)
        i += 1


def changearrivalrate(env, arrivalrate):
    """ update the arrival rate every hour

        Reads in the arrival rate table and updates the arrival rate every
        hour. One hour after the last hour begins, changes arrival rate to 0
    """
    for i in range(len(arrivalrate)):
        F.meanTBA = 1.0/(arrivalrate[i])
        yield env.timeout(1.0)
    # After the end of the day, set the arrival rate = 0
    F.meanTBA = 0.0


#  Experiment data -------------------------
class F:
    maxTime = 100    # hours
    theseed = 9999
    period = 60.0
    nPeriods = 8
    meanRegular = 2.5/period  # hours
    varRegular = 1.0/period  # hours
    stdRegular = math.sqrt(1.0)/period  # per hour
    meanSpecial = 4.0/period  # hours
    varSpecial = 1.0/period  # hours
    stdSpecial = math.sqrt(1.0)/period  # per hour
    tPMshiftchange = 4.0
    numAgents = 15
    numAgentsPM = 9
    numSpecialists = 6
    numSpecialistsPM = 3
    maxRate = 6.24
    aRate = [4.37, 6.24, 5.29, 2.97, 2.03, 2.79, 2.36, 1.04]  # per minute
    meanTBA = 1 / (maxRate * period)  # hour


# Model/Experiment ------------------------------
class Faxcentersim(object):
    def run(self, aseed):
        random.seed(aseed)
        env = simpy.Environment()
        # Start processes
        agents = simpy.Resource(env, capacity=F.numAgents)
        specialagents = simpy.Resource(env, capacity=F.numSpecialists)
        agentspm = simpy.Resource(env, capacity=F.numAgentsPM)
        specialagentspm = simpy.Resource(env, capacity=F.numSpecialistsPM)

        F.meanTBA = 0.0
        env.process(generatecustomer(resourcenormal=agents,
                                     resourcespecial=specialagents,
                                     resourcenormalPM=agentspm,
                                     resourcespecialPM=specialagentspm))
        env.process(changearrivalrate(F.aRateperhour))
        tchange = SecondShift('PM Shift', sim=self)
        env.run(until=F.maxTime)
