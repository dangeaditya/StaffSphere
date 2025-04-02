package com.example.ems.Repository;

import com.example.ems.Entity.Emplyoee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpRepo extends JpaRepository<Emplyoee,Long>

{

    List<Emplyoee> findByName(String name);


}