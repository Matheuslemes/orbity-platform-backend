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
    private OffsetDateTime updatedAt;

    public Customer(CustomerId id, String sub, String email) {

        this.id = id;
        this.sub = trim(sub);
        this.email = trim(email);

    }

    public Customer(
            CustomerId id,
            String sub,
            String email,
            String firstName,
            String lastName,
            String phone,
            List<Address> addresses,
            Consent consent,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.sub = trim(sub);
        this.email = trim(email);
        this.firstName = trim(firstName);
        this.lastName = trim(lastName);
        this.phone = trim(phone);
        if (addresses != null) {
            this.addresses.addAll(addresses);
        }
        this.consent = consent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    public OffsetDateTime updatedAt() { return updatedAt; }

    public void updateProfile(String first, String last, String phone) {

        this.firstName = trim(first);
        this.lastName = trim(last);
        this.phone = trim(phone);
        touch();

    }

    public void setConsent(Consent c) { this.consent = c; touch(); }
    public void setTimestamps(OffsetDateTime c, OffsetDateTime u) { this.createdAt = c; this.updatedAt = u; }

    public void addOrReplaceAddress(Address a) {

        addresses.removeIf(x -> x.id().equals(a.id()));
        addresses.add(a);
        touch();

    }

    public void removeAddressById(UUID id) {

        addresses.removeIf(a -> a.id().equals(id));
        touch();

    }

    private void touch() { this.updatedAt = OffsetDateTime.now(); }

    private static String trim(String s) { return s == null ? null : s.trim(); }

    public CustomerId getId() { return id; }

    public String getSub() { return sub; }

    public String getEmail() { return email; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getPhone() { return phone; }

    public List<Address> getAddresses() { return addresses; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public Consent getConsent() { return consent; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

}
