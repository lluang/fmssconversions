library(simmer)
RANDOM_SEED = 42
NUM_STATIONS = 2  # Number of machines being charged
CHARGETIME = 3      # Minutes it takes to charge a car
T_INTER = 2       # Car interarrival interval
NUM_CARS = 10      # Number of cars in simulation


env <- simmer("Carchargesim")

car <- trajectory("Car") %>%
  seize("chargingstation", 1) %>%
  timeout(function() sample(CHARGETIME-1:CHARGETIME+1,1)) %>%
  release("chargingstation", 1)


env %>%
  add_resource("chargingstation", capacity=NUM_STATIONS, mon=TRUE) %>%
  add_generator("car", car, function() sample(max(0, T_INTER-2):T_INTER+2, 1))

env %>% run(until=c(160))

head(
  env %>% get_mon_resources()
)