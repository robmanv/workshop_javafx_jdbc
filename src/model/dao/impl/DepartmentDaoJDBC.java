package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("INSERT INTO department "
                                     + "(Id, Name) "
                                     + "VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, obj.getCodigo());
            st.setString(2, obj.getNome());
            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();

                if (rs.next()) {
                    obj.setCodigo(rs.getInt(1));
                    DB.closeResultSet(rs);
                }
            }


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Department obj) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("UPDATE department "
                                        + "SET Name = ? "
                                        + "WHERE Id = ? ");
            
            st.setString(1, obj.getNome());
            st.setInt(2, obj.getCodigo());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("DELETE FROM department WHERE id = ?");

            st.setInt(1, id);
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("Não existe id para delete");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        Department dep = null;

        try {
            st = conn.prepareStatement("SELECT Id, Name FROM department "
                                      + "WHERE Id = ?");
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {
                dep = new Department(rs.getInt(1), rs.getString(2));
            }
            
            return dep;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        List<Department> list = new ArrayList<>();

        try {
            st = conn.prepareStatement("SELECT Id, Name FROM department");
            rs = st.executeQuery();

            while (rs.next()) {
                list.add(instantiateDepartment(rs));
            }
            
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
        }
       
    }

    public Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department(rs.getInt("Id"),
                                        rs.getString("Name"));
        return dep;
    }
    
}