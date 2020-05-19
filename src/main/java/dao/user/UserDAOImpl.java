package dao.user;

import dao.DBUtil;
import org.apache.log4j.Logger;
import util.ApplicationException;
import util.AuditLogger;
import vo.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements IUserDAO {

    static final Logger LOGGER = Logger.getLogger("applicationError");

    @Override
    public String authenticateUser(String userName, String password)  {
        String userRole = "NOT_FOUND";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select Role from UserMaster where Username='"+userName+"' and Password='"+password+"'");
            while (resultSet.next()) {
                userRole= resultSet.getString(1);
            }
           return userRole;
        } catch (SQLException e) {
            AuditLogger.auditLog(userName,"Login", "Login failure due to SQL Exception");
            LOGGER.error("In UserDAOImpl.authenticateUser : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in UserDAOImpl.authenticateUser :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet,statement,connection);
        }
    }



    @Override
    public boolean addCustomer(Customer customer)  {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement("Insert into UserMaster(Name, UserName, Country, Address, Phone) values(? , ?, ?, ?, ?)");
            preparedStatement.setString(1,customer.getCustomerName());
            preparedStatement.setString(2,customer.getCustomerEmail());
            preparedStatement.setString(3, customer.getCustomerCountry());
            preparedStatement.setString(4, customer.getCustomerAddress());
            preparedStatement.setString(5, customer.getCustomerPhone());
            return preparedStatement.execute();
        } catch (SQLException sqlException) {
            AuditLogger.auditLog("","Add Customer", "Add customer failure due to SQL Exception - customer email :"+customer.getCustomerEmail());
            LOGGER.error("In UserDAOImpl.addCustomer : SQL exception encountered ", sqlException);
            throw new ApplicationException("SQL Exception encountered in UserDAOImpl.addCustomer :"+sqlException.getMessage());
        } finally {
            DBUtil.closeResources(preparedStatement,connection);
        }
    }

    @Override
    public boolean checkIfUserNameExists(String userName)  {
        boolean userNameExists;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DBUtil.getConnection();
            preparedStatement = connection.prepareStatement("Select count(1) from UserMaster where UserName = ?");
            preparedStatement.setString(1,userName);
            userNameExists = false;
            preparedStatement.executeQuery();
            while ( resultSet.next() ) {
                if (resultSet.getInt(1) > 0) {
                    userNameExists = true;
                }
            }
            return userNameExists;
        } catch (SQLException sqlException) {
            AuditLogger.auditLog("","Check Customer", "Check customer failure due to SQL Exception - customer :"+userName);
            LOGGER.error("In UserDAOImpl.checkIfUserNameExists : SQL exception encountered ", sqlException);
            throw new ApplicationException("SQL Exception encountered in UserDAOImpl.checkIfUserNameExists :"+sqlException.getMessage());
        } finally {
            DBUtil.closeResources(resultSet,preparedStatement,connection);
        }

    }

    @Override
    public String getArchitectForClient(String clientName)  {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String architectName=null;
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select Architect from ArchitectToClientMapping where Client='"+clientName+"'");
            while (resultSet.next()) {
                architectName= resultSet.getString(1);
            }
            return architectName;
        } catch (SQLException e) {
            AuditLogger.auditLog(clientName,"Get Architect", "Failed to retrieve architect due to SQL Exception ");
            LOGGER.error("In UserDAOImpl.getArchitectForClient : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in UserDAOImpl.getArchitectForClient :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet, statement, connection);
        }
    }


    public List<String> getCustomersForArchitect(String architectName)  {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<String> customerList = new ArrayList<>();
        try {
            connection = DBUtil.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("Select client from ArchitectToClientMapping where Architect='" + architectName + "'");
            while (resultSet.next()) {
                customerList.add(resultSet.getString(1));
            }
            return customerList;
        } catch (SQLException e) {
            AuditLogger.auditLog(architectName,"Get Customers", "Failed to retrieve customers due to SQL Exception ");
            LOGGER.error("In UserDAOImpl.getCustomersForArchitect : SQL exception encountered ", e);
            throw new ApplicationException("SQL Exception encountered in UserDAOImpl.getCustomersForArchitect :"+e.getMessage());
        } finally {
            DBUtil.closeResources(resultSet, statement, connection);
        }
    }    


}
