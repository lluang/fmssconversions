import random
class T:
    Sinit = 2
    S = Sinit
    Clock = 0
    NextFailure = random.randint(1,6)
    NextRepair = 100000000
    Slast = S
    Tlast = 0
    Area = 0.0
    Timer = "Failure"
def Failure():
    T.S = T.S - 1
    if T.S > 0:
       T.NextFailure = T.Clock + random.randint(1, 6)
    if T.S == (T.Sinit - 1):
       T.NextRepair = T.Clock + 2.5
    T.Area = T.Area + T.Slast * (T.Clock - T.Tlast)
    T.Tlast = T.Clock
    T.Slast = T.S
    return {'S':T.S, 'NextFailure':T.NextFailure, 'NextRepair':T.NextRepair}
def Repair():
    T.S = T.S + 1
    if T.S < T.Sinit:
        T.NextRepair = T.Clock + 2.5
    else:
        T.NextRepair = T.Clock + 1000000
    T.Area = T.Area + T.Slast * (T.Clock - T.Tlast)
    T.Tlast = T.Clock
    T.Slast = T.S
    return {'S':T.S, 'NextFailure':T.NextFailure, 'NextRepair':T.NextRepair}
def TimerFunc():
    if T.NextFailure < T.NextRepair:
        T.Timer = "Failure"
        T.Clock = T.NextFailure
        T.NextFailure = 1000000
    else:
        T.Timer = "Repair"
        T.Clock = T.NextRepair
        T.NextRepair = 1000000
    return {'Timer':T.Timer, 'Clock':T.Clock}
