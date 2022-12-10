from pathlib import Path as path
from file_writer_server import FileWriterServer


import logging
from trace_setup import get_tracer

from opentelemetry.trace.propagation.tracecontext import TraceContextTextMapPropagator

#retrieve trace provider
tracer = get_tracer()

class FileReader:
   #info-level logging format
    format = "%(asctime)s: %(message)s"
    logging.basicConfig(format=format, level=logging.INFO,
                        datefmt="%H:%M:%S")   
   
   
    #read files
    def readFromFiles(self, numberOfFiles: int):  
       #start and trace the reading of files      
        with tracer.start_as_current_span("client-read-span"):
         try:
               #define a carrier to inject into context
               carrier = {}
               TraceContextTextMapPropagator().inject(carrier)
                   
               #get path to the data file dir storing all 10 files           
               paths = path.cwd().joinpath("data")
               
               #list to store lines from read files
               linesOfStrings = list()
               
               #read all files from data dir
               for i in range(numberOfFiles):
                  fileNumber = i+1
                  fileName = "data_{}.txt".format(fileNumber)               
                  reader = open(paths.joinpath(fileName), "r")
                  
                  logging.info("Client: reading file {}".format(fileNumber))
                  
                  #append all lines into list
                  for line in reader:
                        linesOfStrings.append(line)
                        
                  reader.close()
                  logging.info("Client: done reading file {} and sending to server".format(fileNumber))
                  
                  #dictionary(map) to store data to be sent to server
                  clientData = {"fileData": linesOfStrings, "traceParent": carrier, "fileNumber":fileNumber}
                  
                  #call server instance to process data
                  FileWriterServer.receiver(clientData)

        #catch and throw exceptions
         except Exception as e:
               logging.info(f"Client: failed to send file to write server: {e}")
      
     
