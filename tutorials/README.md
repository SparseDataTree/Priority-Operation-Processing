#Tutorials

## Context
This is just getting started.  My initial intention is to create a monolithic Java service that incorporates all the API and 
process components of POP, but is as agnostic as reasonable to existing tech stacks.

## Starting very simple
We start with a simple (monolithic Springboot) service that:
1. takes in a payload and converts it to an agenda
2. puts the agenda instance on a queue
3. has a puller that can pull agendas from the queue
4. has an executor that can execute the operations specified by the agenda
5. has handlers for operations

At this simple level, we track actions through log messages.

## Next steps:
(still lightweight monolithic functionality)
1. authorization
2. scheduling
3. resource pool
4. insight queue
5. agenda progress

## More next steps:
(start breaking out functionality into microservices)
1. docker/kubernetes
2. leverage db and message technologies
