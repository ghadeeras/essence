package essence.core.testutils;

import essence.core.random.RandomGeneration;
import essence.core.random.SeededRandomGenerator;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SameSeed implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                try {
                    RandomGeneration.using(new SeededRandomGenerator(seed()), () -> evaluate(base));
                } catch (RuntimeException e) {
                    throw e.getCause() != null ? e.getCause() : e;
                }
            }

            private long seed() {
                long seed = description.getTestClass().getSimpleName().hashCode();
                seed <<= 32;
                seed += description.getMethodName().hashCode();
                return seed;
            }

            private void evaluate(Statement base) {
                try {
                    base.evaluate();
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }

        };
    }

}
