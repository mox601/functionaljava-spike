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
    }

    private static Lens<Address, String> addressStringLens() {
        final F<Address, String> getZipcode = address -> address.zipcode;
        final F2<String, Address, Address> setZipcode = (s, address) -> new Address(s);
        return Lens.lens(getZipcode, setZipcode);
    }

    private static Lens<Person, Address> personAddressLens() {
        final F<Person, Address> addressGetter = person -> person.address;
        final F2<Address, Person, Person> addressSetterPerson = (address, person) -> new Person(address);
        return Lens.lens(addressGetter, addressSetterPerson);
    }

    private static class Lens<A, B> {

        private final F<A, B> get;

        private final F2<B, A, A> set;

        private Lens(F<A, B> get, F2<B, A, A> set) {

            this.get = get;

            this.set = set;
        }

        public static <A, B> Lens<A, B> lens(F<A, B> get, F2<B, A, A> set) {

            return new Lens<>(get, set);
        }

        public B get(A a) {

            return this.get.f(a);
        }

        public A set(B b, A a) {

            return this.set.f(b, a);
        }

        public A modify(A a, F<B, B> updateFunction) throws Exception {

            return set(updateFunction.f(get(a)), a);
        }

        public <C> Lens<A, C> then(Lens<B, C> other) {

            return lens(F1Functions.andThen(this.get, other.get), setter(other));
        }

        private <C> F2<C, A, A> setter(final Lens<B, C> other) {

            return (c, a) -> set(other.set(c, get(a)), a);
        }

    }

    @Value
    private static class Address {
        private final String zipcode;
    }

    @Value
    private static class Person {
        private final Address address;
    }
}
