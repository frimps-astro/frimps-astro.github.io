
import logging
import threading

from opentelemetry.trace.propagation.tracecontext import TraceContextTextMapPropagator

from trace_setup import get_tracer

#list for threads
threads = list()

#retrieve trace provider
tracer = get_tracer()

#trace context propagator
prop = TraceContextTextMapPropagator()

class FileWriterServer:   
   #format for info-level logging  
   format = "%(asctime)s: %(message)s"
   logging.basicConfig(format=format, level=logging.INFO,
                        datefmt="%H:%M:%S")   
   
   #write received data to a file
   def writeToLocalFile(data:list, fileNumber):
      
      writer = open("output.txt", "w")
      
      logging.info("Server: calling the writer service on data of file {} to local".format(fileNumber))

      for line in data:
         writer.write(line)
      
      logging.info("Server: done writing data of file {} to local".format(fileNumber))
      
      writer.close()
      
   def receiver(clientData:dict):
      global threads
      
      #extract context of client trace
      carrier = clientData.get("traceParent")      
      context = prop.extract(carrier=carrier)
      
      #extract actual data to write
      fileData = clientData.get("fileData")
      fileNumber = clientData.get("fileNumber")
      
      #start and trace a new thread task with the client context
      with tracer.start_as_current_span("server-write-span-{}".format(fileNumber), context=context):
            logging.info("Server: creating and starting thread for file {}".format(fileNumber))
            t = threading.Thread(target=FileWriterServer.writeToLocalFile, args=(fileData, fileNumber))
            threads.append(t)
            t.start()
   
   #getter for the threads
   def getThreads(self):     
      return threads
               