import random
import matplotlib.pyplot as plt
import statsmodels.stats as sm
import SimPy.Simulation as Sim
import networkx as nx


class ActivityProcess(Sim.Process):
    def waitup(self,node, myEvent):      # PEM illustrating "waitevent"
                                   # wait for "myEvent" to occur
        yield Sim.waitevent, self, myEvent
        tis = random.expovariate(1.0)
        yield Sim.hold, self, tis
        finishtime = Sim.now()
        if finishtime >SANglobal.finishtime:
            SANglobal.finishtime = finishtime
        SANglobal.F.nodecomplete[node].signal()

class SANglobal:
    F = nx.DiGraph()
    a = 0
    b = 1
    c = 2
    d = 3
    inTo = 0
    outOf = 1
    F.add_nodes_from([a, b, c, d])
    F.add_edges_from([(a,b), (a,c), (b,c), (b,d), (c,d)])
    finishtime = 0


class StartSignaller(Sim.Process):
    # here we just schedule some events to fire
    def startSignals(self):
        yield Sim.hold, self, 0
        startevent.signal()

finishtimes = []
for rep in range(1000):
    SANglobal.finishtime = 0
    Sim.initialize()
    SANglobal.F.nodecomplete= []
    for i in range(len(SANglobal.F.nodes())):
        eventname = 'Complete%1d' % i
        SANglobal.F.nodecomplete.append(Sim.SimEvent(eventname))
    SANglobal.F.nodecomplete

    activitynode = []
    for i in range(len(SANglobal.F.nodes())):
        activityname = 'Activity%1d' % i
        activitynode.append(ActivityProcess(activityname))
    for i in range(len(SANglobal.F.nodes())):
        if i <> SANglobal.inTo:
            prenodes = SANglobal.F.predecessors(i)
            preevents = [SANglobal.F.nodecomplete[j] for j in prenodes]
            Sim.activate(activitynode[i], activitynode[i].waitup(i,preevents))
    startevent = Sim.SimEvent('Start')
    sstart = StartSignaller('Signal')
    Sim.activate(sstart, sstart.startSignals())
    Sim.activate(activitynode[SANglobal.inTo],
                activitynode[SANglobal.inTo].waitup(SANglobal.inTo, startevent))

    Sim.simulate(until=50)
    finishtimes.append(SANglobal.finishtime)

plt.hist(finishtimes, bins = 60, range = (0, 12), histtype = 'step',
    normed = True, cumulative=True)
