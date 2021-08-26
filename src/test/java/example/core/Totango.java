package example.core;

import example.data.TolkienCharacter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static example.data.Race.DWARF;
import static example.data.Race.ELF;
import static example.data.Race.HOBBIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.in;
import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.assertj.core.api.WithAssertions;

// Assertj is that it makes code cleaner, more readable.
//
// Let's go to the Robert Martin's book "Clean code"

public class Totango {

  @Test void describe_assertion() {
    TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, HOBBIT);
    // Junit
    // Dyadic and triadic functions
    // Where is expected and where is actual?
    assertEquals(String.format("Check %s's age", frodo.getName()), frodo.getAge(),
        Integer.valueOf(100));

    // Monadic function
    assertThat(frodo.getAge()).as("Check %s's age", frodo.getName()).isEqualTo(100);

    // The main benefit of AssertJ is that it is set up to provide auto-completion in IDEs.
    // Once you have specified the object you want to compare (using assertThat):
    // the IDE will understand the type of the comparison (in this case Object)
    // and will show you all available assertions.
    assertThat(frodo.name).isNotBlank();
  }

  @Test void chain_assertions() {
    //  each assertion method returns a reference to the used “assertion object”.
    //  This means that we can chain assertions by simply invoking another assertion method.
    assertThat("The Lord of the Rings").isNotNull()  // <2> <3>
        .startsWith("The") // <4>
        .contains("Lord") // <4>
        .endsWith("ing"); // <4>
  }

  @Test void list() {
    // Go to image with collections assertions

    // Intersection of Two Lists
    TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, HOBBIT);
    TolkienCharacter aragorn = new TolkienCharacter("Aragorn", 70, ELF);
    TolkienCharacter gala = new TolkienCharacter("Gala", 12, DWARF);

    List<TolkienCharacter> longList = Arrays.asList(frodo, gala, aragorn);

    // Junit
    List<TolkienCharacter> shortList = Arrays.asList(frodo, gala);

    assertFalse(longList.stream().distinct().filter(shortList::contains).collect(Collectors.toSet())
        .isEmpty());

    // AssertJ
    assertThat(longList).containsAnyOf(frodo, gala);
  }

  @Test public void testTolkienCharacterArrayList() {
    // Filtering
    ArrayList<TolkienCharacter> actualList = new ArrayList<>();
    TolkienCharacter frodo = new TolkienCharacter("Frodo", 32, HOBBIT);
    TolkienCharacter aragorn = new TolkienCharacter("Aragorn", 62, HOBBIT);
    TolkienCharacter sam = new TolkienCharacter("Sam", 36, HOBBIT);
    actualList.add(frodo);
    actualList.add(aragorn);
    actualList.add(sam);

    // JUNIT
    // I have to create expected list and filter actual list using stream API
    List<TolkienCharacter> expected = new ArrayList<>();
    expected.add(frodo);
    expected.add(aragorn);

    List<TolkienCharacter> filteredList =
        actualList.stream().filter((character) -> character.name.contains("o"))
            .collect(Collectors.toList());

    assertEquals(expected, filteredList);

    // ASSERTJ
    // I can do the same in one line. Collecting is done under the hood
    assertThat(actualList).filteredOn(character -> character.name.contains("o"))
        .containsOnly(aragorn, frodo);

  }

  @Test void filter() {
    TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, HOBBIT);
    TolkienCharacter aragorn = new TolkienCharacter("Aragorn", 70, ELF);
    TolkienCharacter gala = new TolkienCharacter("Gala", 12, DWARF);
    List<TolkienCharacter> fellowshipOfTheRing = Arrays.asList(frodo, gala, aragorn);

    // Useful in(), notIn() for filtering based on that value matches
    // any of the given values
    assertThat(fellowshipOfTheRing).filteredOn("race", in(DWARF, ELF)).containsOnly(aragorn, gala);
  }

  @Test void extract() {
    // When we want to check specific field in object
    TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, HOBBIT);
    TolkienCharacter aragorn = new TolkienCharacter("Aragorn", 70, ELF);
    TolkienCharacter gala = new TolkienCharacter("Gala", 12, DWARF);
    List<TolkienCharacter> fellowshipOfTheRing = Arrays.asList(frodo, gala, aragorn);

    // "name" needs to be either a property or a field of the TolkienCharacter class

