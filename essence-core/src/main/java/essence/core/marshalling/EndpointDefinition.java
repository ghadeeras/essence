package essence.core.marshalling;

import essence.core.basic.DataType;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EndpointDefinition<I, O> {

    private final DataType<I> inputType;
    private final DataType<O> outputType;
    private final ContentFormat inputFormat;
    private final ContentFormat outputFormat;

    private final Unmarshaller<I> inputUnmarshaller;
    private final Marshaller<O> outputMarshaller;
    private final Marshaller<I> inputMarshaller;
    private final Unmarshaller<O> outputUnmarshaller;

    private EndpointDefinition(DataType<I> inputType, DataType<O> outputType, ContentFormat inputFormat, ContentFormat outputFormat) {
        this.inputType = inputType;
        this.outputType = outputType;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;

        this.inputUnmarshaller = inputFormat.unmarshallerFor(inputType);
        this.outputMarshaller = outputFormat.marshallerFor(outputType);
        this.inputMarshaller = inputFormat.marshallerFor(inputType);
        this.outputUnmarshaller = outputFormat.unmarshallerFor(outputType);
    }

    public Function<String, String> service(Function<I, O> serviceFunction) {
        return inputUnmarshaller.andThen(serviceFunction).andThen(outputMarshaller);
    }

    public BiConsumer<Reader, Writer> streamingService(Function<I, O> serviceFunction) {
        return (in, out) -> {
            I input = inputUnmarshaller.unmarshal(in);
            O output = serviceFunction.apply(input);
            try {
                outputMarshaller.marshal(output, out);
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };
    }

    public static BiConsumer<InputStream, OutputStream> encoded(BiConsumer<Reader, Writer> serviceFunction) {
        return (in, out) -> serviceFunction.accept(new InputStreamReader(in), new OutputStreamWriter(out));
    }

    public Function<I, O> client(Function<String, String> sender) {
        return inputMarshaller.andThen(sender).andThen(outputUnmarshaller);
    }

    public DataType<I> getInputType() {
        return inputType;
    }

    public DataType<O> getOutputType() {
        return outputType;
    }

    public ContentFormat getInputFormat() {
        return inputFormat;
    }

    public ContentFormat getOutputFormat() {
        return outputFormat;
    }

    public static <I> InputFormatDefiner<I> expects(DataType<I> inputType) {
        return inputFormat -> new OutputTypeDefiner<I>() {
            @Override
            public <O> OutputFormatDefiner<I, O> andReturns(DataType<O> outputType) {
                return outputFormat -> new EndpointDefinition<I, O>(inputType, outputType, inputFormat, outputFormat);
            }
        };
    }

    public interface InputFormatDefiner<I> {
        OutputTypeDefiner<I> in(ContentFormat format);
    }

    public interface OutputTypeDefiner<I> {
        <O> OutputFormatDefiner<I, O> andReturns(DataType<O> outputType);
    }

    public interface OutputFormatDefiner<I, O> {
        EndpointDefinition<I, O> in(ContentFormat format);
    }

}
