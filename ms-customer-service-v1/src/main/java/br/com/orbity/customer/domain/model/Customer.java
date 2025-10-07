package br.com.orbity.customer.domain.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Customer {

    private final CustomerId id;
    private String sub;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private final List<Address> addresses = new ArrayList<>();
    private Consent consent;
    private OffsetDateTime createdAt;
    private OffsetDateTime updateAt;

    public Customer(CustomerId id, String sub, String email) {

        this.id = id;
        this.sub = trim(sub);
        this.email = trim(email);

    }

    public CustomerId id() { return id; }
    public String sub() { return sub; }
    public String email() { return email; }
    public List<Address> addresses() { return addresses; }
    public Consent consent() { return consent; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public String phone() { return phone; }
    public OffsetDateTime createdAt() { return createdAt; }
    public OffsetDateTime updatedAt() { return updateAt; }

    public void updateProfile(String first, String last, String phone) {

        this.firstName = trim(first);
        this.lastName = trim(last);
        this.phone = trim(phone);
        touch();

    }

    public void setConsent(Consent c) { this.consent = c; touch(); }
    public void setTimestamps(OffsetDateTime c, OffsetDateTime u) { this.createdAt = c; this.updateAt = u; }

    public void addOrReplaceAddress(Address a) {

        addresses.removeIf(x -> x.id().equals(a.id()));
        addresses.add(a);
        touch();

    }

    public void removeAddressById(UUID id) {

        addresses.removeIf(a -> a.id().equals(id));
        touch();

    }

    private void touch() { this.updateAt = OffsetDateTime.now(); }

    private static String trim(String s) { return s == null ? null : s.trim(); }
}