//     fellowshipOfTheRing.stream().map(TolkienCharacter::getName)
    assertThat(fellowshipOfTheRing).extracting(TolkienCharacter::getName)
        .contains("Frodo", "Aragorn", "Gala").doesNotContain("Sauron", "Elrond");
  }

  @Test public void testExpectedIOException() {
    assertThatThrownBy(() -> {
      throw new IOException("Throwing a new Exception");
    }).isInstanceOf(IOException.class).hasMessage("Throwing a new Exception");
  }

  @Test void throw_assertions() {
    Throwable throwable = new IllegalArgumentException("wrong amount 123");

    // Different ways to check exception message :
    assertThat(throwable).hasMessage("wrong amount 123").hasMessage("%s amount %d", "wrong", 123)
        // check start
        .hasMessageStartingWith("wrong").hasMessageStartingWith("%s a", "wrong")
        // check content
        .hasMessageContaining("wrong amount").hasMessageContaining("wrong %s", "amount")
        .hasMessageContainingAll("wrong", "amount")
        // check end
        .hasMessageEndingWith("123").hasMessageEndingWith("amount %s", "123")
        // check with regex
        .hasMessageMatching("wrong amount .*")
        // check does not contain
        .hasMessageNotContaining("right").hasMessageNotContainingAny("right", "price");

    //  another way to assert throwing an exception
    assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
      throw new RuntimeException(new IllegalArgumentException("boom!"));
    }).havingCause().withMessage("boom!");
  }

  @Test void throwable_check_cause() {

    // Check the cause of throwable
    NullPointerException cause = new NullPointerException("boom!");
    Throwable throwable = new Throwable(cause);
    assertThat(throwable).hasCause(cause)
        // hasCauseInstanceOf will match inheritance.
        .hasCauseInstanceOf(NullPointerException.class).hasCauseInstanceOf(RuntimeException.class)
        // hasCauseExactlyInstanceOf will match only exact same type
        .hasCauseExactlyInstanceOf(NullPointerException.class);
  }


  @Test public void withAssertions_examples() {
    // AssertJ provides other entry points class,
    // BDDAssertions for BDD style assertions that replace assertThat by then
    TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, HOBBIT);

    assertThat(frodo.age).isEqualTo(33);
    assertThat(frodo.getName()).isEqualTo("Frodo").isNotEqualTo("Frodon");

    assertThat(frodo).matches(p -> p.age > 30 && p.getRace() == HOBBIT);
    assertThat(frodo.age).matches(p -> p > 30);

    // then methods come from BDDAssertions.then static allows Given-when-then template
    then(frodo.age).isEqualTo(33);
    then(frodo.getName()).isEqualTo("Frodo").isNotEqualTo("Frodon");

    then(frodo).matches(p -> p.age > 30 && p.getRace() == HOBBIT);
    then(frodo.age).matches(p -> p > 30);
  }

  public class Person {
    String name;
    double height;
    Home home = new Home();

    public Person(String name, double height) {
      this.name = name;
      this.height = height;
    }
  }


  public class Home {
    Address address = new Address();
    Date ownedSince;
  }


  public static class Address {
    int number;
    String street;
  }

  @Test public void usingRecursiveComparison() {


    Person sherlock = new Person("Sherlock", 1.80);
    sherlock.home.ownedSince = new Date(123);
    sherlock.home.address.street = "Baker Street";
    sherlock.home.address.number = 221;

    Person sherlock2 = new Person("Sherlock", 1.80);
    sherlock2.home.ownedSince = new Date(123);
    sherlock2.home.address.street = "Baker Street";
    sherlock2.home.address.number = 221;

    // assertion succeeds as the data of both objects are the same.
    assertThat(sherlock).usingRecursiveComparison().isEqualTo(sherlock2);

    // assertion fails as Person equals only compares references.
    assertThat(sherlock).isEqualTo(sherlock2);
  }

}
// end::user_guide[]
