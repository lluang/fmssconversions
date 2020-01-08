#import random
import simpy
import networkx as nx

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




SANglobal.finishtime = 0
#Sim.initialize()
SANglobal.F.nodecomplete= []
for i in range(len(SANglobal.F.nodes())):
    eventname = 'Complete%1d' % i
#    SANglobal.F.nodecomplete.append(Sim.SimEvent(eventname))
SANglobal.F.nodecomplete

# start at node 0
# if there are outbound nodes, schedule 

def runSimulation():

    EventNotice nextEvent;
    Activity thisActivity;

    for(int rep = 0; rep < 1000; rep++) {
      javaSim.javaSimInit();
      sanInit(); // initializes the activities in the SAN
      milestone(0, a); // causes outbound activities of node a to be scheduled

      do {
        nextEvent = javaSim.calendarRemove();
        javaSim.setClock(nextEvent.getEventTime());
        thisActivity = (Activity) nextEvent.getWhichObject();
        milestone(thisActivity.getWhichActivity(), thisActivity.getWhichNode());
      } while(javaSim.calendarN() > 0); // stop when event calendar is empty

      javaSim.report(javaSim.getClock(), rep + 1, 0);
    }

def milestone(int actIn, int node) {
    int m;
    ArrayList<Integer> inbound = new ArrayList<Integer>(nodes.get(inTo).get(node));
    ArrayList<Integer> outbound = new ArrayList<Integer>(nodes.get(outOf).get(node));
    m = inbound.size();

    for(int incoming = 0; incoming < m; incoming++) {
      if (inbound.get(incoming) == actIn) {
        inbound.remove(incoming);
        break;
      }
    }
    nodes.get(inTo).get(node).clear();
    nodes.get(inTo).get(node).addAll(inbound);

    if (inbound.isEmpty()) {
      m = outbound.size();
      for(int actOut = 0; actOut < m; actOut++) {
        Activity thisActivity = new Activity();
        thisActivity.setWhichActivity(outbound.get(0));
        thisActivity.setWhichNode(destination.get(outbound.get(0)));
        javaSim.schedulePlus("Milestone", generator.expon(1, 0), thisActivity);
        outbound.remove(0);
      }
    }

  }
