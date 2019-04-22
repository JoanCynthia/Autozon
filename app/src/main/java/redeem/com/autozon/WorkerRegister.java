package redeem.com.autozon;

public class WorkerRegister
{
    private String image, email, password, phoneNumber, adminPhone;

    public WorkerRegister(String image, String email, String password, String phoneNumber, String adminPhone) {
        this.image = image;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.adminPhone = adminPhone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }
}
