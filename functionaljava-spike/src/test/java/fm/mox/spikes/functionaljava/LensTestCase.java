package fm.mox.spikes.functionaljava;

import fj.F;
import fj.F1Functions;
import fj.F2;
import lombok.Value;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class LensTestCase {

    private static final fj.F<Address, String> GET_ZIPCODE = address -> address.zipcode;
    private static final fj.F<String, F<Address, Address>> SET_ZIPCODE2 = (s) -> (F<Address, Address>) address -> new Address(s);

    private static final fj.F2<String, Address, Address> SET_ZIPCODE = (s, address) -> new Address(s);
    private static Lens<Address, String> addressStringLens() {return Lens.lens(GET_ZIPCODE, SET_ZIPCODE);}

    @Test
    public void testName() throws Exception {
        final Lens<Person, Address> personAddress = personAddressLens();
        final Lens<Address, String> addressZipcode = addressStringLens();
        final Lens<Person, String> personZipCode = personAddress.then(addressZipcode);

        final String aZipcode = "00100";
        final Address anAddress = new Address(aZipcode);
        final Person raj = new Person(anAddress);

        final Person updatedRaj = personZipCode.set(personZipCode.get(raj) + "1", raj);
        assertEquals(updatedRaj.address.zipcode, aZipcode + "1");

        final Person modified = personAddress.modify(raj, address -> new Address(address.zipcode + "1"));
        assertEquals(modified.address.zipcode, aZipcode + "1");

        final fj.data.optic.Lens<Address, String> lens = fj.data.optic.Lens.lens(GET_ZIPCODE, SET_ZIPCODE2);
    }

    private static Lens<Person, Address> personAddressLens() {
        final F<Person, Address> addressGetter = person -> person.address;
        final F2<Address, Person, Person> addressSetterPerson = (address, person) -> new Person(address);
        return Lens.lens(addressGetter, addressSetterPerson);
    }

    @Value
    private static class Lens<A, B> {

        F<A, B> get;
        F2<B, A, A> set;

        public static <A, B> Lens<A, B> lens(final F<A, B> get, final F2<B, A, A> set) {
            return new Lens<>(get, set);
        }

        public B get(final A a) {
            return this.get.f(a);
        }

        public A set(final B b, final A a) {
            return this.set.f(b, a);
        }

        public A modify(final A a, final F<B, B> updateFunction) throws Exception {
            return set(updateFunction.f(get(a)), a);
        }

        public <C> Lens<A, C> then(final Lens<B, C> other) {
            return lens(F1Functions.andThen(this.get, other.get), setter(other));
        }

        private <C> F2<C, A, A> setter(final Lens<B, C> other) {
            return (c, a) -> set(other.set(c, get(a)), a);
        }
    }

    @Value
    private static class Address {
        String zipcode;
    }

    @Value
    private static class Person {
        Address address;
    }
}
