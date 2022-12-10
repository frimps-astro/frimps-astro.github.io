from file_reader_client import FileReader
from file_writer_server import FileWriterServer
import logging


format = "%(asctime)s: %(message)s"
logging.basicConfig(format=format, level=logging.INFO,
                        datefmt="%H:%M:%S")   

#instantiate file client and server
reader_client =  FileReader()
writer_server = FileWriterServer()

def opentelemetry():
    numberOfFiles = 10        

    reader_client.readFromFiles(numberOfFiles)
            
 #thread joing function   
def runThreads(threads: list, type: str):
    for i, t in enumerate(threads):
         logging.info("{}: retrieving and joining thread {}".format(type, i+1))
         t.join()

#start the whole application
opentelemetry()

#retrieve and join server threads
server_threads = writer_server.getThreads()
runThreads(server_threads, "Server")

