/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.modules.mdm.tools;

/**
 *
 * @author govind
 */
public class AddressCountDto {

    String address;
    Long count;

    public AddressCountDto(Long count, String address) {
        this.count = count;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
