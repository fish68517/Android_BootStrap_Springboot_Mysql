package com.example.orderfood.model;

public class Supplier {
    private String name;
    private String contactPerson;
    private String phone;
    private String address;

    public Supplier(String name, String contactPerson, String phone, String address) {
        this.name = name;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "供应商: " + name + '\n' +
               "联系人: " + contactPerson + '\n' +
               "电话: " + phone + '\n' +
               "地址: " + address;
    }
} 