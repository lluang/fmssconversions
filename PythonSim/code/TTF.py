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
	# A failure is occurring
	if T.S > 0:
	   T.NextFailure = T.Clock + random.randint(1, 6)
	   # Scheduling the next failure
	if T.S == (T.Sinit - 1):
	   T.NextRepair = T.Clock + 2.5
	   # Scheduling the next repair since there is no repair ongoing
	T.Area = T.Area + T.Slast * (T.Clock - T.Tlast)
	T.Tlast = T.Clock
	T.Slast = T.S
	return {'S':T.S, 'NextFailure':T.NextFailure, 'NextRepair':T.NextRepair}

def Repair():
	T.S = T.S + 1
	# A repair is occurring.
	if T.S < T.Sinit:
   	    T.NextRepair = T.Clock + 2.5
   	else:
   	    T.NextRepair = T.Clock + 1000000
   	    # The next event has to be a failure because all machines should now be working.
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
		print "At time"
		print T.Clock
		print ("we have %i machines operating" % (int(T.S)))
		TimerFunc()
		if T.Timer == "Failure":
			print ("There is a failure occurring at time %i" % (T.Clock))
			Failure()
		elif T.Timer == "Repair":
			print ("There is a repair occurring at time")
			print (T.Clock)
			Repair()
		else:
		        print ("Done!")
		        print T.Clock

	T.Average = T.Area/T.Clock
        print ("There are no machines left!")
        print ("The time of failure is %i" % (int(T.Clock)))
	print ("The average number of components was")
	print (T.Average)
	return ({'Average':T.Average, 'Clock':T.Clock})


TTF(1234)
TTF(2345)
TTF(3456)
