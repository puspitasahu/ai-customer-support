package com.dailywork.aicustomersupport.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

     @Entity
    @Getter
    @Setter
     @ToString
    public class Customer {
        @Id
        @GeneratedValue(strategy= GenerationType.IDENTITY)
        private Long id;
        private String fullName;
        private String emailAddress;
        private String phoneNumber;
        @OneToMany(mappedBy="customer",fetch=FetchType.LAZY)
        @ToString.Exclude
        private List<Conversation> conversation;

}


