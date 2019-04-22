package redeem.com.autozon;

public class Compliance
{
    String service,product_name, status, remarks;
    String phone, address;
    String ticket_id;
    public Compliance(String phone, String address) {
        this.phone = phone;
        this.address = address;
//        this.ticket = ticket;
    }

    public Compliance(String service, String product_name, String status, String remarks) {
        this.service = service;
        this.product_name = product_name;
        this.status = status;
        this.remarks = remarks;
    }

    public Compliance(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

//    public String getTicket() {
//        return ticket;
//    }
//
//    public void setTicket(String ticket) {
//        this.ticket = ticket;
//    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }
}
