package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {  // É IMPORTANTE IMPLEMENTAR O DAO DE INTERFACE, PORQUE SE A TECNOLOGIA MUDA DE MYSQL PRA SQL OU OUTRO BANCO, NÃO TEM ALTERAÇÕES
    void insert(Seller obj);
    void update(Seller obj);
    void deleteById(Integer id);
    Seller findById(Integer id);
    List<Seller> findAll();
    List<Seller> findByDepartment(Department department);
}