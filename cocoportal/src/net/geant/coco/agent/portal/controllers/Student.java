package net.geant.coco.agent.portal.controllers;

public class Student {  
	 private int id;  
	 private String firstName;  
	 private String lastName;  
	 private String email;  
	 private String phone;  
	  
	 protected Student() {  
	 }  
	  
	 public Student(int id, String firstName, String lastName, String email,  
	   String phone) {  
	  this.id = id;  
	  this.firstName = firstName;  
	  this.lastName = lastName;  
	  this.email = email;  
	  this.phone = phone;  
	 }  
}
