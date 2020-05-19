package dao.user;

import vo.Customer;

import java.util.List;

public interface IUserDAO {
    public String authenticateUser(String userName, String password);
    public boolean addCustomer(Customer customer) ;
    public boolean checkIfUserNameExists(String userName) ;
    public String getArchitectForClient(String clientName);
    public List<String> getCustomersForArchitect(String architectName);
}
