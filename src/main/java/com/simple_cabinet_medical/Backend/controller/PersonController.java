package com.simple_cabinet_medical.Backend.controller;

import com.simple_cabinet_medical.Backend.model.Person;
import com.simple_cabinet_medical.Backend.repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;


@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonRepo personRepo;


    @GetMapping("/")
    public String test () {
        return "hello world";
    }

    @GetMapping("/all")
    public ArrayList<Person> findAllPersons () {
        ArrayList persons = new ArrayList();
        persons.add(new Person(45, "islem"));
        return persons;
    }


    @PostMapping("/add")
    public String createPerson () {
        System.out.println ("add person endpoint called");
        return "person added";
    }
}
