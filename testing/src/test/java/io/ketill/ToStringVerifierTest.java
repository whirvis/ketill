package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class ToStringVerifierTest {

    @Test
    void testForClass() {
        /*
         * It would not make sense to verify the toString() method for a
         * null class or a null instance of a class. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> ToStringVerifier.forClass(null, new Object()));
        assertThrows(NullPointerException.class,
                () -> ToStringVerifier.forClass(String.class, null));

        /*
         * One of the main objects of the toString() verifier is to ensure
         * that the class has overridden toString() from the Object class.
         * As a result, it would not be possible to verify if the original
         * Object class properly overrides toString().
         */
        assertThrows(UnsupportedOperationException.class,
                () -> ToStringVerifier.forClass(Object.class, new Object()));

        /*
         * This method should never return a null verifier when given valid
         * arguments. If an invalid arguments is passed, an exception should
         * be thrown instead.
         */
        assertNotNull(ToStringVerifier.forClass(String.class, ""));
    }

    @Test
    void testVerify() {
        /*
         * The verifier requires that the toString() method to be overridden
         * by the given class. The class below does not do this. As a result,
         * the verification should fail.
         */
        assertThrows(AssertionError.class,
                () -> ToStringVerifier.forClass(MockThing.NoOverride.class,
                        new MockThing.NoOverride()).verify());

        /*
         * The verifier requires that the toString() method to not return
         * the result of calling the super method. The class below calls
         * super.toString(). As such, the verification below should fail.
         */
        assertThrows(AssertionError.class,
                () -> ToStringVerifier.forClass(MockThing.ReturnsSuper.class,
                        new MockThing.ReturnsSuper()).verify());

        /*
         * The class below both implements toString() and does not return
         * super.toString(). As such, the verification below should pass.
         */
        ToStringVerifier.forClass(MockThing.ProperToString.class,
                new MockThing.ProperToString()).verify();
    }

}
