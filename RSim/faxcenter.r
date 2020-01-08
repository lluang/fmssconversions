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

library(simmer)

#  Experiment data -------------------------
periodlength = 60.0
maxrate = 6.24
F <- list(
  maxTime = 100,    # hours
  theseed = 9999,
  period = periodlength,
  nPeriods = 8,
  meanRegular = 2.5/periodlength,  # hours
  varRegular = 1.0/periodlength , # hours
  stdRegular = sqrt(1.0)/periodlength,  # per hour
  meanSpecial = 4.0/periodlength,  # hours
  varSpecial = 1.0/periodlength,  # hours
  stdSpecial = sqrt(1.0)/periodlength,  # per hour
  c = 4.0,
  numAgents = 15,
  numAgentsPM = 9,
  numSpecialists = 6,
  numSpecialistsPM = 3,
  maxRate = maxrate,
  aRate = c(4.37, 6.24, 5.29, 2.97, 2.03, 2.79, 2.36, 1.04),  # per minute
  faxRate = 4.37,
  meanTBA = 1 / (maxrate * periodlength),  # hour
  pSpecial = 0.20
)


# Model/Experiment ------------------------------

fax <- trajectory("morningfax") %>%
  branch(function() sample(c(1, 2), 1,
                           prob = c(F[['pSpecial']], 1-F[['pSpecial']])),
         c(T, T),
         trajectory() %>%
           seize("specialagents", 1) %>%
           # do stuff here
           timeout(function() rnorm(n=1, 
                                    mean=F[['meanSpecial']], 
                                    sd = F[['stdSpecial']])) %>%
           release("specialagents", 1),
         trajectory() %>%
           seize("agents", 1) %>%
           # do stuff here
           timeout(function() rnorm(n=1, 
                                    mean=F[['meanRegular']], 
                                    sd = F[['stdRegular']])) %>%
           release("agents", 1))

changearrivalrate <- trajectory("changearrivalrate") %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][2]) %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][3]) %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][4]) %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][5]) %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][6]) %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][7]) %>%
  timeout(1) %>%
  set_attribute("faxRate", F[['aRate']][8])



envs <- lapply(1:100, function(i) {
  simmer("faxcenter") %>%
    add_resource("agents", F[['numAgents']]) %>%
    add_resource("specialagents", F[['numSpecialists']]) %>%
    add_resource("agentspm", F[['numAgentsPM']]) %>%
    add_resource("specialagentspm", F[['numSpecialistsPM']]) %>%
    add_generator("fax", fax, 
                  function() rexp(n=1,rate = (1/F[['faxRate']]))) %>%
    add_generator("Change arrival rate", changearrivalrate,
                  function () 4) %>%
    run(until=F[['maxTime']])
})

envs[[1]] %>% get_queue_size("agents")
head(
  envs %>% get_mon_resources()
)

arrivals <- get_mon_arrivals(envs[[1]], per_resource = T)

# Times that each branch was entered

arrivals %>%
  group_by(resource) %>%
  summarize(count = length(name),
            avgtime = mean(activity_time)) %>%
  print()

plot(get_mon_resources(envs), metric="usage")

plot(get_mon_resources(envs), metric="utilization")
