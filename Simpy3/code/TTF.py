from TTFhelper import *
def TTF(aseed):
    random.seed(aseed)
    print ("We are beginning the TTF simulation!")
    T.S = T.Sinit
    T.Clock = 0
    T.NextFailure = random.randint(1,6)
    T.NextRepair = 100000000
    T.Slast = T.S
    T.Tlast = 0
    T.Area = 0.0
    T.Timer = "Failure"
    while T.S > 0:
        print("At %4.2f we have %i machines operating" % (T.Clock, int(T.S)))
        TimerFunc()
        if T.Timer == "Failure":
            print("There is a failure occurring at time %i" % (T.Clock))
            Failure()
        elif T.Timer == "Repair":
            print("There is a repair occurring at time")
            print(T.Clock)
            Repair()
        else:
            print("Done!")
            print(T.Clock)
    T.Average = T.Area/T.Clock
    print ("There are no machines left!")
    print ("The time of failure is %i" % (int(T.Clock)))
    print ("The average number of components was %4.2f" % (T.Average))
    return ({'Average':T.Average, 'Clock':T.Clock})
TTF(1234)