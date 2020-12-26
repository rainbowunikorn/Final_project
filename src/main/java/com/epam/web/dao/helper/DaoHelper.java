package com.epam.web.dao.helper;

import com.epam.web.dao.connection.ConnectionPool;
import com.epam.web.dao.connection.ProxyConnection;
import com.epam.web.dao.book.BookDao;
import com.epam.web.dao.book.BookDaoImpl;
import com.epam.web.dao.order.OrderDtoDao;
import com.epam.web.dao.order.OrderDtoDaoImpl;
import com.epam.web.dao.user.UserDao;
import com.epam.web.dao.user.UserDaoImpl;
import com.epam.web.exception.ConnectionPoolException;
import com.epam.web.exception.DaoException;

import java.sql.SQLException;

public class DaoHelper implements AutoCloseable {

    private ProxyConnection connection;

    public DaoHelper(ConnectionPool pool) throws ConnectionPoolException {
        this.connection = pool.getConnection();
    }

    //all dao use one connection

    public UserDao createUserDao(){
        return new UserDaoImpl(connection);
    }

    public BookDao createBookDao(){return new BookDaoImpl(connection); }

    public OrderDtoDao createOrderDtoDao(){return new OrderDtoDaoImpl(connection); }

    public void startTransaction() throws DaoException {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public void commitTransaction() throws DaoException {
        try {
            connection.commit();
        } catch (SQLException commitException) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                throw new DaoException(rollbackException);
            }
        }
    }


    @Override
    public void close() throws SQLException, DaoException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DaoException(e);
            }
        }
    }
}
