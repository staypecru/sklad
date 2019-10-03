package operations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction {
    private String name;
    private Integer cost;
    private Integer quantity;
    private Date date;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    public Transaction(String name, Integer cost, Integer quantity, Date date) {
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.date = date;
    }

    public Transaction(String name, Integer cost, Integer quantity, String date) throws ParseException {
        this.name = name;
        this.cost = cost;
        this.quantity = quantity;
        this.date = dateFormat.parse(date);
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "name='" + name + '\'' +
                ", cost=" + cost +
                ", quantity=" + quantity +
                ", date='" + date + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        Transaction transaction = (Transaction) o;
        return this.getName().equals(transaction.getName()) &&
                this.getCost().equals(transaction.getCost()) &&
                this.getQuantity().equals(transaction.getQuantity()) &&
                this.getDate().getDay() == transaction.getDate().getDay() &&
                this.getDate().getMonth() == transaction.getDate().getMonth() &&
                this.getDate().getYear() == transaction.getDate().getYear();
    }

}
