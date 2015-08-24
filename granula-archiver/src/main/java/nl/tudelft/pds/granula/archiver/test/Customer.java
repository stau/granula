package nl.tudelft.pds.granula.archiver.test;


import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="Customer")
public class Customer {

    Customer parent;
    List<Customer> customers = new ArrayList<>();

    String name;
    int age;
    int id;

    public String getName() {
        return name;
    }

    public Customer getParent() {
        return parent;
    }

    public void setParent(Customer parent) {
        this.parent = parent;
    }

    @XmlElementWrapper
    public List<Customer> getCustomers() {
        return customers;
    }

    @XmlElement
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    @XmlElement
    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    @XmlAttribute
    public void setId(int id) {
        this.id = id;
    }

}
