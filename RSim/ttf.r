(??)Sinit = 2
(??)#S = Sinit
(??)#Clock = 0
(??)NextFailure = round(runif(1,1,7),0)
(??)NextRepair = 100000
(??)#Slast = S
(??)#Tlast = 0
(??)#Area = 0.0
(??)#Timer = "Failure"

failure <- function(sinit, s, clock, tlast, area){
  # A failure is occurring
  # Schedule repair for current component and the 
  # next failure if system has not failed 
  # (for the backup component which will start working)
  slast = s
  s = s-1
  if (s > 0)
    NextFailure = clock + sample(1:6,1)
  else
    NextFailure = 1000000
  # Scheduling the next repair
  if (s <= (sinit - 1))
    # Scheduling the next repair since there is no repair ongoing
    NextRepair = clock + 2.5
  area = area + slast * (clock - tlast)
  state <- list(s=s, NextFailure=NextFailure, NextRepair=NextRepair, area=area)
  print(state)
  state
}

repair <- function(sinit, s, clock, tlast, area){
  # A repair is completed
  # Schedule the next repair if there is a failed component
  slast = s
  s = s+1
  if (s < sinit)
    NextRepair = clock + 2.5
  else
    NextRepair = 1000000
  area = area + slast * (clock - tlast)
  state <- list(s=s, NextRepair=NextRepair, area=area)
  print(state)
  state
}

timerfunc <- function(NextFailure, NextRepair, s, slast, clock, tlast, area){
  # Check simulation clock to determine next event.
  # Return the next event as part of timer member of the returned list
  
  if (NextFailure < NextRepair){
    timer = "Failure"
    clock = NextFailure
    NextFailure = 1000000
  } else {
    timer = "Repair"
    clock = NextRepair
    NextRepair = 1000000
  }
  area = area + slast * (clock - tlast)

  ret <- list(s=s, NextFailure = NextFailure, NextRepair = NextRepair, 
              timer = timer, clock = clock)
}


TTF <- function(aseed){
  set.seed(aseed)
  print("We are beginning the TTF simulation!\n")
  Sinit = 2
  s = Sinit
  clock = 0
  NextFailure = sample(1:6, 1)
  NextRepair = 1000000
  slast = s
  tlast = 0
  area = 0
  timer = "Failure"
  while(s > 0){
    cat("At time ", clock)
    cat(" we have ", s, " machines operating", "\n")
    #list[s, NextFailure, NextRepair, timer, clock] <- 
    ret <- 
      timerfunc(NextFailure, NextRepair, s, slast, clock, tlast, area)
    print(ret)
    if(ret$timer=="Failure"){
      cat("There is a failure occurring at time ", ret$clock, "\n")
      state <- 
        failure(Sinit, ret$s, ret$clock, tlast, area)
      cat("Next failure is at ", state$NextFailure)
    } else {
      if (ret$timer=="Repair"){
        cat("There is a repair completed at time ", ret$clock, "\n")
        state <- 
          repair(Sinit, ret$s, ret$clock, tlast, area)
        state$NextFailure <- ret$NextFailure
      } else{
        print("Done!")
      }
      
    }
    NextFailure= state$NextFailure
    NextRepair = state$NextRepair
    s=state$s
    area=state$area
    clock = ret$clock
    tlast = clock
  }
  taverage = area/clock
  print("There are no machines left!")
  c(taverage, clock)
}

aseed = 1234
uptime <- TTF(aseed)
cat("The time of failure is ", uptime[2], "\n")
cat("The average number of components was ", uptime[1])
