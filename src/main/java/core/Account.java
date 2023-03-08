package core;

import java.util.Objects;

/**
 * Represents an account.
 */

public class Account {
    private final String name;
    private final String shortName;

    public Account(String name) {
        this.name = name;

        var lastIndex = name.lastIndexOf(":");
        this.shortName = (lastIndex != -1) ? name.substring(lastIndex + 1) : name;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(name, account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                '}';
    }
}
