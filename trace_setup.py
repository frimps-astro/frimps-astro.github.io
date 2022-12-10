from opentelemetry import trace
from opentelemetry.exporter.jaeger.thrift import JaegerExporter
from opentelemetry.sdk.resources import SERVICE_NAME, Resource
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor

#set up for jaeger exporter
def get_jaeger_exporter():
    return JaegerExporter(
   agent_host_name="localhost",
   agent_port=6831,
   )


def get_tracer():
    span_exporter = get_jaeger_exporter()
    
    resource = Resource(attributes={
    SERVICE_NAME: "file-service"
    })
    provider = TracerProvider(resource=resource)
        
    #uses BatchSpan Processor 
    #which collects all traces and send in batches
    processor = BatchSpanProcessor(span_exporter)
    provider.add_span_processor(processor)
    trace.set_tracer_provider(provider)    

    return trace.get_tracer(__name__)