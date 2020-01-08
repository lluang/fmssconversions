import math; import simpy
import scipy as sp; import pandas as pd
class MonitoredResource(simpy.Resource):
    def __init__(self, *args, **kwargs):
        super(MonitoredResource, self).__init__(*args, **kwargs)
        self.data = []
        self.numcars = 0
    def request(self, *args, **kwargs):
        self.data.append((self._env.now, self.count, len(self.queue)))
        self.numcars += 1
        return super(MonitoredResource, self).request(*args, **kwargs)
    def release(self, *args, **kwargs):
        self.data.append((self._env.now, self.count, len(self.queue)))
        return super(MonitoredResource, self).release(*args, **kwargs)
    def updatestats(self):
        elapseddata = [(self.data[i][0] - self.data[i-1][0], self.data[i][1],
                        self.data[i][2])
                       if i > 0 else (self.data[i][0], self.data[i][1],
                                      self.data[i][2])
                       for i in range(0, len(self.data))]

        try:
            self.carwashdata = pd.DataFrame(self.data,
                                            columns=['time', 'servers',
                                                     'queue'])
            self.carwashdata['system'] = (self.carwashdata['servers'] +
                                          self.carwashdata['queue'])
            self.elapseddata = pd.DataFrame(elapseddata,
                                            columns=['elapsedtime', 'servers',
                                                     'queue'])
            self.elapseddata['system'] = (self.elapseddata['servers'] +
                                          self.elapseddata['queue'])
            return self.carwashdata
        except:
            return elapseddata
    def countcars(self):
        return self.numcars
    def timeAverage(self, elapsedtime, values):
        return ((sp.sum(elapsedtime * values)) / elapsedtime.sum())
    def timeVariance(self, elapsedtime, values):
        weightedmu = self.timeAverage(elapsedtime, values)
        V = elapsedtime.sum()
        weightsumsquared = sum([elapsedtime[i] *
                                math.pow(values[i] - weightedmu, 2)
                                for i in range(len(elapsedtime))])
        return weightsumsquared / (V - 1)
    def actTimeAverage(self):
        return self.timeAverage(self.elapseddata['elapsedtime'],
                                self.elapseddata['servers'])
    def waitTimeAverage(self):
        return self.timeAverage(self.elapseddata['elapsedtime'],
                                self.elapseddata['queue'])
    def actTimeVariance(self):
        return self.timeVariance(self.elapseddata['elapsedtime'],
                                         self.elapseddata['servers'])
    def waitTimeVariance(self):
        return self.timeVariance(self.elapseddata['elapsedtime'],
                                         self.elapseddata['queue'])