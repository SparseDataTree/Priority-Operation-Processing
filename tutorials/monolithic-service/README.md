# Priority Operation Processing (POP) framework

For the sake of this tutorial, let's take a second look at this framework, imagining it handling a task.

**We have a customer Sue who wants to execute a task (which can be defined as a process).**
## POP process
POP supports execution of a wide range of processes.  It does this by representing work as a network of nodes (Operations).  
These operations are helpfully viewed as producers and consumers, with a DAG (directed acyclic graph) topology. 

The POP framework can optimize asynchronous execution of operations with awareness of when inputs are available.

**Sue has some work to do.**

**She has a document written in English.  She wants it 'cleaned up' (in English) and then the cleaned version is translated 
into Spanish, Jianhuazi, and Hindi.**

**She has two options for entering this job into the POP system: first, she can provide a payload that defines the entire 
workflow (an Agenda); 
secondly, if this workflow has a reusable structure, she can create a template (AgendaTemplate) that can be leveraged for 
individual tasks; needing
only supply parameters (from which a task Agenda can be created).**

Looking at the potential topology of this task, of course, it might be handled by a single heavyweight application.  
But it does suggest 
an opportunity to break functionality into smaller pieces; perhaps a microservice to "clean up" the English; and then
separate microservices for each of the translations.  In such a scenario, the "cleaner" service takes in the raw input and 
provides a product that can be consumed in parallel by each of the translation services.

Those are, perhaps, the most straightforward scenarios; and are sufficient for our illustration.  But, for the
sake of exploration, we push the scenario a bit.

Perhaps we do not know what language the source is in.  Our job might be: 1. determine the language of the source text; 
2. convert the text into a "hub" language; 3. translate from the hub language to the desired output languages.

Again, this is still relatively straightforward.

If we think that conversion and translation can be handled piecemeal, and if the size of the text is sufficiently
large, and if we need it done fast, then we might add in an operation to break up the work into pieces that can be
handled asynchronously by separate microservices.

In such a case, we might have a topology such as: 1. identify the language; 2. break the text into pieces; 3. asynchronously 
convert text to "hub" language; 3. for each chunk converted to "hub", translate the chunk into the various 
specified output languages - a different microservice instance for each language and chunk.


## POP anatomy
The POP framework has two parts:  a brain; and a body.  The brain handles communication with customers; namely, 
managing input and providing information on progress back to customers.

The management of input includes: unmarshalling of process instructions; validation; confirmation of permitted compute 
resources; staging of input in a queue for execution.

Progress reporting includes: updates on process stage and percent complete, along with completion status.  

Additional functionality of the POP brain includes logic to retry or to cancel jobs.

TODO we need to talk about the body.
### POP brain in action
**Sue has an entire collection of documents to translate, all starting in English, and with potentially different
desired target languages for output.  She is not concerned about speed.  But she does want the documents to be 
cleaned up, perhaps depending on document type.  For poems, she wants the documents to be untouched; no meddling 
with spellling or grammar or such.  For prose, perhaps spelling is corrected and compound sentences are broken up.**

**Since this is a type of job, Sue has prepared and submitted an Agenda template, which the POP brain has persisted.  
Sue accesses this template by providing its ID and a set of parameters 
that can be used by the brain to create an Agenda for the task.  This agenda includes several items: a file path; 
a document type (which indicates the kind of cleaning to be done); a list of target languages for translation; and a 
list of target files.**

**She uses a POP client to submit an agenda template Id and a payload of key:value pairs to substitute into the agenda 
template that she has previously uploaded to the POP system.  The POP brain looks up the template, substitutes template 
keys with values to create an recipie for the process (the agenda).  The agenda includes a list of operations, in this 
case, one operation to clean up the text, and three operations to translate the cleaned up text into three 
different languages.**

**But the agenda does more than that.  It specifies who the customer is, and references endpoints that allows the brain 
to determine what computing resources are available for that customer, and are appropriate for that task.**

todo spell this out a bit more... e.g. insights and resource pools.
What is happening under the hood?  The brain retrieves the customer id from the agenda and looks it up.


todo spell out the roles of insight mappers.

todo explore the options associated with queues and why one might have multiple resource pools.







### POP body in action
The POP body 

todo we start where Sue's agenda is in a queue as is picked up by a puller in a resource pool (e.g. a k8 cluster and associated mounted memory)

todo then explain how the agenda is moved and transformed as it is picked up by an executor.

todo then explain how the executor can spin up pods for each of the operations and how it sends updates back to the brain.

