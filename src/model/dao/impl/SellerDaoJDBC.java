package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;

    // Deixar o conn a disposição em qualquer lugar da classe
    public SellerDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("INSERT INTO seller " 
                    + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
                    + "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getNome());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getCodigo());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();

                if (rs.next()) {
                    int id = rs.getInt(1);
                    obj.setCodigo(id); // já deixo o obj populado com o id
                    DB.closeResultSet(rs);
                }
                else {
                    throw new DbException("Unexpected error! No rows affected");
                }
            }
            
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }

    }

    @Override
    public void update(Seller obj) {
        PreparedStatement st = null;

        try {
            st = conn.prepareStatement("UPDATE seller "
                                        + "SET Name = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
                                        + "WHERE Id = ? ");
            
            st.setString(1, obj.getNome());
            st.setDate(2, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(3, obj.getBaseSalary());
            st.setInt(4, obj.getDepartment().getCodigo());
            st.setInt(5, obj.getCodigo());

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
            st = conn.prepareStatement("DELETE FROM seller WHERE id = ?");

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
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
                                        + "FROM seller INNER JOIN department "
                                        + "ON seller.DepartmentId = department.Id "
                                        + "WHERE seller.Id = ?");
            
            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) {

                Department dep = instantiateDepartment(rs);
                Seller seller = instatiateSeller(rs, dep);
            
                return seller;
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st); // posso fechar no programa principal se for executar outras queries
            DB.closeResultSet(rs);
        }
    }

    private Seller instatiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller seller = new Seller(rs.getInt("Id"), 
                                        rs.getString("Name"),
                                        rs.getString("Email"),
                                        new java.util.Date(rs.getTimestamp("BirthDate").getTime()),
                                        rs.getDouble("BaseSalary"),
                                        dep);
        return seller;
    }

    public Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department(rs.getInt("DepartmentId"),
                                        rs.getString("DepName"));
        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
                                        + "FROM seller INNER JOIN department "
                                        + "ON seller.DepartmentId = department.Id "
                                        );
             
            rs = st.executeQuery();
            
            List<Seller> sellerList = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId")); // primeira vez não existe

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                };

                sellerList.add(instatiateSeller(rs, dep));
                
             }

             return sellerList;

       } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st); // posso fechar no programa principal se for executar outras queries
            DB.closeResultSet(rs);
        }
         
     }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement("SELECT seller.*, department.Name as DepName "
                                        + "FROM seller INNER JOIN department "
                                        + "ON seller.DepartmentId = department.Id "
                                        + "WHERE department.Id = ?");
             
            st.setInt(1, department.getCodigo());
            rs = st.executeQuery();
            
            List<Seller> sellerList = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>();

            while (rs.next()) {
                Department dep = map.get(rs.getInt("DepartmentId")); // primeira vez não existe

                if (dep == null) {
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"), dep);
                };

                sellerList.add(instatiateSeller(rs, dep));
                
             }

             return sellerList;

       } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st); // posso fechar no programa principal se for executar outras queries
            DB.closeResultSet(rs);
        }
         
    }
    
}