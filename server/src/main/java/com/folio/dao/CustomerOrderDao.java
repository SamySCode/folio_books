package com.folio.dao;

import com.folio.model.CustomerOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerOrderDao {

    private JdbcTemplate jdbcTemplate;

    public CustomerOrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createCustomerOrder(CustomerOrder customerOrder) {
        // Fetch the max customer order ID and increment by 1 to create the new ID
        int lastCustomerOrderId = getLastCustomerOrderId();
        int newCustomerOrderId = lastCustomerOrderId + 1;

        // Prepare the SQL INSERT statement
        String sql = "INSERT INTO customer_order (id, customer_id, status) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, newCustomerOrderId, customerOrder.getCustomer_id(), customerOrder.getStatus_());
    }

    private int getLastCustomerOrderId() {
        String sql = "SELECT MAX(id) FROM customer_order";
        Integer maxId = jdbcTemplate.queryForObject(sql, Integer.class);
        if (maxId == null) {
            return 0;
        } else {
            return maxId;
        }
    }

    public CustomerOrder getCustomerOrderById(int id) {
        String sql = "SELECT * FROM customer_order WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new CustomerOrderMapper(), id);
    }

    public List<CustomerOrder> getCustomerOrdersByCustomerId(int customerId) {
        String sql = "SELECT * FROM customer_order WHERE customer_id = ?";
        return jdbcTemplate.query(sql, new CustomerOrderMapper(), customerId);
    }

    public CustomerOrder getBasketCustomerOrdersByCustomerId(int customerId) {
        String sql = "SELECT * FROM customer_order WHERE customer_id = ? AND status_ = ?";
        return jdbcTemplate.queryForObject(sql, new CustomerOrderMapper(), customerId, "Basket");
    }

    public void updateCustomerOrder(CustomerOrder customerOrder) {
        String sql = "UPDATE customer_order SET customer_id = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql, customerOrder.getCustomer_id(), customerOrder.getStatus_(), customerOrder.getId());
    }

    public void deleteCustomerOrder(int id) {
        String sql = "DELETE FROM customer_order WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class CustomerOrderMapper implements RowMapper<CustomerOrder> {
        @Override
        public CustomerOrder mapRow(ResultSet resultSet, int i) throws SQLException {
            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setId(resultSet.getInt("id"));
            customerOrder.setCustomer_id(resultSet.getInt("customer_id"));
            customerOrder.setStatus_(resultSet.getString("status"));
            return customerOrder;
        }
    }
}