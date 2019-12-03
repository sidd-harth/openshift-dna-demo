package com.openshift.jenkins;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openshift.jenkins.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
