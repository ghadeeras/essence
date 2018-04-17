package essence.core.marshalling;

import essence.core.basic.DataType;

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
