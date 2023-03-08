package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AccountTest {

    @Test
    void getShortName() {
        var name = "Foo";
        var account = new Account(name);
        assertEquals("Foo", account.getShortName());

        name = "Foo:Bar";
        account = new Account(name);
        assertEquals("Bar", account.getShortName());

        name = "Foo:Bar:Baz";
        account = new Account(name);
        assertEquals("Baz", account.getShortName());
    }
}